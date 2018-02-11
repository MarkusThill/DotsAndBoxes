package dotsAndBoxes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import tools.BitFiddling;

//TODO: What abut the player p. Can we use that as symmetry as well. So store only positions for player A?

public class GameStateSnapshot implements Serializable {
	public static final int MAX_DIMENSION = 7;
	private static final long serialVersionUID = 7056803316159837959L;
	static final Random RND = tools.SRandom.RND;
	// only for odd m, we need to mask the central column of horizontal lines.
	// Mask the whole two bytes
	// private long MASK_HOR_CENTRAL_COLUMN;

	// Player to move
	byte p; // +1 (A), -1 (B)

	/**
	 * Dimensions of the board (number of boxes in each direction)
	 */
	byte n, m;

	final long[] lines; // Board is stored in a bit-field

	/**
	 * Difference of Boxes: Boxes_A - Boxes_B. Player A increments the value, //
	 * Player B decrements. Changed to short, in order to save memory
	 */
	short boxDiff;

	public GameStateSnapshot(int m, int n) {
		// Init player to move: Always A
		this.p = +1;
		this.n = (byte) n;
		this.m = (byte) m;

		// Create lines
		lines = new long[2];
	}

	public GameStateSnapshot(GameStateSnapshot s) {
		p = s.p;
		n = s.n;
		m = s.m;
		boxDiff = s.boxDiff;
		lines = s.lines.clone();
	}

	public GameStateSnapshot(GameStateSnapshot s, long[] lines) {
		p = s.p;
		n = s.n;
		m = s.m;
		boxDiff = s.boxDiff;
		this.lines = lines;
	}

	/* @formatter:off */
	/**
	 * Use the following indexing 
	 * +--0,0--+--1,0--+...
	 * |       |       |
	 * +--0,1--+--1,1--+...
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	/* @formatter:on */
	public int getHorLineBitIndex(int x, int y) {
		return GameStateFactory.getHorLineBitIndex(x, y, m, n);
	}

	/* @formatter:off */
	/**
	 * Usage: x,y
	 *  +-----+------
	 *  |     |
	 * 0,0   1,0  ...
	 *  |     |
	 *  +-----+   ...
	 *  |     |
	 * 0,1   1,1  ...
	 *  |     |
	 *  +-----+   ...
	 *     .
	 *     .
	 *     .
	 * @param x
	 * @param y
	 * @return
	 */
	/* @formatter:on */
	public int getVertLineBitIndex(int x, int y) {
		return GameStateFactory.getVertLineBitIndex(x, y, m, n);
	}

	/**
	 * @param direction
	 *            0, for horizontal lines. 1, for vertical lines
	 * @param bitIndex
	 * @return
	 */
	public boolean isSet(int direction, int bitIndex) {
		return 0L != (lines[direction] & (1L << bitIndex));
	}

	public boolean isSet(int direction, int x, int y) {
		int bitIndex = (direction == 0 ? getHorLineBitIndex(x, y)
				: getVertLineBitIndex(x, y));
		return isSet(direction, bitIndex);
	}

	/**
	 * Only use this method for test-purposes, since it only sets a line without
	 * any further operations such as checking if a box was closed.
	 * 
	 * @param direction
	 * @param bitIndex
	 */
	public void setLine(int direction, int bitIndex) {
		lines[direction] |= (1L << bitIndex);
	}

	/**
	 * Removes a line from the board. To undo a move use
	 * {@link GameState#undo(Move)}, otherwise the Game-State will become
	 * inconsistent, since a few attributes have to be changed, when undoing a
	 * move.
	 * 
	 * @param direction
	 * @param bitIndex
	 */
	public void removeLine(int direction, int bitIndex) {
		lines[direction] &= ~(1L << bitIndex);
	}

	/**
	 * Only use this method for test-purposes, since it only sets a line without
	 * any further operations such as checking if a box was closed.
	 * 
	 * @param direction
	 * @param x
	 * @param y
	 */
	public void setLine(int direction, int x, int y) {
		int bitIndex = (direction == 0 ? getHorLineBitIndex(x, y)
				: getVertLineBitIndex(x, y));
		setLine(direction, bitIndex);
	}

	public long[] flipVertical(final long[] lines, int m, int n) {
		// Flipping the board is slightly more tricky than rotating it. Until
		// now I could not find an extremely efficient solution, even though
		// this approach should be far more faster than flipping an array. This
		// method is also not needed as often as the rotation, which is much
		// more efficient.

		// Vertical flipping needs some masks for the upper and lower part of
		// the
		// board for every column. It is only necessary to copy the reference in
		// case the object is duplicated. The array does not have to be copied.
		long[] verticalFlipMasks = GameStateFactory.getVertFlipMasks(m, n);

		// Horizontal lines can be flipped, simply by reversing the horizontal
		// lines.
		// The Long.reverse command should be very fast, since it only needs a
		// few operations on registers in the CPU and no memory access
		long horizontal = Long.reverse(lines[0]);
		long vertical = 0L;

		// The reverseBytes() method is (should be) directly supported by the
		// CPU, which can reverse the order of the bytes with 1 CPU instruction,
		// bswap command in assembler
		horizontal = Long.reverseBytes(horizontal);

		// Since the whole bytes are reversed, we have to move the bits back
		// into the valid bit-area. Also we want to reverse only a subset of
		// bytes (e.g. the lower 4 bytes). This has to be considered as well.
		int shift = Byte.SIZE - n - 1;
		// Long.rotateRight(horizontal, shiftBack); rotate should not be needed
		horizontal = (horizontal >>> shift);

		// For odd m, we have to swap the two bytes representing the central
		// column
		if ((m & 1) != 0 && m > 1) {
			final int[] bytes = { m >> 1, m };
			long centerMask = BitFiddling.getByteMask(bytes);
			// If n is even, then the horizontal line int the center of
			// the central column has to stay at its place
			if ((n & 1) == 0) {
				// This is the last bit of the higher of both bytes
				long centralBit = (1L << (bytes[1] * Byte.SIZE + n / 2));
				// Delete this bit from the mask, since we do not want to move
				// it
				centerMask ^= centralBit;
			}
			long center = lines[0] & centerMask;
			horizontal &= ~centerMask; // mask out center column first
			// Swap upper and lower half of center column and then add to the
			// remaining lines again
			horizontal |= BitFiddling.swapBytes(center, m >> 1, m);

		}
		if ((n & 1) != 0) {
			final int[] bytes = { n >> 1, n };
			final long centerMask = BitFiddling.getByteMask(bytes);
			// if n is odd, we also have to add the central row of vertical
			// lines to the flipped board, since the position of them does not
			// change
			final long centerRow = lines[1] & centerMask;
			vertical |= centerRow;
		}

		// Now we have to flip the vertical lines, which is far more complicated
		// Take the precomputed masks and flip column by column
		long half;
		assert (verticalFlipMasks.length == 2 * (m + 1));
		shift = ((n + n % 2) * Byte.SIZE >> 1) - m;
		// There are m+1 vertical line-columns
		for (int i = 0, k = 0; i <= m; i++) {
			// flip upper half
			half = lines[1] & verticalFlipMasks[k++];
			// shift upper half to lower half
			vertical |= half << shift;

			// flip lower half to the top
			half = lines[1] & verticalFlipMasks[k++];
			vertical |= half >> shift;
			shift += 2;
		}

		return new long[] { horizontal, vertical };
	}

	public long[] flipVertical(final long[] lines) {
		return flipVertical(lines, m, n);
	}

	public void flipVertical() {
		long flipped[] = flipVertical(lines);
		lines[0] = flipped[0];
		lines[1] = flipped[1];
	}

	public long[] flipHorizontal(long[] lines) {
		// flipping horizontal is the same as turning the board by 90° and then
		// flipping this board vertically and afterwards turning it back again
		// swap m and n in method call
		long[] flipped = flipVertical(new long[] { lines[1], lines[0] }, n, m);
		// swap the values x and y
		// x1 = x ^ y
		// y1 = y ^ x1 = y ^ x ^ y = x
		// x2 = x1 ^ y1 = x ^ y ^ x = y
		flipped[0] ^= flipped[1];
		flipped[1] ^= flipped[0];
		flipped[0] ^= flipped[1];
		return flipped;
	}

	public void flipHorizontal() {
		long flipped[] = flipHorizontal(lines);
		lines[0] = flipped[0];
		lines[1] = flipped[1];
	}

	/**
	 * Rotate the given board (only the lines of the board) to the right. The
	 * board can be rotated in a very efficient manner. Simply swap vertical and
	 * horizontal lines, whereby the horizontal lines are rotated to the right
	 * first. This all only needs very few CPU-instructions. Only a board with m
	 * = n can be rotated.
	 */
	private long[] rotateBoardRight(final long[] lines) {
		assert (m == n);
		long[] rotatedLines = new long[2];
		long horizontal = lines[0];
		rotatedLines[0] = lines[1];
		int rolbits = Byte.SIZE * (m + (m & 1));
		horizontal = BitFiddling.rol(horizontal, rolbits >> 1, rolbits);
		rotatedLines[1] = horizontal;
		return rotatedLines;
	}

	/**
	 * Rotate the given board (only the lines of the board) to the right. The
	 * board can be rotated in a very efficient manner. Simply swap vertical and
	 * horizontal lines, whereby the horizontal lines are rotated to the right
	 * first. This all only needs very few CPU-instructions. Only a board with m
	 * = n can be rotated.
	 */
	@Deprecated
	public void rotateBoardRight() {
		// The board can be rotated in a very efficient manner. Simply swap
		// vertical and horizontal lines, whereby the horizontal lines are
		// rotated to the right first. This all only needs very few
		// CPU-instructions. Only a board with m = n can be rotated.
		assert (m == n);
		long horizontal = lines[0];
		lines[0] = lines[1];
		int rolbits = Byte.SIZE * (m + (m & 1));
		horizontal = BitFiddling.rol(horizontal, rolbits >> 1, rolbits);
		lines[1] = horizontal;
	}

	private long[][] getEquivalentBoards4Fold() {
		// 4-fold symmetry for m != n
		// TODO: What about the symmetries, that were mentioned in the paper?
		long[][] equiv = new long[4][];
		equiv[0] = lines.clone();
		// Flip vertical
		equiv[1] = flipVertical(equiv[0]);
		// Flip Horizontally
		equiv[2] = flipHorizontal(equiv[0]);
		// Flip vertically and horizontally
		equiv[3] = flipHorizontal(equiv[1]);
		return equiv;
	}

	private long[][] getEquivalentBoards8Fold() {
		// 8 fold symmetry for m == n
		// TODO: What about the symmetries, that were mentioned in the paper?
		long[][] equiv = new long[8][];
		equiv[0] = lines.clone();
		// Rotate board 3 times and then flip it and rotate it again
		for (int i = 1; i < equiv.length; i++) {
			if (i == equiv.length / 2) {
				equiv[i] = flipVertical(equiv[i - 1]);
				continue;
			}
			equiv[i] = rotateBoardRight(equiv[i - 1]);
		}
		return equiv;
	}

	/**
	 * @return All symmertric boards based on mirror-symmetries (vertical,
	 *         diagonal, horizontal). For quadratic boards there are 8 such
	 *         symmetric boards, in all other cases there are 4 symmetric
	 *         boards.
	 */
	public long[][] getMirrorSymmetricBoards() {
		// Get all the symmetric positions (Rotation, Vertical flipping)
		// If board is quadratic, we can rotate it, otherwise it only has
		// vertical/horizontal symmetry
		return (n == m ? getEquivalentBoards8Fold()
				: getEquivalentBoards4Fold());
	}

	/**
	 * Inverts the current game state. This means: The players will be swapped,
	 * A will become B and vice versa. Boxes belonging to A will belong to B and
	 * vice versa. The lines remain unchanged, since they have no owner.
	 * 
	 * @return The inverted board, swapping all attributes for player A and B.
	 */
	public GameStateSnapshot getInvertedSnapshot() {
		GameStateSnapshot s = new GameStateSnapshot(this);
		// Invert box-difference, since we swap player A and B
		s.boxDiff *= -1;

		// Now the other player has to move
		s.p *= -1;

		// invert box-owners, if used
		// TODO: How do we handle this?
		// if (boxOwner != null) {
		// for (int i = 0; i < m; i++)
		// for (int j = 0; j < n; j++)
		// boxOwner[i][j] *= -1;
		// }

		// The lines remain unchanged
		return s;
	}

	/**
	 * @return All mirrored snapshots of the current GameState. Since only
	 *         snapshots are returned, it is not possible to change any class
	 *         attributes or advance to a new state.
	 */
	public GameStateSnapshot[] getMirrorSymmetricSnapshots() {
		Set<GameStateSnapshot> hs = getMirrorSymmetricSnapshotsAsSet();
		return hs.toArray(new GameStateSnapshot[hs.size()]);
	}

	/**
	 * @return All mirrored snapshots of the current GameState. Since only
	 *         snapshots are returned, it is not possible to change any class
	 *         attributes or advance to a new state.
	 */
	private Set<GameStateSnapshot> getMirrorSymmetricSnapshotsAsSet() {
		long[][] boards = getMirrorSymmetricBoards();
		int length = boards.length;
		HashSet<GameStateSnapshot> hs = new HashSet<GameStateSnapshot>(length);
		for (int i = 0; i < length; i++)
			hs.add(new GameStateSnapshot(this, boards[i]));
		return hs;
	}

	public static GameStateSnapshot[] getCornerSymmetricSnapshots(GameStateSnapshot s, int m, int n) {
		// If we have n corners, which have one edge set, then we get
		// sum_i=0^n{binomial(n,i)} = 2^n symmetric boards. For n=4 (all corners
		// have
		// one edge set) we get up to 16 symmetric positions. binomial(4,0)
		// means
		// that we do not change any corner of the board. binomial(4,2) is the
		// number of symmetric boards, if we swap two arbitrary corners of the
		// board.
		ArrayList<long[]> sym = GameStateFactory.getCornerSymmetricLines(
				s.getLines(), m, n);
		int length = sym.size();
		HashSet<GameStateSnapshot> hs = new HashSet<GameStateSnapshot>(length);
		for (int i = 0; i < length; i++)
			hs.add(new GameStateSnapshot(s, sym.get(i)));
		return hs.toArray(new GameStateSnapshot[hs.size()]);
	}

	/**
	 * @return Get all symmetric snapshots of the current game-state. This also
	 *         includes the corner-symmetry described by Korf and Barker.
	 */
	public GameStateSnapshot[] getAllSymmetricSnapshots() {
		Set<GameStateSnapshot> hs = getAllSymmetricSnapshotsAsSet();
		return hs.toArray(new GameStateSnapshot[hs.size()]);
	}

	/**
	 * @return Get all symmetric snapshots of the current game-state. This also
	 *         includes the corner-symmetry described by Korf and Barker.
	 */
	private Set<GameStateSnapshot> getAllSymmetricSnapshotsAsSet() {
		// estimate size
		int size = 100;
		HashSet<GameStateSnapshot> hs = new HashSet<GameStateSnapshot>(size);

		GameStateSnapshot[] mirrorSym = getMirrorSymmetricSnapshots();

		// iterate through all mirrored boards and get the corner-symmetries for
		// them
		for (GameStateSnapshot i : mirrorSym) {
			GameStateSnapshot[] cornerSym = getCornerSymmetricSnapshots(i, m, n);
			// add corner-symmetric states to the set
			for (GameStateSnapshot j : cornerSym)
				hs.add(j);
		}
		return hs;
	}

	/**
	 * /** Checks for a given edge, if this edge is part of one of the 4 corners
	 * of the board. Every corner consists out of a vertical and one horizontal
	 * edge.
	 * 
	 * @param mv
	 * @return True, if the given edge is part of a corner, otherwise false;
	 */
	public boolean isCornerEdge(Move mv) {
		return isCornerEdge(mv.direction, mv.bitIndex);
	}

	/**
	 * Checks for a given edge, if this edge is part of one of the 4 corners of
	 * the board. Every corner consists out of a vertical and one horizontal
	 * edge.
	 * 
	 * @param direction
	 * @param bitIndex
	 * @return True, if the given edge is part of a corner, otherwise false;
	 */
	public boolean isCornerEdge(int direction, int bitIndex) {
		final int[][] corner = GameStateFactory.ALL_CORNERS[m - 1][n - 1];
		boolean b1 = false;
		for (int i = 0; i < 4 && !b1; i++) {
			// Check, if part of the i-th corner
			b1 = b1 || (bitIndex == corner[i][direction]);
		}
		return b1;
	}

	/**
	 * Checks, whether the given edge is part of a corner and returns the
	 * corner-number, if this is true. If the edge is not part of any corner, -1
	 * is returned.
	 * 
	 * @param direction
	 * @param bitIndex
	 * @return The corner-number (0-3) in case the given edge is part of a
	 *         corner, otherwise -1;
	 */
	public int getCorner(int direction, int bitIndex) {
		int[][] corner = GameStateFactory.ALL_CORNERS[m - 1][n - 1];
		for (int i = 0; i < 4; i++) {
			// Check, if part of i-th corner
			if (bitIndex == corner[i][direction])
				return i;
		}
		// Not part of a corner
		return -1;
	}

	/**
	 * /** Checks, whether the given edge is part of a corner and returns the
	 * corner-number, if this is true. If the edge is not part of any corner, -1
	 * is returned.
	 * 
	 * @param mv
	 *            The corresponding move, that selects the specified edge.
	 * @return The corner-number (0-3) in case the given edge is part of a
	 *         corner, otherwise -1;
	 */
	public int getCorner(Move mv) {
		return getCorner(mv.direction, mv.bitIndex);
	}

	public int linesHashCode() {
		return linesHashCode(lines);
	}

	public static int linesHashCode(long lines[]) {
		int hash = Arrays.hashCode(lines);
		// Arrays.hashCode is not sufficient. For subsequent states of the same
		// index in the hash-table is addressed. We do not really want this
		final int[] keys = GameStateFactory.RANDOM_KEY_LIST;
		final int count0 = Long.bitCount(lines[0]);
		final int count1 = Long.bitCount(lines[1] ^ keys[count0]);
		return hash ^ keys[count1];
	}

	public long[] getLines() {
		return lines.clone();
	}

	/**
	 * @return true, if the current state is a terminal state, so if all lines
	 *         are set and either of the players won or the game ended draw.
	 */
	public boolean isTerminal() {
		final long[] mask = GameStateFactory.ALL_LINES[m - 1][n - 1];
		return (mask[0] & ~lines[0]) == 0 && (mask[1] & ~lines[1]) == 0;
	}

	/**
	 * Retreives the winner of a terminal state, or the player with the most
	 * boxes for a non-terminal state.
	 * 
	 * @return +1, if player A possesses the most boxes;<br>
	 *         0, if both players have the same amount of boxes;<br>
	 *         -1, if player B possesses the most boxes
	 */
	public int getWinner() {
		return Integer.signum(boxDiff);
	}

	/**
	 * @return The player to move from this game-state.
	 */
	public int getPlayerToMove() {
		return p;
	}

	/**
	 * @return Number of boxes owned by player A subtracted by the number of
	 *         boxes owned by Player B
	 */
	public int getBoxDifference() {
		return boxDiff;
	}

	/**
	 * Set boxDiff to zero. Used only for test-purposes. When finding the best
	 * move from a given position, it is not necessary to know the current
	 * box-difference, both players should try to maximize the number of boxes
	 * they will occupy in future. A move that maximizes the future boxes
	 * occupied, is optimal. Therefore a tree-search for instance should find
	 * the best move, no matter what the current box-difference is.
	 */
	public void resetBoxDiff() {
		boxDiff = 0;
	}

	/**
	 * USE ONLY FOR TEST-PUPOSES. Since dots-and-boxes is an impartial game, it
	 * is not relevant, which players turn it is to find the optimal move. This
	 * method is used to test this property.
	 */
	public void invertPlayer() {
		p = (byte) -p;
	}
	
	/**
	 * In a few cases, one might want to change the player to move.
	 * @param p
	 */
	public void setPlayerToMove(int p) {
		this.p = (byte) p;
	}

	@Override
	public boolean equals(Object o) {
		// if (o instanceof GameStateSnapshot) {
		GameStateSnapshot s = (GameStateSnapshot) o;
		boolean b1 = linesEquals(s);
		boolean b2 = (boxDiff == s.boxDiff);
		boolean b3 = (p == s.p);
		boolean b4 = (m == s.m);
		boolean b5 = (n == s.n);
		boolean ret = (b1 && b2 && b3 && b4 && b5);
		// assume, that actions and s.actions are the same if ret == true
		// TODO: check this in an assert
		return ret;
		// }
		// return false;
	}

	public boolean linesEquals(GameStateSnapshot s) {
		return lines[0] == s.lines[0] && lines[1] == s.lines[1];
	}

	@Override
	public int hashCode() {
		// A position is identified by:
		// 1. The lines
		// 2. The box-difference
		// 3. player to move
		int hash = linesHashCode();
		hash ^= GameStateFactory.RANDOM_KEY_LIST[m * n + boxDiff];
		// Players are -1, +1
		hash ^= GameStateFactory.PLAYER_KEYS[p < 0 ? 0 : 1];
		return hash;
	}

	@Override
	public String toString() {
		return GameStateFactory.toString(lines, m, n);
	}

}
