package treeSearch;

//TODO: For positions with open chains, the optimal moves are always into the chain (capturing all or leaving a hard-hearted handout). 
// Positions, where no hard-hearted handout can be left (all moves in the chain have to be captured), do not really have to be considered in the database.
//
// An agent will first close all chains, before considering other moves.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateSnapshot;
import dotsAndBoxes.Move;

public class GenerateStates {

	private static int VERBOSE = 3;

	private static final int M = 4;

	private static final int N = 4;

	private static final int MAX_DEPTH = 5;

	private static final boolean USE_CORNER_SYM = true;

	private static final boolean SKIP_2ND_CORNER_MOVE = true;

	private static final boolean USE_TRANSPOSITION_TABLE = true;
	private static final int TRANSPOSITION_MAX_DEPTH = 10;

	/**
	 * For a given state, the optimal move is always that move, that maximizes
	 * the future number of boxes, that the player can occupy. The current
	 * box-difference is not relevant in this case. Therefore, all states with
	 * the same line-configuration but different box-differences lead to the
	 * best optimal move. Furthermore, the exact value of a state can be
	 * calculated, even if the current box-difference is assumed to be zero. We
	 * just run for instance the tree-search from the current position with
	 * box-difference zero. After the tree-search returned a value, we add the
	 * box-difference of the state to this value and achieve the exact
	 * game-theoretic value.
	 */
	private static final boolean USE_BOXDIFF_ZERO = true;

	/**
	 * Similar to USE_BOXDIFF_ZERO. For a given state it is not relevant, which
	 * player is to move. The optimal move will be exactly the same for both.
	 * Therefore, we can always assume that e.g. player A is to move and run the
	 * tree-search to get a value. If actually player B is to move, we simply
	 * invert the value returned by the tree-search.
	 */
	private static final boolean USE_IMPARTIALITY = true;

	/**
	 * Print the current status of the generation-process always after a certain
	 * amount of nodes were visited.
	 */
	private static final long OUTPUT_THRESHOLD = 100000L;

	/**
	 * Number of possible leaf-nodes for a given MAX_DEPTH, depending on the
	 * size of the field.
	 */
	private static final double NUM_LEAF_NODES;
	static {
		int numMoves = 2 * M * N + M + N;
		double numLeafNodes = 1;
		for (int i = 0; i < MAX_DEPTH; i++)
			numLeafNodes *= numMoves--;
		NUM_LEAF_NODES = numLeafNodes;
	}

	private GameState s;
	private HashSet<GameStateSnapshot> leafNodes;

	private HashSet<GameStateSnapshot> trans;

	private GameStateSnapshot[] lastSet = null;
	private int startDepth = 0;

	// stats
	private long numNodesVisited = 0;
	private long nodesAddedToSet = 0;
	private long time_ms;

	public void init(int m, int n) {
		s = new GameState(m, n);
		// TODO: how determine the initial size
		leafNodes = new HashSet<GameStateSnapshot>((int) Math.pow(2, 24));
		trans = new HashSet<GameStateSnapshot>((int) Math.pow(2, 20));
		lastSet = null;
		startDepth = 0;
	}

	public void startGeneration(int lastDepth) {
		long start = System.currentTimeMillis();
		if (lastSet == null) {
			numNodesVisited = 0;
			nodesAddedToSet = 0;
			advance(0);
		} else {
			startDepth = lastDepth;
			// We do not need the transposition table in this case actually
			// This appears to be slightly faster in the tests...
			trans = null;

			System.gc();
			System.gc();

			// Iterate through all states for depth-1 and create the new states
			for (GameStateSnapshot i : lastSet) {
				s = new GameState(i);// i.snapshotToState();
				advance(lastDepth);
			}
		}
		long end = System.currentTimeMillis();
		time_ms = end - start;
	}

	public void prepareGeneration(String lastFilePath) {
		HashSet<GameStateSnapshot> oldSet = readHashSet(lastFilePath);

		// To array, in order to save memory
		lastSet = oldSet.toArray(new GameStateSnapshot[oldSet.size()]);

	}

	private static double numLeafNodes(int startDepth) {
		int numMoves = 2 * M * N + M + N - startDepth;
		double numLeafNodes = 1;
		for (int i = startDepth; i < MAX_DEPTH; i++)
			numLeafNodes *= numMoves--;
		return numLeafNodes;
	}

	private void advance(int depth) {
		// If we have a terminal state, return
		if (s.isTerminal()) {
			return;
		}

		GameStateSnapshot snap = s.getGameStateSnapshot();

		// If we utilize impartiality of dots-and-boxes
		if (USE_IMPARTIALITY)
			snap.setPlayerToMove(1);

		// If we want to utilize the fact, that the box-difference is not
		// important for the optimal strategy
		if (USE_BOXDIFF_ZERO)
			snap.resetBoxDiff();

		// If we have reached this position, or symmetric positions before, then
		// we can skip the whole subtree, since all resulting positions have
		// already occured.
		if (trans != null && USE_TRANSPOSITION_TABLE
				&& depth <= TRANSPOSITION_MAX_DEPTH && depth > startDepth) {
			if (hashSetContains(trans, snap)) {
				// If we skip this subtree, then we can assume that all the
				// leaf-nodes were visited. So add this number to our counter
				numNodesVisited += numLeafNodes(depth);
				return;
			} else
				trans.add(snap);
		}

		if (depth >= MAX_DEPTH) {
			// add snapshot of the state to Hash-Map
			if (!hashSetContains(leafNodes, snap)) {
				// returns true, if the set did NOT contain the board
				boolean notContained = leafNodes.add(snap);
				// If the set contained this board, then something went wrong,
				// because we checked this before already.
				if (!notContained)
					throw new UnsupportedOperationException();
				nodesAddedToSet++;
			}
			numNodesVisited++;
			if (numNodesVisited % OUTPUT_THRESHOLD == 0L)
				printProgress();
			return;
		}

		boolean cornerVisited[] = new boolean[4];
		int corner = -1;

		// Try all moves
		//Iterator<Move> i = s.iterator();
		//while (i.hasNext()) {
			//Move mv = i.next();
		for(Move mv : s) {
			s.advance(mv);

			// check, if move is placed in the corner and if this corner was
			// visited already. If the corner was already visited, we do not
			// have to evaluate this move.
			if (SKIP_2ND_CORNER_MOVE)
				corner = s.getCorner(mv);
			if (corner < 0 || !cornerVisited[corner]) {
				// If corner is visted the first time, then note this
				if (corner >= 0)
					cornerVisited[corner] = true;
				advance(depth + 1);
			} else
				numNodesVisited += numLeafNodes(depth + 1);

			s.undo();

			// check, if value is better than old value
		}
	}

	private void printProgress() {
		if (VERBOSE >= 3) {
			System.out.println("Number of nodes visited already: "
					+ numNodesVisited / 1000 + " Thousend. In percent: "
					+ 100.0 * numNodesVisited / NUM_LEAF_NODES + "%");
			System.out.println("Number of nodes added to the Set: "
					+ nodesAddedToSet / 1000 + " Thousend");
		}
	}

	private boolean hashSetContains(HashSet<GameStateSnapshot> hs,
			GameStateSnapshot ss) {
		// Check for all symmetric positions
		GameStateSnapshot[] eq = USE_CORNER_SYM ? ss.getAllSymmetricSnapshots()
				: ss.getMirrorSymmetricSnapshots();
		for (int i = 0; i < eq.length; i++) {
			if (hs.contains(eq[i]))
				return true;
		}
		return false;
	}

	public void hashSetToFile(String path) {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(leafNodes);
			oos.close();
			fos.close();
			System.out
					.printf("Serialized HashMap data is saved in hashmap.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static HashSet<GameStateSnapshot> readHashSet(String path) {
		HashSet<GameStateSnapshot> set = null;
		try {
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			set = (HashSet<GameStateSnapshot>) ois.readObject();

			ois.close();
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return null;
		}
		System.out.println("Deserialized HashMap..");
		return set;
	}

	private void fileToHashSet(String path) {
		leafNodes = readHashSet(path);
	}

	@Override
	public String toString() {
		String s = new String();
		s += "Number of terminal nodes visited: " + numNodesVisited + "\n";
		s += "Number of nodes added to hash-set: " + leafNodes.size() + "\n";
		s += "Time for the search: " + time_ms / 1000.0 + " seconds";
		return s;
	}

	public static void main(String[] args) {
		GenerateStates gs = new GenerateStates();
		gs.init(M, N);

		// Start with last depth
		int lastDepth = MAX_DEPTH - 1;
		if (lastDepth > 0)
			gs.prepareGeneration(M + "x" + N + "_" + lastDepth + ".oo");

		gs.startGeneration(lastDepth);

		System.out.println(gs);

		gs.hashSetToFile(M + "x" + N + "_" + MAX_DEPTH + ".oo");

	}
}
