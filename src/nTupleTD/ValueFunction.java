package nTupleTD;

import java.io.Serializable;

import tools.Function;
import tools.Function.FunctionScheme;

import adaptableLearningRates.ActivationFunction;
import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateSnapshot;

//TODO: Try both possibilities: board-inversion and 2 separate value functions for A and B...
// Advantages for board-inversion: Since we always learn player A, we should learn faster.. Disadvantage: Maybe the state of an n-tuple really depends on which players move it is

/**
 * Implementation of a value function based on an n-tuple system. This
 * value-function is designed to be trained with the TD-learning algorithm.
 * 
 * @author Markus Thill
 * 
 */
public class ValueFunction implements Serializable {
	private static final long serialVersionUID = 8123554926925519297L;

	// private double globalAlpha;

	/**
	 * Contains many pre-defined parameters, that are required by the
	 * TD-Learning algorithm and this underlying value-function. For details
	 * refer to the class {@link TDParams}.
	 */
	private final TDParams tdPar;

	/**
	 * The n-tuple system that is used to set up this value-function. The
	 * n-tuple system typically contains a whole set of look-up tables that
	 * contain the single weights of the value-function. Note that the n-tuple
	 * system basically represents a linear value function.
	 */
	private NTupleNet nTupleNet;

	private Function globalAlpha;

	/**
	 * Constructor for a new Value-function. The constructor sets up the n-tuple
	 * system and initializes the global step-size parameter alpha.
	 * 
	 * @param par
	 *            The training parameters.
	 */
	ValueFunction(TrainingParams par) {
		tdPar = par.tdPar;
		nTupleNet = new NTupleNet(par);

		initGlobalAlpha(par);
	}

	/**
	 * Update the value function and therefore the weights of the system. Based
	 * on the state s_t and the selected after-state s_{t+1} the error-signal
	 * delta is determined and with this signal the weights activated by s_t are
	 * updated. This update-rule basically follows the standard update-rule for
	 * TD-Learning:
	 * <code>V(s_t) <- V(s_t) + alpha * [r_{t+1} + gamma * V(s_{t+1} -
	 * V(s_t)]</code>, where alpha represents the global step-size parameter, r
	 * represents the reward and gamma the so called discount factor.
	 * 
	 * @param curS
	 *            The game-state s_t.
	 * @param nextS
	 *            The game-state s_{t+1}.
	 * @param gameOver
	 *            True, if s_{t+1} is a terminal state.
	 * @param reward
	 *            Reward from the environment.
	 */
	public void updateValueFunction(int numGames, GameStateSnapshot curS,
			GameStateSnapshot nextS, boolean gameOver, double reward) {

		//
		// Discount factor, typically chosen to be 1.0
		//
		double gamma = tdPar.gamma;

		//
		// If we are using board-inversion, then we only learn the
		// value-function for player A. Values for player B can be determined by
		// inverting the board. If the player to move is player B, then the
		// reward has to be inverted, since we assume, that the reward is always
		// for player A (If player B closed a box, the reward will be negative.
		// We assume, that player A closed the box in this case).
		//
		//
		// Additionally, if the player for state s_t and s_{t+1} differs, then
		// response of the n-tuple system has to be inverted for s_{t+1}. The
		// reason for this is: If player A makes a move and it is player B's
		// turn,
		//
		double fak = 1.0;
		//double fak1 = 1.0;
		//double fak2 = 1.0;
		if (tdPar.useBoardInversion) {
			if (curS.getPlayerToMove() < 0)
				reward *= -1.0;
			if (curS.getPlayerToMove() != nextS.getPlayerToMove())
				fak *= -1.0;
		}

		// Get net response for current state
		double y =  getNetResponse(curS);

		// Depending on the Activation-function used, the reward has to be
		// scaled in a certain range.
		//if (gameOver) {
			reward = scaleReward(reward);
		//}

		// Target value
		// If we reached a terminal state, we cannot retrieve a response from
		// the net. In some cases the reward will be unequal zero, even if no
		// terminal state is reached. This is the case when immediate rewards
		// are used.
		//
		double tg = (gameOver ? reward : reward + fak * gamma
				* getNetResponse(nextS));

		// Error-Signal delta
		double delta = tg - y;

		// Get the outer derivative of y for the Weight-update Loss-Function.
		// This is typically also the MSE-Error of the Target-Value. In certain
		// cases however, also other Loss-functions are used, such as in Koop's
		// IDBD
		double derivW = outerDerivateWeightLoss(y);

		// Get outer derivative of y for the MSE
		double derivMSE = outerDerivateMSELoss(y);

		// Update-Parameters to update the n-tuple network
		double alpha = globalAlpha.f(numGames);
		UpdateParams u = new UpdateParams(curS, y, derivMSE, derivW, delta,
				alpha);

		// Update Weights
		nTupleNet.updateWeights(u);
	}

	/**
	 * Returns the response of the n-tuple network for the current state. Note
	 * that calling this method is not correct, if board-inversion is used,
	 * since the net only knows the values for player A.
	 * 
	 * @param s
	 *            Current game-state
	 * @return Net-response of the given state, put through an
	 *         activation-function.
	 */
	private double getNetResponse(GameStateSnapshot s) {
		double linVal = nTupleNet.getValue(s);

		// Put this value through an activation-function, if necessary
		return ActivationFunction.activation(linVal, tdPar.activation);
	}

	public double getValue(GameStateSnapshot s) {
		// When using board-inversion, this method getNetResponse always returns
		// the value,
		// assuming that player A is to move. Therefore, we have to invert the
		// value, when player B is to move.
		double netResponse = getNetResponse(s);
		if (tdPar.useBoardInversion && s.getPlayerToMove() < 0)
			return -netResponse;
		return netResponse;
	}

	private double scaleReward(double reward) {
		// the reward is in range [-1,1]. We map it to range [0,1] for
		// the non-linear IDBD version to adjust it to the range of
		// the logistic sigmoid:
		return ActivationFunction.scaleReward(reward, tdPar.activation);
	}

	private double outerDerivateWeightLoss(double y) {
		return ActivationFunction.derivateWeightLoss(y, tdPar.activation);
	}

	private double outerDerivateMSELoss(double y) {
		return ActivationFunction.derivateMSELoss(y, tdPar.activation);
	}

	private void initGlobalAlpha(TrainingParams par) {
		TDParams td = tdPar;
		double a0 = td.alphaInit; // Initial exploration-rate
		double aN = td.alphaFinal; // Final exploration-rate
		double N = par.numGames; // Total number of training-games
		FunctionScheme a = td.alphaAdjust; // how epsilon is adjusted
		double[] coeff = td.alphaAdjustCoeff; // If some coeff. are required

		globalAlpha = new Function(a0, aN, N, a, coeff);
	}

	public double getGlobalAlpha(int numGames) {
		double x = numGames;
		return globalAlpha.f(x);
	}
	

	public void resetEligibilityTraces(GameState s) {
		nTupleNet.resetEligibilityTraces();
		boolean replace = tdPar.replacingTraces;
		double y = getNetResponse(s);
		double grad = outerDerivateWeightLoss(y);
		nTupleNet.addGradientToEligibilityTraces(s, grad, replace);
	}

	public void updateEligibilityTraces(GameState s) {
		double lambda = tdPar.lambda;
		double gamma = tdPar.gamma;
		boolean replace = tdPar.replacingTraces;
		double y = getNetResponse(s);
		double grad = outerDerivateWeightLoss(y);
		nTupleNet.updateEligibilityTraces(s, lambda, gamma, grad, replace);
	}
}
