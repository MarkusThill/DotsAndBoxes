package solvers;

import java.util.List;

import dotsAndBoxes.GameState;
import dotsAndBoxes.IAgent;
import dotsAndBoxes.Move;
import dotsAndBoxes.MoveValue;
import treeSearch.AlphaBeta;

public class WilsonAlphaBetaAgent implements IAgent {
	private WilsonAgent wa;
	private AlphaBeta ab;
	private int m;
	private int n;

	public WilsonAlphaBetaAgent(int m, int n) {
		wa = new WilsonAgent(m, n);
		ab = new AlphaBeta();
		this.m = m;
		this.n = n;
	}

	private IAgent selectAgent(GameState s) {
		
		//
		// Decide, whether to use the tree-search or Wilsons solver. The
		// decision is made based on the average time for the response
		// (empirically evaluated).
		//
		//
		// 
		// If one side has only one box
		if((m == 1 || n == 1) && m + n < 5)
			return ab;
		//
		// for 2x2 boards
		//
		if(m * n == 4)
			return ab;
		//
		// For 2x3 or 3x3 boards
		//
		if(m*n == 6) 
			return ab;
		
		//
		// For 3x3 board
		//
		if(m == 3 && n == 3) {
				int numLines = s.countLines();
				if(numLines < 10)
					return wa;
				return ab;
		}
		
		if(m == 4 && n == 4) {
			int numLines = s.countLines();
			if(numLines < 15) //!!!!
				return wa;
			return ab;
		}
	
		throw new UnsupportedOperationException("These dimensions are not supported yet!!!");
	}

	private void checkBoardDimensions(GameState s) {
		int[] dim = s.getBoardDimensions();
		if (dim[0] != m || dim[1] != n)
			throw new UnsupportedOperationException(
					"Board dimensions not supported by this agent. Initialize a new Agent!");
	}

	@Override
	public Move getBestMove(GameState s) {
		checkBoardDimensions(s);
		return selectAgent(s).getBestMove(s);
	}

	@Override
	public List<Move> getBestMoves(GameState s) {
		checkBoardDimensions(s);
		return selectAgent(s).getBestMoves(s);
	}

	@Override
	public double getValue(GameState s) {
		checkBoardDimensions(s);
		return selectAgent(s).getValue(s);
	}

	@Override
	public List<MoveValue> getActionValues(GameState s) {
		checkBoardDimensions(s);
		return selectAgent(s).getActionValues(s);
	}

}
