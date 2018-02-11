package agentEvaluation;

import java.util.List;

import dotsAndBoxes.GameState;
import dotsAndBoxes.IAgent;
import dotsAndBoxes.Move;

public class AgentMatch {
	private IAgent[] agents;
	private int m;
	private int n;
	private boolean randomizeMoves = true;

	public AgentMatch(IAgent agentA, IAgent agentB, int m, int n) {
		this.m = m;
		this.n = n;
		//
		// index zero corresponds to agent A
		//
		this.agents = new IAgent[2];
		agents[0] = agentA;
		agents[1] = agentB;
	}

	/**
	 * Set true, if agents should randomly pick among moves with equal values.
	 * 
	 * @param randomizeMoves
	 */
	public void setRandomizeMoves(boolean randomizeMoves) {
		this.randomizeMoves = randomizeMoves;
	}

	public SingleMatchResult compete(boolean swapAgents) {
		//
		// Start with an empty board of size m x n
		//
		GameState s = new GameState(m, n);

		//
		// If we want to swap the agents, then do it here.
		//
		IAgent[] a = (swapAgents ? new IAgent[] { agents[1], agents[0] }
				: agents);

		while (!s.isTerminal()) {
			//
			// Determine, which player has to move
			//
			int p = s.getPlayerToMove();

			//
			// Player -1 corresponds to agent 1 and player +1 to agent 0
			//
			int agentIndex = (p > 0 ? 0 : 1);

			//
			// If we want to randomly pick a move among a list of moves with the
			// same value, then do this here
			//
			List<Move> bestMoves = null;
			int selectedMove = -1;
			if (randomizeMoves) {
				bestMoves = a[agentIndex].getBestMoves(s);
				selectedMove = tools.SRandom.RND.nextInt(bestMoves.size());
			}

			//
			// If we did not randomly select among the optimal moves, then
			// determine the best move now
			//
			Move mv = (bestMoves == null ? a[agentIndex].getBestMove(s)
					: bestMoves.get(selectedMove));

			s.advance(mv);
		}

		//
		// Game is over, retrieve the necessary information for this match
		//
		double x = (swapAgents ? -1 : +1) * s.getWinner();
		int agentWinner = (int) Math.round(1.5 * x * x - 0.5 * x - 1);

		SingleMatchResult smr = new SingleMatchResult();
		smr.m = m;
		smr.n = n;
		smr.agents = agents;
		smr.swapAgents = swapAgents;
		smr.agentWinner = agentWinner;
		smr.boxDiff = s.getBoxDifference();
		smr.actionSequence = s.getActionSequence();
		return smr;
	}

	public MultiMatchResult compete(int numMatches, boolean swapAgents) {

		SingleMatchResult[] results = new SingleMatchResult[numMatches];
		//
		// Now perform the number of desired matches
		//
		for (int i = 0; i < numMatches; i++) {
			SingleMatchResult smr = compete(swapAgents);
			results[i] = smr;
		}
		
		//
		// Count wins, losses, draws (view of agent0)
		//
		int wins= 0;
		int loss = 0;
		int draw = 0;
		for(SingleMatchResult smr : results) {
			if(smr.agentWinner == 0)
				wins++;
			else if(smr.agentWinner == 1)
				loss++;
			else if(smr.agentWinner == -1)
				draw++;
			else
				throw new UnsupportedOperationException("Someting went wrong!!!");
		}
		
		assert (wins+loss+draw == numMatches);
			
		
		//
		// Statistics
		//
		MultiMatchResult mmr = new MultiMatchResult();
		mmr.m = m;
		mmr.n = n;
		mmr.agents = agents;
		mmr.swapAgents = swapAgents;
		mmr.numMatches = numMatches;
		mmr.numWins = new int[]{wins, loss, draw};
		mmr.singleMatchResults = results;
		
		return mmr;
	}
}
