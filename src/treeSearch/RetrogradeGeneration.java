package treeSearch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateFactory;
import dotsAndBoxes.GameStateSnapshot;

public class RetrogradeGeneration {
	private static final int M = 4;
	private static final int N = 4;

	private static int VERBOSE = 3;

	private static final byte BITINDEX[][][];

	private static final int RUN_TO_DEPTH = 4;

	private int max_depth = 7;

	private static final boolean USE_TRANSPOSITION_TABLE = true;
	private static final int TRANSPOSITION_MAX_DEPTH = 10;
	private static final boolean USE_CORNER_SYM = true;

	/**
	 * Print the current status of the generation-process always after a certain
	 * amount of initial nodes were visited.
	 */
	private static final long OUTPUT_THRESHOLD = 100000L;

	private GameStateSnapshot s;
	private HashSet<GameStateSnapshot> leafNodes;

	private HashSet<GameStateSnapshot> trans;

	private GameStateSnapshot[] lastSet = null;

	private long time_ms;

	private long initialNodeCount;

	static {
		BITINDEX = GameStateFactory.ALL_BIT_INDEXES[M - 1][N - 1];
	}

	public RetrogradeGeneration() {
		init(M, N);
	}

	public void startGeneration(int lastDepth) {
		long start = System.currentTimeMillis();

		if (lastSet == null) {
			trans = new HashSet<GameStateSnapshot>((int) Math.pow(2, 20));
			advanceBackwards(0);
		} else {
			// We do not need the transposition table in this case actually
			// This appears to be slightly faster in the tests...
			trans = null;

			System.gc();

			// Iterate through all states for depth-1 and create the new states
			initialNodeCount = 0;
			for (GameStateSnapshot i : lastSet) {
				s = new GameStateSnapshot(i);
				advanceBackwards(lastDepth);
				initialNodeCount++;
				if (initialNodeCount % OUTPUT_THRESHOLD == 0) {
					printProgress();
				}
			}
		}
		long end = System.currentTimeMillis();
		time_ms = end - start;
	}

	public void init(int m, int n) {
		s = new GameState(m, n);
		// TODO: how determine the initial size??
		leafNodes = new HashSet<GameStateSnapshot>((int) Math.pow(2, 24));

		// fill board completly
		s = fillBoard(m, n);

		// Use impartiality
		s.setPlayerToMove(1);

		// Set Box-Difference to zero
		s.resetBoxDiff();
	}

	private static GameStateSnapshot fillBoard(int m, int n) {
		GameState state = new GameState(M, N);
		while (!state.isTerminal())
			state.advance(false);
		return state.getGameStateSnapshot();
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

	private void advanceBackwards(int depth) {
		GameStateSnapshot snap = new GameStateSnapshot(s);// s.getStaticSnapshot();

		// If we have reached this position, or symmetric positions before, then
		// we can skip the whole subtree, since all resulting positions have
		// already occured.
		if (trans != null && USE_TRANSPOSITION_TABLE
				&& depth <= TRANSPOSITION_MAX_DEPTH) {
			if (hashSetContains(trans, snap)) {
				// If we skip this subtree, then we can assume that all the
				// leaf-nodes were visited. So add this number to our counter
				return;
			} else
				trans.add(snap);
		}

		if (depth >= max_depth) {
			// add snapshot of the state to Hash-Map
			if (!hashSetContains(leafNodes, snap)) {
				// returns true, if the set did NOT contain the board
				boolean notContained = leafNodes.add(snap);
				// If the set contained this board, then something went wrong,
				// because we checked this before already.
				if (!notContained)
					throw new UnsupportedOperationException();
				return;
			}
			return;
		}

		// Remove one line and advance
		byte hor[][] = BITINDEX[0];
		byte vert[][] = BITINDEX[1];
		for (int i = 0; i <= M; i++) {
			for (int j = 0; j <= N; j++) {
				if (i < M) {
					// remove an horizontal line
					if (s.isSet(0, hor[i][j])) {
						s.removeLine(0, hor[i][j]);
						advanceBackwards(depth + 1);
						s.setLine(0, hor[i][j]);
					}
				}
				if (j < N) {
					// remove a vertical line
					if (s.isSet(1, vert[i][j])) {
						s.removeLine(1, vert[i][j]);
						advanceBackwards(depth + 1);
						s.setLine(1, vert[i][j]);
					}
				}
			}
		}
	}

	public static void arrayToFile(GameStateSnapshot[] ss, String path) {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(ss);
			oos.close();
			fos.close();
			System.out.printf("Data saved in: " + path);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static GameStateSnapshot[] fileToArray(String path) {
		GameStateSnapshot[] set = null;
		try {
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			set = (GameStateSnapshot[]) ois.readObject();
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
		System.out.println("Deserialized Array..");
		return set;
	}

	private void printProgress() {
		int roundFac = 10000;
		if (VERBOSE >= 3) {
			double percent = (100.0 * initialNodeCount / lastSet.length);
			int percentI = (int) ((int) roundFac * (percent / roundFac));
			System.out.println("Analyzed approx. " + percentI
					+ "% of all initial nodes.");
			System.out.println("Number of nodes added to the hash-set: "
					+ leafNodes.size());
		}
	}

	public void prepareGeneration(String lastFilePath) {
		// To array, in order to save memory
		if(lastSet == null)
			lastSet = fileToArray(lastFilePath);
	}

	public void setMacDepth(int maxDepth) {
		max_depth = maxDepth;
	}

	public int getMaxDepth() {
		return max_depth;
	}

	@Override
	public String toString() {
		String s = new String();
		s += "Number of nodes added to hash-set: " + leafNodes.size() + "\n";
		s += "Time for the search: " + time_ms / 1000.0 + " seconds";
		return s;
	}

	private void save(String path) {
		GameStateSnapshot[] ss = leafNodes
				.toArray(new GameStateSnapshot[leafNodes.size()]);
		
		arrayToFile(ss, path);
		lastSet = null;
		System.gc();
		System.gc();
		lastSet = ss;
	}

	public static void main(String[] args) {
		for (int i = 4; i <= RUN_TO_DEPTH; i++) {
			RetrogradeGeneration rg = new RetrogradeGeneration();
			rg.setMacDepth(i);
			System.out.println("\n\n\nStart with generating states for " + i
					+ " moves...");

			int lastDepth = rg.getMaxDepth() - 1;
			if (lastDepth > 0)
				rg.prepareGeneration("retro_" + M + "x" + N + "_" + lastDepth
						+ ".oo");

			rg.startGeneration(lastDepth);

			System.out.println(rg);

			rg.save("retro_" + M + "x" + N + "_" + rg.getMaxDepth() + ".oo");
		}
	}

}
