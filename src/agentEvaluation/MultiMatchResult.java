package agentEvaluation;

import dotsAndBoxes.IAgent;

public class MultiMatchResult {
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
	 * Number of matches performed.
	 */
	public int numMatches;

	/**
	 * If this variable is set, then the agents were swapped for the match. This
	 * means that agent0 played second and agent1 played first.
	 */
	public boolean swapAgents;

	/**
	 * Number of wins for agent0, agent1, none. The total number should be equal
	 * to numMatches.
	 */
	public int numWins[];
	
	/**
	 * The results for the single matches.
	 */
	public SingleMatchResult[] singleMatchResults;
}
