package agentEvaluation;

import nTupleTD.TrainingParams.EvaluationPlayAs;
import solvers.WilsonAgent;
import solvers.WilsonAlphaBetaAgent;
import dotsAndBoxes.IAgent;

public class Evaluation {
	private static final boolean RANDOMIZE_EQUAL_MOVES = true;
	
	/**
	 * The agent which is the common reference for the evaluation.
	 */
	private IAgent referenceAgent;

	public Evaluation(IAgent reference, int numEvaluationMatches) {
		this.referenceAgent = reference;
		this.numEvaluationMatches = numEvaluationMatches;
	}
	
	private int numEvaluationMatches = 200;

	/**
	 * Initializes the reference-agent by default with an Agent based on
	 * Wilson's solver.
	 * 
	 * @param m
	 *            Number of boxes in x-direction
	 * @param n
	 *            Number of boxes in y-direction
	 */
	public Evaluation(int m, int n, int numEvaluationMatches) {
		this.referenceAgent = new WilsonAlphaBetaAgent(m, n);
		this.numEvaluationMatches = numEvaluationMatches;
	}

	/**
	 * Deprecated: Better use
	 * {@link Evaluation#evaluate(IAgent, int, int, EvaluationPlayAs)} instead,
	 * since this method does not allow to choose which side the agent agt
	 * should play. The agent agt will always play both sides for this method. <br>
	 * Evaluate an agent. This is done by letting this agent play a predefiened
	 * number of matches against an so-called reference agent. The agent will
	 * play once as player A and once as player B for each
	 * {@link Evaluation#numEvaluationMatches} matches.
	 * 
	 * @param agt
	 *            Agent, that should be evaluated.
	 * @param m
	 *            The x-dimension (boxes) of the board for the evaluation
	 * @param n
	 *            The y-dimension (boxes) of the board for the evaluation
	 * @return A numerical value in the range of -1 to +1. A value of -1
	 *         indicates, that the agent lost all his matches against the
	 *         reference, a value of 0 indicates that in average all matches
	 *         ended in a draw and, a value of +1 indicates that the agent won
	 *         all his matches against the reference.
	 */
	@Deprecated
	public double evaluate(IAgent agt, int m, int n) {
		return evaluate(agt, m, n, EvaluationPlayAs.PLAY_BOTH);
	}

	/**
	 * Evaluate an agent. This is done by letting this agent play a predefined (
	 * {@link Evaluation#numEvaluationMatches}) number of matches against an
	 * so-called reference agent. If the agent is supposed to play both sides,
	 * then the number of evaluation matches is doubled.
	 * 
	 * @param agt
	 *            Agent, that should be evaluated.
	 * @param m
	 *            The x-dimension (boxes) of the board for the evaluation
	 * @param n
	 *            The y-dimension (boxes) of the board for the evaluation
	 * @param playAs
	 * @return A numerical value in the range of -1 to +1. A value of -1
	 *         indicates, that the agent lost all his matches against the
	 *         reference, a value of 0 indicates that in average all matches
	 *         ended in a draw and, a value of +1 indicates that the agent won
	 *         all his matches against the reference.
	 */
	public double evaluate(IAgent agt, int m, int n, EvaluationPlayAs playAs) {
		//
		// First initialize a match.
		//
		AgentMatch am = new AgentMatch(agt, referenceAgent, m, n);
		am.setRandomizeMoves(RANDOMIZE_EQUAL_MOVES);

		//
		// First perform the evaluation matches for agt as player A and
		// reference as player B. If it is defined, for which player agt should
		// play, then do this
		//
		boolean swap = playAs == EvaluationPlayAs.PLAY_AS_B;
		MultiMatchResult mmr1 = am.compete(numEvaluationMatches, swap);
		MultiMatchResult mmr2 = null;
		if (playAs == EvaluationPlayAs.PLAY_BOTH) {
			//
			// agt plays as B now. Only done, if agt plays both sides.
			//
			mmr2 = am.compete(numEvaluationMatches, true);
		}

		//
		// Get the score, indicating the strength of agent agt in comparision to
		// the reference-agent
		//
		double value = mmr1.numWins[0] - mmr1.numWins[1];
		if (playAs == EvaluationPlayAs.PLAY_BOTH)
			value += mmr2.numWins[0] - mmr2.numWins[1];

		//
		// scale into the range -1 to +1
		//
		int fac = (playAs == EvaluationPlayAs.PLAY_BOTH ? 2 : 1);
		return value / (numEvaluationMatches * fac);

	}

	public static void main(String[] args) {
		int m = 3, n = 3;
		Evaluation ev = new Evaluation(m, n, 100);
		WilsonAgent wa = new WilsonAgent(m, n);

		System.out.println("Value: " + ev.evaluate(wa, m, n));

		wa.close();
	}

}
