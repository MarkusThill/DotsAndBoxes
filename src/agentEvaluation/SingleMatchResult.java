package agentEvaluation;

import java.util.List;

import dotsAndBoxes.IAgent;
import dotsAndBoxes.Move;

public class SingleMatchResult {

	/**
	 * Dimensions of the board
	 */
	public int m, n;

	/**
	 * The two agents that competed with each other. Index 0 corresponds to the
	 * player who moves first, if the players are not swapped.
	 */
	public IAgent agents[];

	/**
	 * If this variable is set, then the agents were swapped for the match. This
	 * means that agent0 played second and agent1 played first.
	 */
	public boolean swapAgents;

	/**
	 * Index of the agent who won the match. If the match ended draw, the value
	 * will be -1.
	 */
	public int agentWinner;

	/**
	 * The final box-difference after the game
	 */
	public int boxDiff;

	/**
	 * The move-sequence that led to the final state
	 */
	public List<Move> actionSequence;
}
