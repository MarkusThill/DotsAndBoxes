package treeSearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import treeSearch.TranspositionTable.Entry;
import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateSnapshot;
import dotsAndBoxes.IAgent;
import dotsAndBoxes.Move;
import dotsAndBoxes.MoveValue;

// TODO: Store best-Move in Transposition-Table: Only useful for iterative deepening.
public class AlphaBeta implements IAgent {

	private static final int MAX_TRANS_DEPTH1 = 14;
	private static final int MAX_TRANS_DEPTH2 = 200;
	private static final boolean USE_TRANSPOSITION_SYM = true;
	private static final int MAX_TRANSPOSITION_SYM_DEPTH = 12;

	private static final boolean USE_TRANSPOSITION_MIN = true;

	// not sure, helps for empty boards, but gets useless later...
	private static final boolean USE_TRANSPOSITION_CORNER_SYM = false;

	private static final boolean USE_CORNER_SYMMETRY = true;

	private static final boolean TRY_CAPTURING_MOVES_FIRST = true;
	private static final int TRY_CAPTURING_MOVES_DEPTH = 100;

	/**
	 * If true, the values of an evaluated board will be the exact
	 * box-difference at the end of the game, if both agents play perfectly. If
	 * false, a simple win-loss-draw search will be performed.
	 */
	private static final boolean USE_BOXDIFF = true;

	/**
	 * Max. depth for enhanced transposition cutoffs.
	 */
	private static final int MAX_ETC_DEPTH = MAX_TRANS_DEPTH1;

	/**
	 * If true, during the tree-search, initially avoid moves, that allow the
	 * opponent to occupy a box. After all other moves have been tried, try
	 * these moves. Only implemented for player max, since
	 */
	private static final boolean AVOID_CREATE_CAPTURABLE = true;
	private static final int AVOID_CREATE_CAPTURABLE_DEPTH = 100;

	private static final boolean USE_CHAINS = false;

	private GameState s;

	private TranspositionTable tt;
	private TranspositionTable tt2;

	public AlphaBeta() {
		if (MAX_TRANS_DEPTH1 > 0)
			tt = new TranspositionTable();
		if (MAX_TRANS_DEPTH2 > 0)
			tt2 = new TranspositionTable();

	}

	private short max(int depth, short alpha, short beta) {
		// If we have a terminal state, return
		if (s.isTerminal())
			return (short) (USE_BOXDIFF ? s.getBoxDifference() : s.getWinner());

		GameStateSnapshot snap = s.getGameStateSnapshot();

		// We have two transposition tables, one for nodes close to the root and
		// another one for nodes nearer to the leafs of the tree
		TranspositionTable myTT = null;
		if (depth <= MAX_TRANS_DEPTH1)
			myTT = tt;
		else if (depth <= MAX_TRANS_DEPTH2)
			myTT = tt2;

		//
		// Enhanced transposition cutoffs. Try all after-states in the hope that
		// one of these states will result in a transposition-cutoff. This can
		// significantly reduce the number of nodes, that have to be evaluated.
		//
		if (depth <= MAX_ETC_DEPTH) {
			GameStateSnapshot[] afterStates = s.getAfterStateSnapshots();
			TranspositionTable etcTT = (depth < MAX_TRANS_DEPTH1 ? tt : tt2);
			for (GameStateSnapshot ss : afterStates) {
				Entry e = etcTT.get(ss);
				if (e != null && e.flag != Entry.UPPER && e.value >= beta)
					return e.value;
			}
		}

		//
		// Check, if we can have a transposition cut-off.
		// We have a two-stage transposition table approach. This
		// prevents that entries for smaller depths (which are more
		// desirable) are overwritten by other entries, since the
		// entries for node close to the root can cut off larger parts
		// of the tree. The first stage is in the table tt, the second in tt2.
		//
		if (myTT != null) {
			GameStateSnapshot[] sym;
			// Lookup symmetric positions only for nodes nearer to the
			// root-node, since this is quite costly
			if (USE_TRANSPOSITION_SYM && depth <= MAX_TRANSPOSITION_SYM_DEPTH) {
				sym = USE_TRANSPOSITION_CORNER_SYM ? s
						.getAllSymmetricSnapshots() : s
						.getMirrorSymmetricSnapshots();
			} else {
				sym = new GameStateSnapshot[] { snap };
			}
			for (GameStateSnapshot ss : sym) {
				Entry e;
				e = myTT.get(ss);
				if (e != null) {
					switch (e.flag) {
					case Entry.EXACT:
						return e.value;
					case Entry.UPPER:
						if (e.value <= alpha)
							return e.value;
						if (e.value < beta)
							beta = e.value;
						break;
					case Entry.LOWER:
						if (e.value >= beta)
							return e.value;
						if (e.value > alpha)
							alpha = e.value;
						break;
					}
					break;
				}
			}
		}
		boolean isExactValue = false;

		// if (USE_CHAINS) {
		// short score = Short.MIN_VALUE;
		// boolean hardHearted = true;
		// boolean continuue = false;
		// List<ChainSegment> strat = null;
		// do {
		// // First look for a strategy, that creates a hard-hearted
		// // handout
		// strat = s.findStrategy(hardHearted);
		// if (strat != null) {
		// //
		// // make all moves, that are part of the optimal strategy
		// //
		// // counter for moves performed
		// TreeSet<Integer> delIndex = new TreeSet<Integer>();
		// for (int i = 0; i < strat.size(); i++) {
		// ChainSegment cs = strat.get(i);
		// boolean test = s.advance(cs.direction,
		// (byte) cs.bitIndex);
		// if (!test) {
		// // TODO: This is only a workaround. Solve the main
		// // problem. See testError1 in the Test-class
		// // somehow we already did this move. Remove from the
		// // List
		// // save i to delete later
		// // if (delIndex >= 0)
		// // throw new UnsupportedOperationException();
		// delIndex.add(i);
		// }
		// }
		//
		// if (delIndex.size() > 0) {
		// ArrayList<Integer> remove = new ArrayList<Integer>(
		// delIndex);
		// Collections.sort(remove, new Comparator<Integer>() {
		// public int compare(Integer a, Integer b) {
		// // todo: handle null
		// return b.compareTo(a);
		// }
		// });
		// for (int rem : remove)
		// strat.remove(rem);
		// }
		//
		// if (strat.size() > 0) {
		//
		// if (s.getPlayerToMove() > 0)
		// score = max(depth + 1, alpha, beta);
		// else
		// score = min(depth + 1, alpha, beta);
		//
		// // undo moves
		// for (int i = 0; i < strat.size(); i++)
		// s.undo();
		//
		// if (score >= beta) {
		// if (myTT != null)
		// myTT.put(snap, score, Entry.LOWER);
		// return beta;
		// }
		// if (score > alpha) {
		// isExactValue = true;
		// alpha = score;
		// }
		// } else
		// continuue = true;
		// }
		//
		// hardHearted = !hardHearted;
		// } while (!hardHearted);
		//
		// // We can stop at this point. Only these moves were optimal
		// if (strat != null && !continuue) {
		// if (myTT != null)
		// myTT.put(snap, alpha, isExactValue ? Entry.EXACT
		// : Entry.UPPER);
		// return alpha;
		// }
		// }

		//
		// Avoid moves, that create capturable boxes, if wanted. In this case
		// search for moves that provide a capturable box for the opponent
		//
		boolean avoidCapturable = AVOID_CREATE_CAPTURABLE
				&& (depth <= AVOID_CREATE_CAPTURABLE_DEPTH);
		ArrayList<Move> avoidMV = null;
		if (avoidCapturable) {
			avoidMV = new ArrayList<Move>(5);
		}

		//
		// Check, if we want to try capturing moves first
		//
		boolean tryCapture = TRY_CAPTURING_MOVES_FIRST
				&& depth <= TRY_CAPTURING_MOVES_DEPTH;
		ArrayList<Move> captureMV = null;
		if (tryCapture) {
			captureMV = new ArrayList<Move>(5);
		}

		//
		// Try all moves first, if we want to try all capturing moves first. Or,
		// if we want to find all moves, that allow the opponent to capture a
		// box
		//
		if (tryCapture || avoidCapturable) {

			short score = Short.MIN_VALUE;
			// Iterator<Move> i1 = s.iterator();
			int boxDiff, p;

			// while (i1.hasNext()) {
			// Move mv = i1.next();
			for (Move mv : s) {
				boxDiff = s.getBoxDifference();
				p = s.getPlayerToMove();
				s.advance(mv);

				//
				// only execute, if we want to try capturing moves
				//
				if (tryCapture && boxDiff != s.getBoxDifference()) {
					captureMV.add(mv);
					score = max(depth + 1, alpha, beta);
				}

				//
				// only execute, if we want to find all moves, that allow the
				// opponent to capture a box. Only avoid this move, if it is the
				// opponents turn now.
				//
				else if (avoidCapturable && s.createdCapturable(mv)
						&& p != s.getPlayerToMove()) {
					avoidMV.add(mv);
				}

				s.undo();

				if (score >= beta) {
					return beta;
				}
				if (score > alpha) {
					isExactValue = true;
					alpha = score;
				}

			}
		}

		//
		// For every corner of the board save, if it was visited already. If the
		// corner was already visited, then we do not have to evaluate the other
		// move to the corner.
		//
		boolean cornerVisited[] = new boolean[4];
		int corner = -1;

		// Iterate through all moves and try them one by one.
		// Iterator<Move> i = s.iterator();
		short score = Short.MIN_VALUE;
		// while (i.hasNext()) {
		// Move mv = i.next();
		for (Move mv : s) {
			s.advance(mv);

			//
			// If we tried a capturing move before, then skip it now
			//
			if (tryCapture && captureMV.contains(mv)) {
				s.undo();
				continue;
			}

			//
			// If we want to avoid moves, that allow the opponent to capture a
			// box, then skip these moves at this point, and try later.
			//
			if (avoidCapturable && avoidMV.contains(mv)) {
				s.undo();
				continue;
			}

			//
			// check, if move is placed in the corner and if this corner was
			// visited already. If the corner was already visited, we do not
			// have to evaluate this move.
			//
			if (USE_CORNER_SYMMETRY)
				corner = s.getCorner(mv);
			if (corner < 0 || !cornerVisited[corner]) {
				// If corner is visited the first time, then note this
				if (corner >= 0)
					cornerVisited[corner] = true;

				if (s.getPlayerToMove() > 0)
					score = max(depth + 1, alpha, beta);
				else
					score = min(depth + 1, alpha, beta);
			}

			s.undo();

			if (score >= beta) {
				if (myTT != null)
					myTT.put(snap, score, Entry.LOWER);
				return beta;
			}
			if (score > alpha) {
				isExactValue = true;
				alpha = score;
			}

		}

		//
		// If we avoided certain moves in the beginning, we have to perform the
		// moves now.
		//
		if (avoidMV != null && avoidMV.size() != 0) {
			Iterator<Move> i1 = avoidMV.iterator();
			while (i1.hasNext()) {
				Move mv = i1.next();
				s.advance(mv);

				//
				// check, if move is placed in the corner and if this corner was
				// visited already. If the corner was already visited, we do not
				// have to evaluate this move.
				//
				if (USE_CORNER_SYMMETRY)
					corner = s.getCorner(mv);
				if (corner < 0 || !cornerVisited[corner]) {
					// If corner is visited the first time, then note this
					if (corner >= 0)
						cornerVisited[corner] = true;

					if (s.getPlayerToMove() > 0)
						score = max(depth + 1, alpha, beta);
					else
						score = min(depth + 1, alpha, beta);
				}

				s.undo();

				if (score >= beta) {
					if (myTT != null)
						myTT.put(snap, score, Entry.LOWER);
					return beta;
				}
				if (score > alpha) {
					isExactValue = true;
					alpha = score;
				}
			}
		}

		if (myTT != null)
			myTT.put(snap, alpha, isExactValue ? Entry.EXACT : Entry.UPPER);
		return alpha;

	}

	private short min(int depth, short alpha, short beta) {
		// If we have a terminal state, return
		if (s.isTerminal())
			return (short) (USE_BOXDIFF ? s.getBoxDifference() : s.getWinner());

		TranspositionTable myTT = null;
		if (depth <= MAX_TRANS_DEPTH1 && USE_TRANSPOSITION_MIN)
			myTT = tt;
		else if (depth <= MAX_TRANS_DEPTH2 && USE_TRANSPOSITION_MIN)
			myTT = tt2;

		//
		// Enhanced transposition cutoffs. Try all after-states in the hope that
		// one of these states will result in a transposition-cutoff. This can
		// significantly reduce the number of nodes, that have to be evaluated.
		//
		if (depth <= MAX_ETC_DEPTH) {
			GameStateSnapshot[] afterStates = s.getAfterStateSnapshots();
			TranspositionTable etcTT = (depth < MAX_TRANS_DEPTH1 ? tt : tt2);
			for (GameStateSnapshot ss : afterStates) {
				Entry e = etcTT.get(ss);
				if (e != null && e.flag != Entry.LOWER && e.value <= alpha)
					return e.value;
			}
		}

		//
		// Transposition-Table lookup
		//
		GameStateSnapshot snap = s.getGameStateSnapshot();
		if (myTT != null) {
			GameStateSnapshot[] sym;
			// Lookup symmetric positions only for nodes nearer to the
			// root-node, since this is quite costly
			if (USE_TRANSPOSITION_SYM && depth <= MAX_TRANSPOSITION_SYM_DEPTH) {
				sym = USE_TRANSPOSITION_CORNER_SYM ? s
						.getAllSymmetricSnapshots() : s
						.getMirrorSymmetricSnapshots();
			} else {
				sym = new GameStateSnapshot[] { snap };
			}
			for (GameStateSnapshot ss : sym) {
				Entry e;
				e = myTT.get(ss);
				if (e != null) {
					switch (e.flag) {
					case Entry.EXACT:
						return e.value;
					case Entry.LOWER: // We had no cut-off last time
						if (e.value >= beta)
							return e.value;
						if (e.value > alpha)
							alpha = e.value;
						break;
					case Entry.UPPER:
						if (e.value <= alpha)
							return e.value;
						if (e.value < beta)
							beta = e.value;
						break;
					}
					break;
				}
			}
		}

		boolean isExactValue = false;

		// if (USE_CHAINS) {
		// short score = Short.MAX_VALUE;
		// boolean hardHearted = true;
		// boolean continuue = false;
		// List<ChainSegment> strat = null;
		// do {
		// // First look for a strategy, that creates a hard-hearted
		// // handout
		// strat = s.findStrategy(hardHearted);
		// if (strat != null) {
		// //
		// // make all moves, that are part of the optimal strategy
		// //
		// // counter for moves performed
		// TreeSet<Integer> delIndex = new TreeSet<Integer>();
		// for (int i = 0; i < strat.size(); i++) {
		// ChainSegment cs = strat.get(i);
		// boolean test = s.advance(cs.direction,
		// (byte) cs.bitIndex);
		// if (!test) {
		// // TODO: This is only a workaround. Solve the main
		// // problem. See testError1 in the Test-class
		// // somehow we already did this move. Remove from the
		// // List
		// // save i to delete later
		// // if (delIndex >= 0)
		// // throw new UnsupportedOperationException();
		// delIndex.add(i);
		// }
		// }
		//
		// if (delIndex.size() > 0) {
		// ArrayList<Integer> remove = new ArrayList<Integer>(
		// delIndex);
		// Collections.sort(remove, new Comparator<Integer>() {
		// public int compare(Integer a, Integer b) {
		// return b.compareTo(a);
		// }
		// });
		// for (int rem : remove)
		// strat.remove(rem);
		// }
		//
		// if (strat.size() > 0) {
		//
		// if (s.getPlayerToMove() > 0)
		// score = max(depth + 1, alpha, beta);
		// else
		// score = min(depth + 1, alpha, beta);
		//
		// // undo moves
		// for (int i = 0; i < strat.size(); i++)
		// s.undo();
		//
		// if (score <= alpha) {
		// if (myTT != null)
		// myTT.put(snap, score, Entry.UPPER);
		// return alpha;
		// }
		// if (score < beta) {
		// beta = score;
		// isExactValue = true;
		// }
		// } else
		// continuue = true;
		// }
		//
		// hardHearted = !hardHearted;
		// } while (!hardHearted);
		//
		// // We can stop at this point. Only these moves were optimal
		// if (strat != null && !continuue) {
		// if (myTT != null)
		// myTT.put(snap, beta, isExactValue ? Entry.EXACT
		// : Entry.LOWER);
		// return beta;
		// }
		// }

		//
		// Avoid moves, that create capturable boxes, if wanted. In this case
		// search for moves that provide a capturable box for the opponent
		//
		boolean avoidCapturable = AVOID_CREATE_CAPTURABLE
				&& (depth <= AVOID_CREATE_CAPTURABLE_DEPTH);
		ArrayList<Move> avoidMV = null;
		if (avoidCapturable) {
			avoidMV = new ArrayList<Move>(5);
		}

		boolean tryCapture = TRY_CAPTURING_MOVES_FIRST
				&& depth <= TRY_CAPTURING_MOVES_DEPTH;
		ArrayList<Move> captureMV = null;
		if (avoidCapturable || tryCapture) {
			// Try capturing moves first
			captureMV = new ArrayList<Move>(5);
			short score = Short.MAX_VALUE;
			// Iterator<Move> i1 = s.iterator();
			int boxDiff = 0, p;
			// while (i1.hasNext()) {
			// Move mv = i1.next();
			for (Move mv : s) {
				boxDiff = s.getBoxDifference();
				p = s.getPlayerToMove();
				s.advance(mv);
				if (tryCapture && boxDiff != s.getBoxDifference()) {
					captureMV.add(mv);
					score = min(depth + 1, alpha, beta);
				}

				//
				// only execute, if we want to find all moves, that allow the
				// opponent to capture a box. Only avoid this move, if it is the
				// opponents turn now.
				//
				else if (avoidCapturable && s.createdCapturable(mv)
						&& p != s.getPlayerToMove()) {
					avoidMV.add(mv);
				}

				s.undo();

				if (score <= alpha) {
					return alpha;
				}
				if (score < beta) {
					beta = score;
					isExactValue = true;
				}

				// // Test. If we captured only one box, then the move was
				// optimal.
				// //TODO: not sure if true
				// if (tryCapture && boxDiff != s.getBoxDifference()) {
				// if(capture() == null) {
				// if (myTT != null)
				// myTT.put(snap, beta, isExactValue ? Entry.EXACT :
				// Entry.LOWER);
				// return beta;
				// }
				// else
				// s.undo();
				// }

			}
		}

		// For every corner of the board save, if it was visited already. If the
		// corner was already visited, then we do not have to evaluate the other
		// move to the corner.
		boolean cornerVisited[] = new boolean[4];
		int corner = -1;

		// Iterator<Move> i = s.iterator();
		short score = Short.MAX_VALUE;
		// while (i.hasNext()) {
		// Move mv = i.next();
		for (Move mv : s) {
			s.advance(mv);

			//
			// if we already tried this move before (capturing a box), then we
			// do not have to evaluate this move again.
			//
			if (tryCapture && captureMV.contains(mv)) {
				s.undo();
				continue;
			}

			//
			// If we want to avoid moves, that allow the opponent to capture a
			// box, then skip these moves at this point, and try later.
			//
			if (avoidCapturable && avoidMV.contains(mv)) {
				s.undo();
				continue;
			}

			// check, if move is placed in the corner and if this corner was
			// visited already. If the corner was already visited, we do not
			// have to evaluate this move.
			if (USE_CORNER_SYMMETRY)
				corner = s.getCorner(mv);
			if (corner < 0 || !cornerVisited[corner]) {
				// If corner is visted the first time, then note this
				if (corner >= 0)
					cornerVisited[corner] = true;

				if (s.getPlayerToMove() > 0)
					score = max(depth + 1, alpha, beta);
				else
					score = min(depth + 1, alpha, beta);
			}
			s.undo();

			if (score <= alpha) {
				if (myTT != null)
					myTT.put(snap, score, Entry.UPPER);
				return alpha;
			}
			if (score < beta) {
				beta = score;
				isExactValue = true;
			}
		}

		//
		// If we avoided certain moves in the beginning, we have to perform the
		// moves now.
		//
		if (avoidMV != null && avoidMV.size() != 0) {
			Iterator<Move> i1 = avoidMV.iterator();
			while (i1.hasNext()) {
				Move mv = i1.next();
				s.advance(mv);

				//
				// check, if move is placed in the corner and if this corner was
				// visited already. If the corner was already visited, we do not
				// have to evaluate this move.
				//
				if (USE_CORNER_SYMMETRY)
					corner = s.getCorner(mv);
				if (corner < 0 || !cornerVisited[corner]) {
					// If corner is visited the first time, then note this
					if (corner >= 0)
						cornerVisited[corner] = true;

					if (s.getPlayerToMove() > 0)
						score = max(depth + 1, alpha, beta);
					else
						score = min(depth + 1, alpha, beta);
				}

				s.undo();

				if (score <= alpha) {
					if (myTT != null)
						myTT.put(snap, score, Entry.UPPER);
					return alpha;
				}
				if (score < beta) {
					beta = score;
					isExactValue = true;
				}
			}
		}

		if (myTT != null)
			myTT.put(snap, beta, isExactValue ? Entry.EXACT : Entry.LOWER);
		return beta;
	}

	// private Move capture() {
	// Iterator<Move> i1 = s.iterator();
	// int boxDiff;
	// while (i1.hasNext()) {
	// Move mv = i1.next();
	// boxDiff = s.getBoxDifference();
	// s.advance(mv);
	// if (boxDiff != s.getBoxDifference()) {
	// return mv;
	// }
	// s.undo();
	// }
	// return null;
	// }

	@Override
	public double getValue(GameState s) {
		short alpha = Short.MIN_VALUE;
		short beta = Short.MAX_VALUE;
		// make a fast-copy of the game-state
		this.s = new GameState(s, true);
		int p = s.getPlayerToMove();
		return (p == 1 ? max(0, alpha, beta) : min(0, alpha, beta));
	}

	@Override
	public Move getBestMove(GameState root) {
		this.s = new GameState(root, true);

		Move bestMove = null;
		short alpha = Short.MIN_VALUE;
		short beta = Short.MAX_VALUE;

		// Iterator<Move> i = s.iterator();
		// while (i.hasNext()) {
		// Move mv = i.next();
		for (Move mv : s) {
			s.advance(mv);
			int p = s.getPlayerToMove();
			short score = (p == 1 ? max(1, alpha, beta) : min(1, alpha, beta));
			s.undo();
			p = s.getPlayerToMove();
			if (p > 0 && score > alpha) {
				bestMove = mv;
				alpha = score;
			} else if (p < 0 && score < beta) {
				bestMove = mv;
				beta = score;
			}
		}
		return bestMove;
	}

	@Override
	public List<Move> getBestMoves(GameState s) {
		GameState root = new GameState(s, true);
		int p = root.getPlayerToMove();
		double bestScore = Double.NEGATIVE_INFINITY;
		List<Move> bestMoves = new ArrayList<Move>();

		// Even with an alpha-beta search we have to start a new search for
		// every move, (with alpha = -inf and beta = +inf), if we want a list of
		// best moves.
		// Iterator<Move> i = root.iterator();
		// while (i.hasNext()) {
		// Move mv = i.next();
		for (Move mv : s) {
			root.advance(mv);
			double score = p * getValue(root);
			root.undo();
			if (score > bestScore) {
				bestScore = score;
				bestMoves.clear();
				bestMoves.add(mv);
			} else if (score == bestScore)
				bestMoves.add(mv);
		}
		return bestMoves;
	}

	public List<MoveValue> getActionValues(GameState s) {
		GameState root = new GameState(s, true);
		// int p = root.getPlayerToMove();

		ArrayList<MoveValue> valueList = new ArrayList<MoveValue>(
				root.actionsLeft());

		// Even with an alpha-beta search we have to start a new search for
		// every move, (with alpha = -inf and beta = +inf), if we want a list of
		// best moves.
		// Iterator<Move> i = root.iterator();
		// while (i.hasNext()) {
		// Move mv = i.next();
		for (Move mv : s) {
			root.advance(mv);
			double score = getValue(root);
			root.undo();
			MoveValue mvv = new MoveValue(mv, score);
			valueList.add(mvv);
		}
		return valueList;
	}

	private static void testTime() {
		int moveNum = 15;
		int numBoards = 100;
		AlphaBeta ab = new AlphaBeta();

		long start = System.currentTimeMillis();
		for (int j = 0; j < numBoards; j++) {
			GameState s = new GameState(4, 4);

			// create random state with x lines
			for (int i = 0; i < moveNum; i++)
				s.advance(true);
			ab.getValue(s);
		}
		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("Time: " + time / 1000.0 + "s!");

	}

	private static void testValueInvertedBoard() {
		int numMoves = 10;
		int numBoards = 10;
		AlphaBeta ab = new AlphaBeta();

		long start = System.currentTimeMillis();
		for (int j = 0; j < numBoards; j++) {
			GameState s = new GameState(3, 3);

			// create random state with x lines
			for (int i = 0; i < numMoves; i++)
				s.advance(true);

			// now create the inverted state
			GameState sI = new GameState(3, 3);
			for (int k = 0; k <= 3; k++)
				for (int l = 0; l <= 3; l++) {
					if (k < 3) {
						byte index = (byte) s.getHorLineBitIndex(k, l);
						if (!s.isSet(0, index))
							sI.advance(0, index);
					}
					if (l < 3) {
						byte index = (byte) s.getVertLineBitIndex(k, l);
						if (!s.isSet(1, index))
							sI.advance(1, index);
					}
				}

			System.out.println(s);
			System.out.println(sI);

			s.resetBoxDiff();
			sI.resetBoxDiff();
			s.setPlayerToMove(1);
			sI.setPlayerToMove(1);
			double val1 = ab.getValue(s);
			double val2 = ab.getValue(sI);
			System.out.println("Val1: " + val1 + " .Val2: " + val2);

		}
		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("Time: " + time / 1000.0 + "s!");
	}

	public static void main(String[] args) {
		testTime();
		// testValueInvertedBoard();
	}

	// public static void main(String[] args) {
	// AlphaBeta ab = new AlphaBeta();
	//
	// //
	// // long start = System.currentTimeMillis();
	// // for (int k = 0; k < 10; k++) {
	// // GameState s = new GameState(4, 4);
	// // for (int j = 0; j < 16; j++) {
	// // s.advance(true);
	// // }
	// // ab.getValue(s);
	// // }
	// // long end = System.currentTimeMillis();
	// // long time = end - start;
	// // System.out.println("Time to determine: "
	// // + time / 1000.0 + "s!");
	// //
	//
	// GameState s = new GameState(4, 4);
	//
	// s.advance(true);
	// s.advance(true);
	// s.advance(true);
	// s.advance(true);
	// s.advance(true);
	// s.advance(false);
	// s.advance(true);
	// s.advance(true);
	// s.advance(true);
	// s.advance(false);
	// // s.advance(false);
	// // s.advance(true);
	// s.advance(true);
	// s.advance(false);
	// s.advance(true);
	//
	// long start = System.currentTimeMillis();
	// System.out.println("value: " + ab.getValue(s));
	// System.out.println(s);
	// long end = System.currentTimeMillis();
	// long time = end - start;
	// System.out.println("Time to determine the value for the board: " + time
	// / 1000.0 + "s!");
	//
	// // Iterator<Move> i = s.iterator();
	// // while (i.hasNext()) {
	// // Move mv = i.next();
	// // s.advance(mv);
	// // System.out.println("value: " + ab.getValue(s));
	// // System.out.println(s);
	// // s.undo();
	// // }
	//
	// // s.advance(0, (byte)10);
	// // while(!s.isTerminal()) {
	// // Move mv = ab.getBestMove(s);
	// // s.advance(mv);
	// // System.out.println(s);
	// // }
	//
	// }

}
