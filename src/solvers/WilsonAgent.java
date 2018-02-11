package solvers;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import tools.CompareDouble;
import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateFactory;
import dotsAndBoxes.IAgent;
import dotsAndBoxes.Move;
import dotsAndBoxes.MoveValue;

public class WilsonAgent implements IAgent {

	WilsonInterface wi;

	public WilsonAgent(String problemName, int m, int n) {
		wi = new WilsonInterface();
		try {
			wi.loadProblem(problemName, m, n);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public WilsonAgent(int m, int n) {
		wi = new WilsonInterface();
		try {
			wi.loadProblem(m + "x" + n, m, n);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	public void finalize() throws Throwable {
		//
		// In case, the agent is not needed anymore and the solver is not closed
		// properly, ensure that the solver gets terminated when the object is
		// garbage collected.
		//
		wi.close();
		super.finalize();
	}

	@Override
	public Move getBestMove(GameState s) {
		SolverResponse x = wi.getRelativeValueList(s);
		int[] dim = s.getBoardDimensions();
		//
		// First, check if lines were added by the solver. These are optimal
		// then.
		//
		if (x.linesAddedBySolver) {
			// simply take the first move from the list
			int[] mv = x.extraLines.get(0);

			byte bitIndex = (byte) GameStateFactory.getLineBitIndex(mv[0],
					mv[1], mv[2], dim[0], dim[1]);
			return new Move(mv[0], Byte.valueOf(bitIndex));
		}

		//
		// Interpret all values and then select the action that leads to the
		// highest value.
		//
		Double bestValue = Double.NEGATIVE_INFINITY;
		double p = s.getPlayerToMove();
		Double[][][] v = x.values;
		int[] best = null;
		for (int i = 0; i < v.length; i++)
			for (int j = 0; j < v[i].length; j++)
				for (int k = 0; k < v[i][j].length; k++) {
					if (p * v[i][j][k] > bestValue) {
						best = new int[] { i, j, k };
						bestValue = p * v[i][j][k];
					}
				}

		// Return best move
		byte bitIndex = (byte) GameStateFactory.getLineBitIndex(best[0],
				best[1], best[2], dim[0], dim[1]);
		return new Move(best[0], Byte.valueOf(bitIndex));
	}

	@Override
	public List<Move> getBestMoves(GameState s) {
		SolverResponse x = wi.getRelativeValueList(s);
		int[] dim = s.getBoardDimensions();
		ArrayList<Move> bestMoves = new ArrayList<Move>();
		//
		// First, check if lines were added by the solver. These are optimal
		// then.
		//
		if (x.linesAddedBySolver) {
			// Check, which of these lines are capturing, the agent can then
			// select among them.

			for (int[] mv : x.extraLines) {
				int oldBoxDiff = s.getBoxDifference();
				s.advance(mv);
				//
				// If we capture a box with this move, the move is optimal
				//
				if (s.getBoxDifference() != oldBoxDiff) {
					byte bitIndex = (byte) GameStateFactory.getLineBitIndex(
							mv[0], mv[1], mv[2], dim[0], dim[1]);
					bestMoves.add(new Move(mv[0], Byte.valueOf(bitIndex)));
				}
				s.undo();
			}
		} else {
			Double bestValue = Double.NEGATIVE_INFINITY;
			double p = s.getPlayerToMove();
			Double[][][] v = x.values;
			for (int i = 0; i < v.length; i++)
				for (int j = 0; j < v[i].length; j++)
					for (int k = 0; k < v[i][j].length; k++) {
						if (!Double.isNaN(v[i][j][k])) {
							byte bitIndex = (byte) GameStateFactory
									.getLineBitIndex(i, j, k, dim[0], dim[1]);
							if (p * v[i][j][k] > bestValue) {
								//
								// If we find a move that has a higher value
								// than all moves before, than remove all moves
								// from the bestMoves-list and add the cuurent
								// move
								//
								bestValue = p * v[i][j][k];
								bestMoves.clear();
								bestMoves.add(new Move(i, Byte
										.valueOf(bitIndex)));
							} else if (CompareDouble.equals(p * v[i][j][k],
									bestValue)) {
								//
								// If the value of the current move is equal to
								// the value of the best move so far, add this
								// move to the list, since it is equally "good"
								//
								bestMoves.add(new Move(i, Byte
										.valueOf(bitIndex)));
							}
						}
					}

		}
		return bestMoves;
	}

	@Override
	public double getValue(GameState s) {
		SolverResponse x = wi.getRelativeValueList(s);
		//
		// If lines were added by the solver, then we cannot retrieve the
		// game-theoretic values of the state s, since the values of the solver
		// correspond to another state.
		//
		if (x.linesAddedBySolver)
			return Double.NaN;

		//
		// Otherwise return the value, that maximizes the the box-difference of
		// the player to move
		//
		Double bestValue = Double.NEGATIVE_INFINITY;
		double p = s.getPlayerToMove();
		Double[][][] v = x.values;
		for (int i = 0; i < v.length; i++)
			for (int j = 0; j < v[i].length; j++)
				for (int k = 0; k < v[i][j].length; k++) {
					if (!Double.isNaN(v[i][j][k])) {
						if (p * v[i][j][k] > bestValue) {
							bestValue = p * v[i][j][k];
						}
					}
				}
		// The interface to Wilson's solver only returns the future
		// box-difference. Therefore the current box-difference has to be added
		// to the value
		return p * bestValue + s.getBoxDifference();
	}
	
	public void close() {
		wi.close();
	}

	@Override
	public List<MoveValue> getActionValues(GameState s) {
		int[] dim = s.getBoardDimensions();
		SolverResponse x = wi.getRelativeValueList(s);
		ArrayList<MoveValue> moves = new ArrayList<MoveValue>(s.actionsLeft());
		//
		// If lines were added by the solver, than we cannot give values to the
		// actions possible from GameState s, since this state differs from the
		// state of the solver.
		//
		if (x.linesAddedBySolver)
			return null;

		//Iterator<Move> i = s.iterator();
		//while (i.hasNext()) {
		for(Move mv : s) {
			//Move mv = i.next();
			int[] pos = GameStateFactory.getLineCoord(mv, dim[0], dim[1]);
			double value = x.values[pos[0]][pos[1]][pos[2]];
			moves.add(new MoveValue(mv, value));
		}
		return moves;
	}
}
