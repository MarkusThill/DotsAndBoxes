package dotsAndBoxes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class GameStateFactory {
	// USE RND for the generation of the keys for the hash-generation. Use the
	// same initial seed always. This assures, that the keys are always the same
	// and makes debugging easier.
	private static final Random RND = tools.SRandom.RND;
	static final GameState[][] ALL_GAME_STATES;
	static final long[][][] ALL_VERTICAL_FLIPMASKS;
	static final int[] RANDOM_KEY_LIST;
	static final int[] PLAYER_KEYS;
	static final int[][][][] ALL_CORNERS;
	static final long[][][][][] ALL_BOX_MASKS;
	public static final byte[][][][][] ALL_BIT_INDEXES;

	/**
	 * int[m][n][direction][bitIndex][{0=x,1=y}]
	 */
	static final int[][][][][] ALL_LINE_COORDS;

	/**
	 * All possible lines for every combination of m and n. These line-masks are
	 * needed e.g. to check if a game is over (all lines set)
	 */
	static final long[][][] ALL_LINES;

	static {
		// generate the masks, needed for vertical flipping of the board
		ALL_VERTICAL_FLIPMASKS = GameStateFactory.generateVertFlipMasks();

		// This has to be called after ALL_VERTICAL_FLIPMASKS is initialized,
		// otherwise we get an Exception
		ALL_GAME_STATES = GameStateFactory.createAllGameStates();

		// Get the hashCode-keys for the players
		PLAYER_KEYS = generatePlayerKeys();

		// Get the keys for all different box-differences
		RANDOM_KEY_LIST = generateBoxDifferenceKeys();

		ALL_LINES = generateAllLines();

		ALL_CORNERS = createAllCornerMasks();

		ALL_BOX_MASKS = createAllBoxMasks();

		ALL_BIT_INDEXES = getAllBitIndexes();

		ALL_LINE_COORDS = getAllLineCoords();
	}

	private static long[][][][][] createAllBoxMasks() {
		int max_dim = GameState.MAX_DIMENSION;
		long[][][][][] boxMasks = new long[max_dim][max_dim][][][];
		for (int i = 0; i < max_dim; i++)
			for (int j = 0; j < max_dim; j++)
				boxMasks[i][j] = generateBoxMasks(i + 1, j + 1);
		return boxMasks;
	}

	private static int[][][][] createAllCornerMasks() {
		int max_dim = GameState.MAX_DIMENSION;
		int[][][][] allCorners = new int[max_dim][max_dim][][];
		for (int i = 0; i < max_dim; i++)
			for (int j = 0; j < max_dim; j++)
				allCorners[i][j] = createCornerMasks(i + 1, j + 1);
		return allCorners;
	}

	/**
	 * Generates the masks for the corners. The Corners are numbered in the
	 * following way: <br>
	 * 0 1 <br>
	 * 2 3
	 * 
	 * @param m
	 *            Number of horizontal boxes
	 * @param n
	 *            Number of vertical boxes
	 * @return a long[4][2] array, containing all four corner masks. One corner
	 *         consists out of 2 masks (one for the vertical line, one for the
	 *         horizontal)
	 */
	private static int[][] createCornerMasks(int m, int n) {
		int[][] corners = new int[4][2];
		// Corner 0
		corners[0][0] = getHorLineBitIndex(0, 0, m, n);
		corners[0][1] = getVertLineBitIndex(0, 0, m, n);
		// Corner 1
		corners[1][0] = getHorLineBitIndex(m - 1, 0, m, n);
		corners[1][1] = getVertLineBitIndex(m, 0, m, n);
		// Corner 2
		corners[2][0] = getHorLineBitIndex(0, n, m, n);
		corners[2][1] = getVertLineBitIndex(0, n - 1, m, n);
		// Corner 3
		corners[3][0] = getHorLineBitIndex(m - 1, n, m, n);
		corners[3][1] = getVertLineBitIndex(m, n - 1, m, n);
		return corners;
	}

	private static long[][][] generateAllLines() {
		int max_dim = GameState.MAX_DIMENSION;
		long[][][] all = new long[max_dim][max_dim][];
		for (int i = 0; i < max_dim; i++) {
			for (int j = 0; j < max_dim; j++)
				all[i][j] = generateAllLines(i + 1, j + 1);
		}
		return all;
	}

	/**
	 * Determine a completely filled board (all lines set) for a m x n board.
	 * 
	 * @param m
	 * @param n
	 * @return a long[2]-value which contains all horizontal lines (index 0) and
	 *         all vertical lines (index 1)
	 */
	private static long[] generateAllLines(int m, int n) {
		long lines[] = new long[2];
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (i < m)
					lines[0] |= (1L << getHorLineBitIndex(i, j, m, n));
				if (j < n)
					lines[1] |= (1L << getVertLineBitIndex(i, j, m, n));
			}
		}
		return lines;
	}

	private static byte[][][][][] getAllBitIndexes() {
		byte[][][][][] allIndexes = new byte[GameState.MAX_DIMENSION][GameState.MAX_DIMENSION][][][];
		for (int i = 0; i < GameState.MAX_DIMENSION; i++)
			for (int j = 0; j < GameState.MAX_DIMENSION; j++)
				allIndexes[i][j] = getBitindexes(i + 1, j + 1);
		return allIndexes;
	}

	/**
	 * @param m
	 * @param n
	 * @return An array containing the all possible bit-indexes for a mxn board.
	 *         The first index of the array addresses the direction
	 *         (0->horizontal, 1->vertical), the second index the x-direction (0
	 *         to m or 0 to m-1) and, the thired index the y-direction (0 to n
	 *         or 0 to n-1)
	 */
	private static byte[][][] getBitindexes(int m, int n) {
		byte[][][] indexes = new byte[2][][];
		// horizontal
		indexes[0] = new byte[m][n + 1];

		// vertical
		indexes[1] = new byte[m + 1][n];

		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (i < m)
					indexes[0][i][j] = (byte) getHorLineBitIndex(i, j, m, n);
				if (j < n)
					indexes[1][i][j] = (byte) getVertLineBitIndex(i, j, m, n);
			}
		}
		return indexes;
	}

	private static int[] generateBoxDifferenceKeys() {
		// Generate 64 Keys. This is sufficient
		int boxNum = Long.SIZE;

		// We need 2x boxNum +1 keys, since the box-difference can be negative
		// or positive or zero
		int[] keys = new int[2 * boxNum + 1];
		for (int i = 0; i < keys.length; i++)
			keys[i] = RND.nextInt();
		return keys;
	}

	private static int[] generatePlayerKeys() {
		// We have two players, so 2 keys
		int keys[] = new int[2];
		keys[0] = RND.nextInt();
		keys[1] = RND.nextInt();
		return keys;
	}

	private static long[][][] generateBoxMasks(int m, int n) {
		// [hor/vert][index][hor1,vert1, {hor2, vert2}]
		// start with vertical lines
		final long[][][] boxMasks;
		boxMasks = new long[2][Long.SIZE][];
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j < n; j++) {
				long[] masks;
				if (i > 0 && i < m) {
					masks = new long[4];
				} else
					masks = new long[2];
				int k = 0;
				// the box left from this vertical line can be closed for i > 0
				if (i > 0) { // left box
					// upper horizontal line of the box
					int horUp = GameStateFactory.getHorLineBitIndex(i - 1, j,
							m, n);
					// lower horizontal line of the box
					int horLow = GameStateFactory.getHorLineBitIndex(i - 1,
							j + 1, m, n);
					// vertical line on the right, where the action was
					// performed
					int vertRight = GameStateFactory.getVertLineBitIndex(i, j,
							m, n);
					// vertical line on the left
					int vertLeft = GameStateFactory.getVertLineBitIndex(i - 1,
							j, m, n);
					long maskHor = (1L << horUp) | (1L << horLow);
					long maskVert = (1L << vertLeft) | (1L << vertRight);
					masks[k++] = maskHor;
					masks[k++] = maskVert;
				}
				// the box left from this vertical line can be closed for i < m
				if (i < m) { // right box from vertical line
					// upper horizontal line of the box
					int horUp = GameStateFactory.getHorLineBitIndex(i, j, m, n);
					// lower horizontal line of the box
					int horLow = GameStateFactory.getHorLineBitIndex(i, j + 1,
							m, n);
					// vertical line on the right
					int vertRight = GameStateFactory.getVertLineBitIndex(i + 1,
							j, m, n);
					// vertical line on the left, where the action was performed
					int vertLeft = GameStateFactory.getVertLineBitIndex(i, j,
							m, n);
					long maskHor = (1L << horUp) | (1L << horLow);
					long maskVert = (1L << vertLeft) | (1L << vertRight);
					masks[k++] = maskHor;
					masks[k++] = maskVert;
				}
				int index = GameStateFactory.getVertLineBitIndex(i, j, m, n);
				boxMasks[1][index] = masks;
			}
		}
		// ................................
		// [vert/hor][index][hor1,vert1, {hor2, vert2}]
		// Now do the same with the horizotnal lines
		for (int i = 0; i < m; i++) {
			for (int j = 0; j <= n; j++) {
				long[] masks;
				if (j > 0 && j < n) {
					masks = new long[4];
				} else
					masks = new long[2];
				int k = 0;
				if (j > 0) { // upper box
					// upper horizontal line of the box
					int horUp = GameStateFactory.getHorLineBitIndex(i, j - 1,
							m, n);
					// lower horizontal line of the box
					int horLow = GameStateFactory
							.getHorLineBitIndex(i, j, m, n);
					// vertical line on the right
					int vertRight = GameStateFactory.getVertLineBitIndex(i + 1,
							j - 1, m, n);
					// vertical line on the left
					int vertLeft = GameStateFactory.getVertLineBitIndex(i,
							j - 1, m, n);
					long maskHor = (1L << horUp) | (1L << horLow);
					long maskVert = (1L << vertLeft) | (1L << vertRight);
					masks[k++] = maskHor;
					masks[k++] = maskVert;
				}

				if (j < n) { // lower box
					// upper horizontal line of the box
					int horUp = GameStateFactory.getHorLineBitIndex(i, j, m, n);
					// lower horizontal line of the box
					int horLow = GameStateFactory.getHorLineBitIndex(i, j + 1,
							m, n);
					// vertical line on the right
					int vertRight = GameStateFactory.getVertLineBitIndex(i, j,
							m, n);
					// vertical line on the left, where the action was performed
					int vertLeft = GameStateFactory.getVertLineBitIndex(i + 1,
							j, m, n);
					long maskHor = (1L << horUp) | (1L << horLow);
					long maskVert = (1L << vertLeft) | (1L << vertRight);
					masks[k++] = maskHor;
					masks[k++] = maskVert;
				}

				int index = GameStateFactory.getHorLineBitIndex(i, j, m, n);
				boxMasks[0][index] = masks;
			}
		}

		return boxMasks;
	}

	private static final GameState[][] createAllGameStates() {
		int max_dim = GameState.MAX_DIMENSION;
		GameState all[][] = new GameState[max_dim][max_dim];
		for (int i = 0; i < max_dim; i++)
			for (int j = 0; j < max_dim; j++)
				all[i][j] = GameState.createGameState(i + 1, j + 1);
		return all;
	}

	private static long[][][] generateVertFlipMasks() {
		long vertFlipMasks[][][] = new long[GameState.MAX_DIMENSION][GameState.MAX_DIMENSION][];
		// Generate flip-masks for all possible board sizes. Since all masks are
		// stored in a static final array, we do not need more memory, if we
		// copy a GameStateB
		for (int i = 0; i < GameState.MAX_DIMENSION; i++)
			for (int j = 0; j < GameState.MAX_DIMENSION; j++)
				vertFlipMasks[i][j] = GameStateFactory.generateVertFlipMasks(
						i + 1, j + 1);
		return vertFlipMasks;
	}

	private static long[] generateVertFlipMasks(int m, int n) {
		// generate two masks for every column: For the upper half and the lower
		// half
		// calculate the number of masks needed: Multiply by two, since we need
		// two masks per column
		int numMasks = 2 * (m + 1);
		long[] masks = new long[numMasks];

		// now go through each column and create the mask for the upper and
		// lower half
		int k = 0;
		long mask;
		for (int i = 0; i <= m; i++) {
			// Upper half
			mask = 0L;
			for (int j = 0; j < n / 2; j++)
				mask |= (1L << GameStateFactory.getVertLineBitIndex(i, j, m, n));
			masks[k++] = mask;

			// Lower half
			mask = 0L;
			for (int j = n / 2 + n % 2; j < n; j++)
				mask |= (1L << GameStateFactory.getVertLineBitIndex(i, j, m, n));
			masks[k++] = mask;

		}
		return masks;
	}

	public static int getHorLineBitIndex(int x, int y, int m, int n) {
		int index = -1;
		int v1 = (m - x - 1) * Byte.SIZE + (n - y);
		int offset = Byte.SIZE * (m + m % 2) / 2;
		int v2 = offset + y + x * Byte.SIZE;

		if (x > m / 2) {
			index = v1;
		} else if (x < m / 2) {
			index = v2;
		} else {
			if (m % 2 == 0 || y > n / 2)
				index = v1;
			else
				index = v2;
		}
		return index;
	}

	/**
	 * Reverse lookup. For a bitIndex get the corresponding x- and y-coordinate.
	 * 
	 * @param bitIndex
	 * @param m
	 * @param n
	 * @return
	 */
	public static int[] getHorLineCoord(int bitIndex, int m, int n) {
		int offset = Byte.SIZE * (m + m % 2) / 2;
		int x, y;
		if (bitIndex < offset) {
			// return for former v1
			x = m - 1 - bitIndex / Byte.SIZE;
			y = n - (bitIndex % Byte.SIZE);
		} else {
			// return for former v2
			x = (bitIndex - offset) / Byte.SIZE;
			y = (bitIndex - offset) % Byte.SIZE;
		}
		return new int[] { x, y };
	}

	/**
	 * @param mv
	 * @param m
	 * @param n
	 * @return array with 3 elements: direction, x, y
	 */
	public static int[] getLineCoord(Move mv, int m, int n) {
		int direction = mv.direction;
		int[] c;
		if (direction == 0) {
			// horizontal
			c = getHorLineCoord(mv.bitIndex, m, n);
		} else {
			// vertical
			c = getVertLineCoord(mv.bitIndex, m, n);
		}
		return new int[] { direction, c[0], c[1] };
	}

	public static int getVertLineBitIndex(int x, int y, int m, int n) {
		// if m = n, we could use getHorLineBitIndex and then rotate the index.
		// However, since the boards are not always quadratic, we cannot do this
		// here.
		int index = -1;
		int v1 = (m - x) + Byte.SIZE * y;
		int offset = Byte.SIZE * (n + n % 2) / 2;
		int v2 = offset + x + Byte.SIZE * (n - 1 - y);
		if (y < n / 2) {
			index = v1;
		} else if (y > n / 2) {
			index = v2;
		} else {
			if (x <= m / 2 || n % 2 == 0)
				index = v2;
			else
				index = v1;
		}
		return index;
	}

	/**
	 * Reverse lookup. For a bitIndex get the corresponding x- and y-coordinate.
	 * 
	 * @param bitIndex
	 * @param m
	 * @param n
	 * @return
	 */
	public static int[] getVertLineCoord(int bitIndex, int m, int n) {
		int offset = Byte.SIZE * (n + n % 2) / 2;
		int x, y;
		if (bitIndex < offset) {
			x = m - (bitIndex % Byte.SIZE);
			y = bitIndex / Byte.SIZE;
		} else {
			x = (bitIndex - offset) % Byte.SIZE;
			y = n - 1 - (bitIndex - offset) / Byte.SIZE;
		}
		return new int[] { x, y };
	}

	private static int[][][] getLineCoords(int m, int n) {
		/**
		 * int[direction][bitIndex][{0=x,1=y}]
		 */
		int[][][] lineCoords = new int[2][Long.SIZE][2];

		// Init with -1
		for (int i = 0; i < lineCoords.length; i++)
			for (int j = 0; j < lineCoords[i].length; j++)
				lineCoords[i][j] = new int[] { -1, -1 };

		for (int i = 0; i <= m; i++)
			for (int j = 0; j <= n; j++) {
				if (i < m) {
					// Horizontal lines
					int bitIndex = getHorLineBitIndex(i, j, m, n);
					lineCoords[0][bitIndex] = new int[] { i, j };
				}
				if (j < n) {
					// vertical lines
					int bitIndex = getVertLineBitIndex(i, j, m, n);
					lineCoords[1][bitIndex] = new int[] { i, j };
				}
			}
		return lineCoords;
	}

	/**
	 * int[m][n][direction][bitIndex][{0=x,1=y}]
	 */
	private static int[][][][][] getAllLineCoords() {
		int max = GameState.MAX_DIMENSION;
		int[][][][][] allLineCoords = new int[max][max][][][];
		for (int i = 0; i < max; i++)
			for (int j = 0; j < max; j++) {
				allLineCoords[i][j] = getLineCoords(i + 1, j + 1);
			}
		return allLineCoords;
	}

	public static ArrayList<ArrayList<Byte>> generateMoveList(int m, int n) {
		assert (m <= GameState.MAX_DIMENSION && n <= GameState.MAX_DIMENSION);

		// Create a list of actions for the empty field
		// Example: Vertical lines of a 3x3 field
		// .... 3 2 1 0
		// ....10 11 5 4
		// .... 6 7 8 9
		// Number of actions Vert, Hor
		int numActions = n * (m + 1);
		final ArrayList<Byte> vertActions = new ArrayList<Byte>(numActions);
		final ArrayList<Byte> horActions = new ArrayList<Byte>(numActions);

		final class P {
			int bitIndex;
			int distance;

			P(int bitIndex, int distance) {
				this.bitIndex = bitIndex;
				this.distance = distance;
			}
		}

		ArrayList<P> vert = new ArrayList<P>(numActions);
		ArrayList<P> hor = new ArrayList<P>(numActions);

		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (i < m) {

					int horBit = GameStateFactory
							.getHorLineBitIndex(i, j, m, n);

					P p = new P(horBit, distanceToEdge(new int[] { 0, i, j },
							m, n));
					hor.add(p);
				}
				if (j < n) {
					int vertBit = GameStateFactory.getVertLineBitIndex(i, j, m,
							n);
					P p = new P(vertBit, distanceToEdge(new int[] { 1, i, j },
							m, n));
					vert.add(p);
				}
			}
		}

		Comparator<P> cmp = new Comparator<P>() {
			@Override
			public int compare(P o1, P o2) {
				return Integer.signum(o2.distance - o1.distance);
			}
		};

		// Sort Array-Lists according to their distance
		Collections.sort(hor, cmp);
		Collections.sort(vert, cmp);

		// Now run through the sorted list and add elements to the move-list
		for (P p : hor)
			horActions.add((byte) p.bitIndex);
		for (P p : vert)
			vertActions.add((byte) p.bitIndex);

		ArrayList<ArrayList<Byte>> actions = new ArrayList<ArrayList<Byte>>(2);
		actions.add(horActions);
		actions.add(vertActions);
		return actions;
	}

	/**
	 * Calculates the minimum distance from a line to the edge of the field.
	 * 
	 * @param point
	 *            A point with the format {direction, x, y}
	 * @param m
	 *            Horizontal number of boxes of the board
	 * @param n
	 *            Vertical number of boxes of the board
	 * @return The sum of the minimum distance in vertical and horizontal
	 *         direction of a point.
	 */
	public static int distanceToEdge(int[] point, int m, int n) {
		assert (point.length == 3);
		int distance = 0;
		// for a horizontal line
		if (point[0] == 0) {
			int horDist = Math.min(point[1], m - 1 - point[1]);
			int vertDist = Math.min(point[2], n - point[2]);
			distance = horDist + vertDist;
		}
		if (point[0] == 1) {
			int horDist = Math.min(point[1], m - point[1]);
			int vertDist = Math.min(point[2], n - 1 - point[2]);
			distance = horDist + vertDist;
		}
		return distance;
	}

	/**
	 * @param point
	 *            A point with the format {direction, x, y}
	 * @param m
	 *            Horizontal number of boxes of the board
	 * @param n
	 *            Vertical number of boxes of the board
	 * @return
	 */
	public static int getLineBitIndex(int[] point, int m, int n) {
		assert (point.length == 3);
		return getLineBitIndex(point[0], point[1], point[2], m, n);
	}

	public static int getLineBitIndex(int direction, int x, int y, int m, int n) {
		return (direction == 0 ? getHorLineBitIndex(x, y, m, n)
				: getVertLineBitIndex(x, y, m, n));
	}

	public static int[] getRandArray(int[] max) {
		assert (max.length == 2);
		int[] rand = new int[max.length];
		for (int i = 0; i < max.length; i++) {
			rand[i] = RND.nextInt(max[i]);
		}
		return rand;
	}

	/**
	 * @param m
	 * @param n
	 * @return A random point as int-array containing as first element the
	 *         direction, followed by x- and y-Value
	 */
	public static int[] getRandPoint(int m, int n) {
		int direction = RND.nextInt(2);
		int[] max = getMaxDimension(direction, m, n);
		assert (max.length == 2);
		int[] p = getRandArray(max);
		return new int[] { direction, p[0], p[1] };
	}

	public static int[] getRandPoint(int direction, int m, int n) {
		int[] max = getMaxDimension(direction, m, n);
		assert (max.length == 2);
		return getRandArray(max);
	}

	public static int getRandomBitIndex(int direction, int m, int n) {
		int[] p = getRandPoint(direction, m, n);
		assert (p.length == 2);
		return getLineBitIndex(direction, p[0], p[1], m, n);

	}

	public static int[] getMaxDimension(int dir, int m, int n) {
		assert (dir == 0 || dir == 1);
		int xMax = (dir == 0 ? m : m + 1);
		int yMax = (dir == 0 ? n + 1 : n);
		return new int[] { xMax, yMax };
	}

	public static boolean isLegalPoint(int[] p, int m, int n) {
		assert (p.length == 3);
		boolean legal = true;
		legal &= (p[0] == 0 || p[0] == 1);
		// get max. for x, y
		int[] max = getMaxDimension(p[0], m, n);
		legal = legal && (p[1] < max[0]); // x
		legal = legal && (p[2] < max[1]); // y
		legal = legal && (p[1] >= 0); // x>=0
		legal = legal && (p[2] >= 0); // y>= 0
		return legal;
	}

	public static int[] randomStep(int prevP[], int m, int n)
			throws TimeoutException {
		int dir, x, y, dX, dY;
		int MAX_TRYS = 20;
		// The class variable RND would always generate the same sequence
		int[] p;
		// returns {direction, x, y}
		if (prevP == null)
			return GameStateFactory.getRandPoint(m, n);

		int trys = 0;
		// find a new point for the random walk, based on the old point
		do {
			// The random-walk algorithm can get stuck in some cases
			if (trys >= MAX_TRYS) {
				throw new TimeoutException("Could not find a valid step");
			}
			// new direction
			dir = RND.nextInt(2);
			dX = RND.nextInt(3) - 1; // Delta X can be -1,0,+1
			dY = RND.nextInt(3) - 1; // Delta Y can be -1,0,+1
			x = prevP[1] + dX;
			y = prevP[2] + dY;
			p = new int[] { dir, x, y };
			trys++;
		} while (!isValidStep(prevP[0], dir, dX, dY) || !isLegalPoint(p, m, n));
		return p;
	}

	public static boolean isValidStep(int dirOld, int dir, int dX, int dY) {
		boolean valid = false;
		if (dirOld == 1) {
			valid = valid || (dX == 0 && Math.abs(dY) == 1) && dir == 1;
			valid = valid || (dX == 0 && dY >= 0) && dir == 0;
			valid = valid || (dX == -1 && dY == 0) && dir == 0;
		} else {
			valid = valid || (Math.abs(dX) == 1 && dY == 0) && dir == 0;
			valid = valid || (dX == 0 && dY <= 0) && dir == 1;
			valid = valid || (dX == +1 && dY == 0) && dir == 1;
		}
		return valid;
	}

	public static boolean isSet(long[] lines, int direction, int bitIndex) {
		return 0 != (lines[direction] & (1L << bitIndex));
	}

	public static boolean isSet(long[] lines, int direction, int x, int y,
			int m, int n) {
		int bitIndex = (direction == 0 ? getHorLineBitIndex(x, y, m, n)
				: getVertLineBitIndex(x, y, m, n));
		return isSet(lines, direction, bitIndex);
	}

	public static ArrayList<long[]> getCornerSymmetricLines(long[] ss, int m,
			int n) {
		return getCornerSymmetricLines(ss, 0, m, n);
	}

	private static ArrayList<long[]> getCornerSymmetricLines(long[] ss,
			int corner, int m, int n) {
		assert (corner >= 0 && corner <= 4);

		ArrayList<long[]> result1;

		// Termination criteria
		if (corner >= 4) {
			result1 = new ArrayList<long[]>();
			result1.add(ss);
			return result1;
		}

		// Swap specified edge
		long lines[] = ss.clone();
		swapCornerEdge(lines, corner, m, n);

		// We call this method again for the next corner.
		result1 = getCornerSymmetricLines(ss, corner + 1, m, n);

		// check, if original board and board with the swapped corner are the
		// same. If not, call this method again for the board with swapped
		// edges.
		if (!Arrays.equals(ss, lines)) {
			ArrayList<long[]> result2 = getCornerSymmetricLines(lines,
					corner + 1, m, n);
			result1.addAll(result2);
		}

		// return all symmetric lines
		return result1;
	}

	public static void swapCornerEdge(long[] lines, int corner, int m, int n) {
		assert (corner >= 0 && corner <= 3);
		final int[] cBitIndexes = GameStateFactory.ALL_CORNERS[m - 1][n - 1][corner];
		final long maskH = (1L << cBitIndexes[0]);
		final long maskV = (1L << cBitIndexes[1]);
		final long h = lines[0] & maskH;
		final long v = lines[1] & maskV;
		final long hv = h ^ v;

		// swapping corners only makes sense, if only one edge in the corner is
		// set
		if (hv != 0L && (hv == h || hv == v)) {
			lines[0] ^= maskH;
			lines[1] ^= maskV;
		}
	}

	public static String toString(long[] lines, int m, int n) {
		// print board
		String s = new String();

		int i, j;

		// Get the Board
		// For every box-row
		for (j = 0; j < n + 1; j++) {
			for (i = 0; i < m; i++) {
				// print horizontal lines first
				s = s + "+";
				s += isSet(lines, 0, i, j, m, n) ? "---" : "   ";
			}
			s += "+\n";
			for (i = 0; i < m + 1 && j < n; i++) {
				// now print vertical lines
				s += isSet(lines, 1, i, j, m, n) ? "| " : "  ";
				s += " ";
				s += " ";
			}
			s += "\n";
		}
		return s + "\n";
	}

	static GameState getGameState(int m, int n) {
		return ALL_GAME_STATES[m - 1][n - 1];
	}

	static long[] getVertFlipMasks(int m, int n) {
		return ALL_VERTICAL_FLIPMASKS[m - 1][n - 1];
	}

	static int[][] getBoxes(int direction, int bitIndex, int m, int n) {
		// reverse lookup, so very slow. Not needed that often
		int b;
		int m1 = (direction == 0 ? m : m + 1);
		int n1 = (direction == 0 ? n + 1 : n);
		for (int i = 0; i < m1; i++) {
			for (int j = 0; j < n1; j++) {
				b = (direction == 0 ? //
				getHorLineBitIndex(i, j, m, n)//
						: getVertLineBitIndex(i, j, m, n));
				if (b == bitIndex) {
					// always is the box right/lower from the line
					int[] box1 = null;
					if (direction == 0 && j < n || direction == 1 && i < m)
						box1 = new int[] { i, j };
					int[] box2 = null;
					if (direction == 0 && j > 0)
						box2 = new int[] { i, j - 1 }; // box upper from line
					if (direction == 1 && i > 0)
						box2 = new int[] { i - 1, j }; // box upper from line
					// First left/upper box, then right/lower box
					if (box1 != null && box2 != null)
						return new int[][] { box2, box1 };
					if (box1 != null)
						return new int[][] { box1 };
					if (box2 != null)
						return new int[][] { box2 };
				}
			}
		}
		return new int[0][];
	}

	/**
	 * Generate all 4 lines that form a box with the coordinates (x,y). The
	 * lines are returned as bit-indexes of the corresponding bit-board.
	 * 
	 * @param x
	 *            X-coordinate of the box
	 * @param y
	 *            y-coordinate of the box
	 * @param m
	 *            size of the board in x-direction (number of boxes)
	 * @param n
	 *            size of the board in y-direction (number of boxes)
	 * @return An array with 4 elements, containing all lines (as bit-indexes)
	 *         forming the corresponding box. {vertical-left, horizontal-top,
	 *         vertical-right, horizontal-bottom}
	 */
	public static int[][] getBoxLines(int x, int y, int m, int n) {
		// long[] box = new long[4];
		int vertLeft = getVertLineBitIndex(x, y, m, n);
		int horTop = getHorLineBitIndex(x, y, m, n);
		int vertRight = getVertLineBitIndex(x + 1, y, m, n);
		int horBottom = getHorLineBitIndex(x, y + 1, m, n);
		return new int[][] { { 1, vertLeft }, { 0, horTop }, { 1, vertRight },
				{ 0, horBottom } };
	}
}
