package nTupleTD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import adaptableLearningRates.ActivationFunction.Activation;
import tools.CompareDouble;
import tools.Function;
import tools.Function.FunctionScheme;
import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateSnapshot;
import dotsAndBoxes.IAgent;
import dotsAndBoxes.Move;
import dotsAndBoxes.MoveValue;

/**
 * Implementation of the Temporal Difference Learning Algorithm for
 * Dots-And-Boxes.
 * 
 * @author Markus Thill
 * 
 */
public class TDLearning implements IAgent, Serializable {
	private static final long serialVersionUID = 6476048357272010654L;

	private static final Random RND = tools.SRandom.RND;

	private static final boolean DEBUG = false;

	/**
	 * The values for different actions from a certain game-state will never be
	 * exactly the same. Therefore, define an epsilon for equality-checks. The
	 * values of two actions are considered to be the same, if it is |value1 -
	 * value2| <= EPS. This constant is needed in order to select a list of best
	 * moves.
	 */
	private static final double BEST_MOVES_EPS = 0.01;

	/**
	 * General Training-parameters. Check the class-definition for further
	 * details: {@link TrainingParams}.
	 */
	protected final TrainingParams par;

	/**
	 * Underlying Value function of the TD-Algorithm. The TD-algorithm attempts
	 * to learn the real Value-Function V*(s) by approximating it with this
	 * function V(s). In this case, the value-function is basically a n-tuple
	 * system.
	 */
	private final ValueFunction v;

	// private GameState s;

	/**
	 * Exploration rate of the learning-process. The value is supposed to be in
	 * the range [0,1]. A value of zero indicates that there is no exploration,
	 * whereas a value of one would indicate that all moves performed are
	 * random.
	 */
	private Function epsilon;

	/**
	 * Number of training-games performed.
	 */
	private int gameNum;

	/**
	 * Initialize a new TD-Learning object.
	 * 
	 * @param par
	 *            Training parameters required for general purposes, for the
	 *            TD-Learning algorithm and the n-tuple system.
	 */
	public TDLearning(TrainingParams par) {
		this.par = par;
		v = new ValueFunction(par);

		reinitialize();
	}

	/**
	 * Reintialize the training. The exploration rate and the global step-size
	 * parameter alpha will be reset to the initial values and the counter for
	 * the training-games will be reset. Note, that the underlying
	 * value-function will not be touched.
	 */
	public void reinitialize() {
		gameNum = 0;
		initEpsilon();
	}

	/**
	 * Selects an after-state randomly or based on the response of the
	 * value-function.
	 * 
	 * @param s
	 *            The game-state that will be advanced.
	 * @param random
	 *            True, if the possibility for a random move shall be given.
	 *            Note however, that a random move is not necessarly performed
	 *            if the parameter is set to true. A random after-state is only
	 *            selected if random == true and at a probability epsilon
	 * @return true, if the after-state was selected randomly, otherwise false.
	 */
	private boolean advance(GameState s, boolean random) {
		boolean randomSelect;

		// determine if a random after-state should be selected
		randomSelect = (random ? RND.nextDouble() < epsilon.f(gameNum) : false);

		if (randomSelect)
			s.advance(true);
		else {
			// Advance to the best move found (Greedy move).
			Move bestMv = getBestMove(s);
			s.advance(bestMv);
		}
		return randomSelect;
	}

	/**
	 * Implementation of the TD-Learning algorithm. It follows closely the
	 * pseudo-code given by most authors. calling this method performs one
	 * training-game. In pratice, this method has to be called many times in
	 * order to make the value-function converge towards the real values.
	 */
	public void trainNet() {
		// TODO: Some things todo here for elig-traces and more...

		// Reset the game-state, create a new empty board
		// initGameState();
		GameState s = new GameState(par.m, par.n, DEBUG);

		//
		// Set up the eligibility traces
		//
		if(par.tdPar.lambda != 0.0)
			v.resetEligibilityTraces(s);

		//
		// Perform one training-game. Update the value-function after every
		// non-random action, or if a terminal state is reached.
		//
		boolean isTerminal = false;
		while (!isTerminal) {
			// Save the current game-state s_t. Only a snapshot is needed.
			GameStateSnapshot last = s.getGameStateSnapshot();

			// Advance to state s_{t+1} and allow random moves. Typically,
			// greedy moves will be performed.
			boolean isRandom = advance(s, true);

			// Check, if we reached a terminal (final) state
			isTerminal = s.isTerminal();

			// Get reward
			double reward = getReward(last, s);

			// Update the value-function, if the after-state was not selected
			// randomly or if we reached a terminal state
			if (!isRandom || isTerminal) {
				v.updateValueFunction(gameNum, last, s, isTerminal, reward);
			}

			boolean resettingTraces = par.tdPar.resettingTraces;
			if (!isRandom || !resettingTraces) {
				if (par.tdPar.lambda != 0.0)
					v.updateEligibilityTraces(s);
			} else if (par.tdPar.lambda != 0.0) {
				//
				// If we made a random move, we have to reset the
				// eligibility traces. Simply set the eligibity-tace vector to
				// the gradient of the new state (s_{t+1})
				//
				v.resetEligibilityTraces(s);
			}
		}

		// updateTrainingParameters();
		gameNum++;

	}

	public long estimateSizeInBytes() {
		return 1000000; // TODO:....
	}

	// /**
	// * Update the training-parameters such as the exploration-rate epsilon and
	// * the global step-size parameter alpha.
	// */
	// private void updateTrainingParameters() {
	// updateEpsilon();
	// // v.updateGlobalAlpha(gameNum, par.numGames);
	// }

	/**
	 * Update the exploration rate.
	 */
	private void initEpsilon() {
		TDParams td = par.tdPar;
		double e0 = td.epsilonInit; // Initial exploration-rate
		double eN = td.epsilonFinal; // Final exploration-rate
		double N = par.numGames; // Total number of training-games
		FunctionScheme a = td.epsilonAdjust; // how epsilon is adjusted
		double[] coeff = td.epsilonAdjustCoeff; // If some coeff. are required
		epsilon = new Function(e0, eN, N, a, coeff);
	}

	/**
	 * According to the game-state s_t and s_{t+1}, determine the corresponding
	 * reward. If the option {@link TDParams#useImmediateRewards} is selected,
	 * the environment will always return a reward unequal zero if a box is
	 * captured by either players. If player A captures a box (or two) the
	 * reward will be positive, for player B the reward will be negative in this
	 * case. Note, that the TD-Learning algorithm will attempt to learn the
	 * future captures starting from a game-state s with this variant. <br>
	 * If immediate rewards are not allowed, then the value for the reward can
	 * only then be unequal zero, if we reached a final state. Note, that this
	 * variant will cause the TD-learning algorithm to learn to predict the
	 * outcome of the game in sense of win/draw/loss.
	 * 
	 * @param lastS
	 *            The game-state s_t.
	 * @param s
	 *            The game-state s_{t+1}.
	 * @return The reward of the environment for the agent.
	 */
	private double getReward(GameStateSnapshot lastS, GameStateSnapshot s) {
		double r;
		boolean immediateReward = par.tdPar.useImmediateRewards;
		Activation a = par.tdPar.activation;
		if (immediateReward) {
			//
			// The rewards depends on how many boxes were captured. If no boxes
			// were captured from s_t to s_{t+1}, then the reward is zero. If
			// one or two boxes were captured by player A, then the reward will
			// be positive (+1 or +2); accordingly, for player B, the reward
			// will
			// be negative.
			//
			double lastBoxdiff = lastS.getBoxDifference();
			double boxDiff = s.getBoxDifference();
			r = boxDiff - lastBoxdiff;

			//
			// If we have an immediate reward and we are using an activation
			// function, then we have to scale the reward into the range -1 to
			// +1 (adjusting the reward for a log. sigmoid function is done in
			// another place.)
			//
			if (a != Activation.NONE) {
				return r / (par.m * par.n);
			}
		} else {
			//
			// If we do not have immediate rewards, we only get the reward at
			// the end of the game from the environment. In all other cases the
			// reward is zero.
			//
			r = (s.isTerminal() ? s.getWinner() : 0.0);
		}
		return r;
	}

	/**
	 * @return The number of training-games already performed.
	 */
	public int getGameNum() {
		return gameNum;
	}

	public double getEpsilon() {
		return epsilon.f(gameNum);
	}

	public double getGlobalAlpha() {
		return v.getGlobalAlpha(gameNum);
	}

	public TrainingParams getTrainingParams() {
		return par;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dotsAndBoxes.IAgent#getBestMove(dotsAndBoxes.GameState)
	 */
	@Override
	public Move getBestMove(GameState s) {
		Move bestMv = null;
		double bestValue = Double.NEGATIVE_INFINITY;
		double p = s.getPlayerToMove();

		//
		// First get a list of action-values
		//
		List<MoveValue> actionValues = getActionValues(s);

		//
		// Find the actions with the highest value
		//
		// Iterator<MoveValue> i = actionValues.iterator();
		// while (i.hasNext()) {
		// MoveValue mvv = i.next();
		for (MoveValue mvv : actionValues) {
			if (p * mvv.value > bestValue) {
				bestValue = p * mvv.value;
				bestMv = mvv.mv;
			}
		}
		return bestMv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dotsAndBoxes.IAgent#getBestMoves(dotsAndBoxes.GameState)
	 */
	@Override
	public List<Move> getBestMoves(GameState s) {
		ArrayList<Move> bestMV = new ArrayList<Move>();
		double bestValue = Double.NEGATIVE_INFINITY;
		double p = s.getPlayerToMove();

		//
		// First get a list of action-values
		//
		List<MoveValue> actionValues = getActionValues(s);

		//
		// Find the actions with the highest value
		//
		// Iterator<MoveValue> i = actionValues.iterator();
		// while (i.hasNext()) {
		// MoveValue mvv = i.next();
		for (MoveValue mvv : actionValues) {
			if (CompareDouble.equals(mvv.value, bestValue, BEST_MOVES_EPS)) {
				bestMV.add(mvv.mv);
			} else if (p * mvv.value > bestValue) {
				bestValue = p * mvv.value;
				bestMV.clear();
				bestMV.add(mvv.mv);
			}
		}

		return bestMV;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dotsAndBoxes.IAgent#getValue(dotsAndBoxes.GameState)
	 */
	@Override
	public double getValue(GameState s) {
		// TODO: In C4 we have a MiniMax-version of this function with a n-ply
		// lookahead. Do we really need this here? If yes, implement an
		// AlphaBeta-Search instead of Minimax, since the branching factor is
		// crazy here and Minimax will not work even for small search depths.
		// Check, if state is a terminal state first
		if (s.isTerminal()) {
			//
			// If we do not use immediate rewards, then the value is simply the
			// winner of the game.
			//
			if (!par.tdPar.useImmediateRewards)
				return s.getWinner();
			//
			// If we have immediate rewards, then the value is the number of
			// boxes captured with the last move (positive, if player A captured
			// them, otherwise negative).
			// TODO:

		}

		// Retrieve value from n-tuple-system
		return v.getValue(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dotsAndBoxes.IAgent#getActionValues(dotsAndBoxes.GameState)
	 */
	@Override
	public List<MoveValue> getActionValues(GameState s) {
		ArrayList<MoveValue> list = new ArrayList<MoveValue>(s.actionsLeft());

		// Try all moves, and return a list of moves with the corresponding
		// values
		//
		// Iterator<Move> i = s.iterator();
		// while (i.hasNext()) {
		for (Move mv : s) {
			// Move mv = i.next();
			s.advance(mv);
			double value = getValue(s);
			s.undo();
			list.add(new MoveValue(mv, value));
		}

		return list;
	}
}
