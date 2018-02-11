package dotsAndBoxes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import tools.DeepArrayFactory;

/**
 * A complete representation of a Dots-And-Boxes game-state. In addition to a
 * {@link GameStateSnapshot}, this class extends the game-state by a list of
 * possible actions and a list of already performed move. The action-list
 * (move-list) has the advantage, that we do not have to search for all possible
 * move every time we change the state, which saves computation time. Obviously,
 * more memory is needed for a game-state, because of these two lists (already
 * performed actions and available actions), so that objects of
 * {@link GameState} should not be stored in hash-tables or similar
 * data-structures. For these kind of purposes refer to the
 * {@link GameStateSnapshot} which can easily be generated from a
 * {@link GameState}.<br>
 * This class provides all methods that are necessary for a typical game
 * (advance to a new state, undo actions, rotate/mirror position) and more.
 * 
 * @author Markus Thill
 * 
 */
public class GameState extends GameStateSnapshot implements Iterable<Move>, Serializable {
	private static final long serialVersionUID = 5677868232509340538L;

	// Box-owner. For every box note, if it is occupied by A or B, or not yet
	// occupied (0,+1,-1). May not always be necessary and can be set to null
	// for higher performance. First index: column. Second index: row.
	int[][] boxOwner;

	// Array containing two ArrayLists, one for vertical lines, one for
	// horizontal lines
	// Bit-Index is stored
	// TODO: Vector could (should) be faster than ArrayList...
	ArrayList<ArrayList<Byte>> actions;
	ArrayList<Move> undoStack;

	public static final GameState createGameState(int m, int n) {
		return new GameState(m, n, (byte) 1);
	}

	/**
	 * Only use for internal purposes.
	 * 
	 * @param m
	 * @param n
	 * @param dummy
	 */
	private GameState(int m, int n, byte dummy) {
		super(m, n);

		// Init Box-owner-array. only needed, if we want to visualize the board.
		boxOwner = new int[m][n];

		// Box-Difference is zero in the beginning
		boxDiff = 0;

		// Generate all possible actions for the empty board
		actions = GameStateFactory.generateMoveList(m, n);

		// Create a LIFO for the moves performed, in order to be possible to
		// rollback to a previous state
		undoStack = new ArrayList<>(actions.size());

		// if (USECHAINS)
		// chains = new ChainsList(this);

	}

	/**
	 * Create an empty field with n x m boxes.
	 * 
	 * @param n
	 *            Horizontal number of boxes
	 * @param m
	 *            Vertical number of boxes
	 */
	public GameState(int m, int n) {
		this(GameStateFactory.getGameState(m, n), false);

		// TODO: Use copy-constructor. Could be tricky because of the
		// cross-references
		// if (USECHAINS)
		// chains = new ChainsList(this);
	}

	public GameState(int m, int n, boolean fullInformation) {
		this(GameStateFactory.getGameState(m, n), !fullInformation);
		// TODO: Use copy-constructor. Could be tricky because of the
		// cross-references
		// if (USECHAINS)
		// chains = new ChainsList(this);
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param s
	 * @param fastCopy
	 */
	public GameState(GameState s, boolean fastCopy) {
		super(s);
		boxOwner = (fastCopy ? null : DeepArrayFactory.clone(s.boxOwner));
		actions = DeepArrayFactory.clone(s.actions);
		undoStack = Move.cloneMoveList(s.undoStack);

		// TODO: Use copy-constructor. Could be tricky because of the
		// cross-references
		// if (USECHAINS)
		// chains = new ChainsList(this);
	}

	/**
	 * Attempts to convert a game-snapshot into a real game-state again. For
	 * this purpose basically a legal move-list has to be generated again by
	 * analyzing the current board in order to determine all still available
	 * legal moves from this state. However, it is not possible to rollback
	 * further than this state, since the move-sequence that led to this state
	 * is unknown.
	 * 
	 */
	public GameState(GameStateSnapshot ss) {
		super(ss);

		actions = GameStateFactory.generateMoveList(ss.m, ss.n);

		// Now we have to remove all actions that were already performed
		// Start with horizontal. Start from the back of the list.
		ArrayList<Byte> h = actions.get(0);
		for (int i = h.size() - 1; i >= 0; i--) {
			Byte b = h.get(i);
			if (isSet(0, b))
				h.remove(i);
		}

		// Now do the same for the vertical lines
		ArrayList<Byte> v = actions.get(1);
		for (int i = v.size() - 1; i >= 0; i--) {
			Byte b = v.get(i);
			if (isSet(1, b))
				v.remove(i);
		}

		undoStack = new ArrayList<>(actions.size());

		// TODO: Use copy-constructor. Could be tricky because of the
		// cross-references
		// if (USECHAINS)
		// chains = new ChainsList(this);

	}

	public int boxesClosed(int direction, int bitIndex) {
		int closedBoxes = 0;
		long x = ~lines[0];
		long y = ~lines[1];
		long[][][] boxMasks = GameStateFactory.ALL_BOX_MASKS[m - 1][n - 1];
		long[] masks = boxMasks[direction][bitIndex];
		if ((x & masks[0]) == 0L && (y & masks[1]) == 0L)
			closedBoxes++;
		if (masks.length > 2 && (x & masks[2]) == 0L && (y & masks[3]) == 0L)
			closedBoxes++;
		return closedBoxes;
	}

	/**
	 * Return for two neighbored boxes (which are separated by the line created
	 * by bitIndex) or for one single box the owner(s). This method is used to
	 * check after a move, if the corresponding player captured one or two
	 * boxes.
	 * 
	 * @param boxes
	 *            one or two boxes (which necessarily have to be neighbored and
	 *            separated by the edge identified by direction and bitIndex).
	 *            The first index of this array selects the direction: 0 ->
	 *            horizontal, 1->vertical. The second
	 * @param direction
	 * @param bitIndex
	 * @return
	 */
	private boolean[] getBoxOwners(int[][] boxes, int direction, int bitIndex) {

		// box1 = {upperBox or left Box}
		// box2 = {upperBox or left Box}

		// TODO: Sort the boxes to left/upper and then right/lower

		// check for the box, if all
		// [hor/vert][BitIndex][horBox1,vertBox1, {horBox2,vertBox2}]
		// TODO: use getter-method....
		long[][][] boxMasks = GameStateFactory.ALL_BOX_MASKS[m - 1][n - 1];
		long masks[] = boxMasks[direction][bitIndex];
		boolean owners[] = new boolean[boxes.length];

		// First box corresponds to first masks
		long x = ~lines[0];
		long y = ~lines[1];
		if ((x & masks[0]) == 0L && (y & masks[1]) == 0L)
			owners[0] = true;
		if (masks.length > 2 && (x & masks[2]) == 0L && (y & masks[3]) == 0L)
			owners[1] = true;
		return owners;
	}

	public boolean createdCapturable(Move mv) {
		return createdCapturable(mv.direction, mv.bitIndex);
	}

	public boolean createdCapturable(int direction, int bitIndex) {
		// TODO: use getter-method....
		long masks[] = GameStateFactory.ALL_BOX_MASKS[m - 1][n - 1][direction][bitIndex];

		// Long.bitCount uses the assembler instruction popcnt
		int count1 = Long.bitCount(lines[0] & masks[0])
				+ Long.bitCount(lines[1] & masks[1]);
		int count2 = 0;
		if (masks.length > 2)
			count2 = Long.bitCount(lines[0] & masks[2])
					+ Long.bitCount(lines[1] & masks[3]);
		// Check, if three edges of both boxes are closed. In this case we
		// created at least one capturable box
		return (count1 == 3 || count2 == 3);
	}

	private int[][] getBoxes(int direction, int bitIndex) {
		return GameStateFactory.getBoxes(direction, bitIndex, m, n);
	}

	// TODO: element comment
	public boolean advance(boolean random) {
		int index = (random ? RND.nextInt(2) : 0);
		int size = actions.get(index).size();
		if (size == 0) {
			// The Action-list for one direction was empty, try the other
			// direction
			index = 1 - index;
			size = actions.get(index).size();
		}
		if (size != 0) {
			// much faster, to remove the last element of an ArrayList...
			int actionIndex = (random ? RND.nextInt(size) : size - 1);
			return advance(index, actionIndex);
		}
		return false;
	}

	/**
	 * Advance by one move, given by the parameter point. Note, that this is not
	 * the fastest operation, since a linear search has to be performed in the
	 * action-list in order to remove the action. For normal purposes (not time
	 * critical), this method can be used without any problems.
	 * 
	 * @param point
	 *            Point in the form {direction, x, y}. The array hs to contain
	 *            three elements.
	 * @return True, if the move could be performed, otherwise false.
	 */
	public boolean advance(int[] point) {
		byte bitIndex = (byte) GameStateFactory.getLineBitIndex(point, m, n);
		return advance(point[0], bitIndex);
	}

	// TODO: element comment
	public boolean advance(int direction, byte bitIndex) {
		// Needs a linear search, so maybe not the best method to use
		int actionIndex = actions.get(direction).indexOf(bitIndex);
		if (actionIndex == -1)
			return false;
		return advance(direction, actionIndex);
	}

	// TODO: element comment
	public boolean advance(int direction, int actionIndex) {
		return advance(new Move(direction, actionIndex));
	}

	// TODO: element comment
	public boolean advance(Move mv) {
		// If mv.allowUndo == true, then this move was already performed before,
		// we do not want this. Actually, if we try a move and then undo the
		// move again, but later find out that this was the best move, then we
		// have to repeat it again. So this assert is not helpful
		// assert (mv.allowUndo = false);

		//
		// if we set the actionIndex to -1, this means we do not know the
		// action-index. Therefore, perform a linear search and look for the
		// corresponding index.
		//
		boolean indexInRange = true;
		if (mv.actionIndex < 0) {
			indexInRange = false;
		}

		// Retrieve bit-Index from action-List
		Byte bitIndex = null;
		if (indexInRange && actions.get(mv.direction).size() > mv.actionIndex)
			bitIndex = actions.get(mv.direction).get(mv.actionIndex);
		else
			indexInRange = false;
		//
		// Test, if bitIndex was already determined before, and hope it is still
		// the same. Otherwise look-up the action-Index again in a linear
		// search.
		//
		if (!indexInRange || mv.bitIndex != null
				&& !mv.bitIndex.equals(bitIndex)) {
			//
			// perform a linear search and look-up the correct action-index
			//
			return advance(mv.direction, (byte) mv.bitIndex);
		}

		Byte removed = actions.get(mv.direction).remove(mv.actionIndex);
		assert (removed == bitIndex);

		// Set line (horizontal or vertical)
		setLine(mv.direction, bitIndex);

		// Adjust the Move-object, that contains all necessary information to
		// undo a move again
		// Now it is allowed to undo this move
		mv.set(bitIndex, boxDiff, boxOwner, p, true);

		// Adjust Box-Counters
		int boxesClosed = boxesClosed(mv.direction, bitIndex);
		boxDiff += p * boxesClosed;

		// Swap players, if necessary
		if (boxesClosed == 0)
			p = (byte) -p;

		// If we want to show the boxOwner...
		else if (boxOwner != null && boxesClosed != 0) {
			// Very slow, so only do if necessary
			boxOwner = DeepArrayFactory.clone(boxOwner);
			int[][] b = getBoxes(mv.direction, bitIndex);
			boolean owned[] = getBoxOwners(b, mv.direction, bitIndex);
			for (int i = 0; i < b.length; i++) {
				if (owned[i])
					boxOwner[b[i][0]][b[i][1]] = p;
			}
		}

		//
		// Add move to the Undo-Stack, so that it can be undone again later.
		//
		undoStack.add(mv);

		//
		// Check, if this move created/adjusted chains
		//
		// if (USECHAINS)
		// chains.addMove(mv);

		// If we do not have any exceptions, then we can return true
		return true;
	}

	/**
	 * Undo the previous move.
	 */
	public void undo() {
		// Get the last performed move from the Stack
		Move mv = undoStack.remove(undoStack.size() - 1);

		// We do not want to undo a move that has not be performed yet.
		assert (mv.allowUndo == true);

		// set to old Box-Owners
		boxOwner = mv.oldBoxOwner;

		// set to last player
		p = mv.oldP;

		// set to previous box-difference
		boxDiff = mv.oldBoxDiff;

		// remove line
		removeLine(mv.direction, mv.bitIndex);

		// Add move back to actionList
		actions.get(mv.direction).add(mv.actionIndex, mv.bitIndex);

		//
		// Adjust chains again, after undoing a move
		//
		// if (USECHAINS)
		// chains.undo();
	}

	/**
	 * 
	 * @return Returns an iterator through the move-list. The user can easily
	 *         iterate through all possible moves for the current state without
	 *         having to know the details of the move-list. The moves returned
	 *         by the iterator can easily be used to advance to a new state and
	 *         then roll back to the current state (E.g. trying moves in a
	 *         Minimax search). All returned moves are legal. Make sure however,
	 *         that the Move-list is not changed while iterating through the
	 *         list (for instance change the corresponding state by advancing
	 *         without undoing the move again before calling
	 *         {@link Iterator#next()}), otherwise the behavior is not defined
	 *         and errors will occur. If the corresponding game-state is
	 *         changed, make sure that the state is rolled back to its original
	 *         state, before the next element is requested from the iterator (so
	 *         always do an {@link GameState#undo(Move)} after an
	 *         {@link GameState#advance(Move)}).
	 */
	public MoveIterator iterator() {
		return new MoveIterator(actions);
	}

	/**
	 * Get a snapshot of the resulting after-state, that is created after the
	 * passed action is performed. The current game-state is not changed, this
	 * method returns a new object. It is not possible to advance further from
	 * this snapshot, only basic information about the after-state is provided..
	 * See {@link GameState#getGameStateSnapshot()} for further information about
	 * snap-shots.
	 * 
	 * @param direction
	 *            0: Horizontal lines, 1: Vertical lines.
	 * @param actionIndex
	 *            Select an action from the horizontal/vertical actionList.
	 * @return
	 */
	public GameStateSnapshot getAfterstateSnapshot(int direction,
			int actionIndex) {
		advance(direction, actionIndex);
		GameStateSnapshot s = getGameStateSnapshot();
		undo();
		return s;
	}

	public GameStateSnapshot[] getAfterStateSnapshots() {
		//Iterator<Move> i = iterator();
		GameStateSnapshot[] s = new GameStateSnapshot[actionsLeft()];
		int j = 0;
		for(Move mv : this) {
			advance(mv);
			s[j++] = getGameStateSnapshot();
			undo();
		}
		return s;
	}

	public int actionsLeft() {
		return actionsLeft(0) + actionsLeft(1);
	}

	/**
	 * Determines the actions left for one direction.
	 * 
	 * @param direction
	 *            0: Horizontal lines, 1: vertical lines
	 * @return
	 */
	// TODO: Maybe make direction an enum, to be safer
	public int actionsLeft(int direction) {
		return actions.get(direction).size();
	}

	public boolean isActionLeft() {
		return actionsLeft() > 0;
	}

	public boolean isActionLeft(int direction) {
		return actions.get(direction).size() > 0;
	}

	public int countLines() {
		return Long.bitCount(lines[0]) + Long.bitCount(lines[1]);
	}

	/**
	 * Snapshots of a board can be generated very fast. The returned objects are
	 * totally new generated, so later changes in the original game-state will
	 * not affect the snapshot. However, snapshots can not be changed anymore,
	 * thus, it is not possible to advance to other states. Also, snapshots do
	 * not provide information about which box belongs to which player
	 * (ownership), only the box-difference is provided.
	 * 
	 * @return A snapshot of the current game-state, providing all essential
	 *         information about the current state, such as dimensions of the
	 *         board, position of the lines, the player to move and the
	 *         box-count-difference.
	 */
	public GameStateSnapshot getGameStateSnapshot() {
		return new GameStateSnapshot(this);
	}

	/**
	 * @return The total number of boxes for this board.
	 */
	public int getBoxNum() {
		return m * n;
	}

	/**
	 * @return A list of actions performed, in order to create this situation.
	 *         NOTE: A Copy of the list is returned, which may be inefficient
	 *         for some purposes.
	 */
	public ArrayList<Move> getActionSequence() {
		return Move.cloneMoveList(undoStack);
	}

	public ArrayList<Byte> getActionSequenceAsBitIndexes() {
		ArrayList<Move> moves = undoStack;
		ArrayList<Byte> actionSequenceB = new ArrayList<Byte>(moves.size());

		Iterator<Move> i = moves.iterator();
		while (i.hasNext()) {
			Move mv = i.next();
			actionSequenceB.add(mv.getBitIndex());
		}
		return actionSequenceB;
	}

	/**
	 * @return Dimensions of the board. Returns an array containing the two
	 *         elements {0=m,1=n}.
	 */
	public int[] getBoardDimensions() {
		return new int[] { m, n };
	}

	// ----------------------------------------------------------------------------------------------------
	// All operations regarding the chains. Many are simply for diagnosis
	// purposes
	// ----------------------------------------------------------------------------------------------------
	// public int chainCount() {
	// return chains.chainCount();
	// }
	//
	// public int chainUndoStackSize() {
	// return chains.chainUndoStackCount();
	// }
	//
	// public Chain chainGet(int index) {
	// return chains.get(index);
	// }
	//
	// public List<ChainSegment> findStrategy(boolean hardHearted) {
	// return chains.findStrategy(hardHearted);
	// }

	// ----------------------------------------------------------------------------------------------------
	// Overwritten Methods
	// ----------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		// print board
		String s = new String();

		int i, j;

		// Get the Board
		// For every box-row
		for (j = 0; j < n + 1; j++) {
			for (i = 0; i < m; i++) {
				// print horizontal lines first
				s = s + "+";
				s += isSet(0, i, j) ? "---" : "   ";
			}
			s += "+\n";
			for (i = 0; i < m + 1 && j < n; i++) {
				// now print vertical lines
				s += isSet(1, i, j) ? "| " : "  ";

				// print, A,B, acoording to who owns the box
				if (boxOwner != null && i < m && boxOwner[i][j] != 0) {
					s += (boxOwner[i][j] > 0 ? "A" : "B");
				}
				// If box is not occupied yet, write down the number of edges,
				// that are set.
				else if (i < m)
					s += " ";
				s += " ";
			}
			// If we are in the middle, print some extra information
			if (j == n / 2) {
				s += "\t\tBox-difference: " + boxDiff;
			}
			if (j == n / 2 + 1) {
				s += "\t\tPlayer to move: " + (p == 1 ? "A" : "B");
			}
			s += "\n";
		}
		return s + "\n";
	}

	// public ArrayList<ArrayList<Byte>> getActions() {
	// return actions;
	// }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GameState s = new GameState(4, 3, true);
		// while (s.advance(true))
		// System.out.print(s);
		s.setLine(0, 11);
		// s.setLine(1, 4, 0);
		// s.setLine(0, 0, 1);
		System.out.print(s);

		s.flipVertical();
		System.out.print(s);

		// s.flipHorizontal();
		// System.out.print(s);

		// s.setLine(0, 1, 0);
		// s.setLine(0, 2, 0);
		// s.setLine(0, 1, 1);
		// s.setLine(0, 3, 3);
		// s.setLine(0, 2, 1);
		// s.setLine(0, 1, 5);

		// System.out.println(s.getHorLineBitIndex(1, 0));
		// s.closedBoxes(0, 0);
	}
}
