package dotsAndBoxes;

import java.util.ArrayList;
import java.util.Random;

import tools.DeepArrayFactory;

public class GameStateArray implements Cloneable {
	private static final Random RND = tools.SRandom.RND;
	private static final int NUM_BOX_EDGES = 4;

	// Which attributes describe the state of the game?
	// Player to move:
	// public static enum Player {PLAYER_A (1), PLAYER_B (-1);
	// private int p;
	// Player(int p) {
	// this.p = p;
	// }
	//
	// public final int value() {
	// return p;
	// }
	// };
	// Player to move
	int p; // +1 (A), -1 (B)

	int n, m; // Dimensions of the board (number of boxes in each direction)

	// First index: 0->Horizontal lines, 1->Vertical lines
	// Values of all lines are binary (0, 1).
	int[][][] lines;

	// Box-owner. For every box note, if it is occupied by A or B, or not yet
	// occupied (0,+1,-1). May not always be necessary and can be set to null
	// for higher performance. First index: column. Second index: row.
	int[][] boxOwner;

	// Count, how many lines of a box are occupied (max. 4). If a line is set,
	// typically 2 boxes are affected (except on the borders). If a value for a
	// box reaches the value 4, then it belongs to the player who made the last
	// move.
	// May not be needed always. Assign null then
	// May be difficult to use with Rotations, mirroring. Could assign null in
	// this case.
	int[][] boxCounter;

	// Difference of Boxes: Boxes_A - Boxes_B. Player A increments the value,
	// Player B decrements.
	int boxDiff;

	// Move list for this state
	// First index identifies the action
	// Second index is the corresponding list of coordinates on the board
	// Each action is identified by a 3-tuple:
	// 1. {0 for horizontal rows, 1 for vertical columns}
	// 2. {Select the line. 0 to n-1 or m-1}
	// 3. {Select element in the line}
	ArrayList<int[]> actions;

	/**
	 * Copy-Constructor. Takes about 2-3s on an i7 to make 10 mio. copies of a
	 * state.
	 * 
	 * @param s
	 */
	public GameStateArray(GameStateArray s) {
		// TODO: can be optimized in certain cases, since not all attributes are
		// always needed after the copy
		this.p = s.p;
		this.n = s.n;
		this.m = s.m;
		this.lines = DeepArrayFactory.clone(s.lines);
		this.boxOwner = DeepArrayFactory.clone(s.boxOwner);
		this.boxCounter = DeepArrayFactory.clone(s.boxCounter);
		this.boxDiff = s.boxDiff;
		// assume, that actions are not changed
		this.actions = new ArrayList<int[]>(s.actions);
	}

	/**
	 * Create an empty field with n x m boxes.
	 * 
	 * @param n
	 *            Horizontal number of boxes
	 * @param m
	 *            Vertical number of boxes
	 */
	public GameStateArray(int n, int m) {
		// Init player to move: Always A
		this.p = +1;
		this.n = n;
		this.m = m;

		// Create the lines array
		final int lineLengthHor = n;
		final int lineNumHor = m + 1;
		final int lineLengthVert = m;
		final int lineNumVert = n + 1;
		lines = new int[2][][];

		// Create all horizontal lines
		lines[0] = new int[lineNumHor][];
		for (int i = 0; i < lineNumHor; i++) {
			lines[0][i] = new int[lineLengthHor];
		}

		// create all vertical lines
		lines[1] = new int[lineNumVert][];
		for (int i = 0; i < lineNumVert; i++) {
			lines[1][i] = new int[lineLengthVert];
		}

		// Init Box-owner-array
		boxOwner = new int[n][m];

		// Init Box-Counter Array
		boxCounter = new int[n][m];

		// Box-Difference is zero in the beginning
		boxDiff = 0;

		// Generate all possible actions for the empty board
		actions = generateMoveList(n, m);
	}
	
	public void rotateLinesRight() {
		
	}

	public static ArrayList<int[]> generateMoveList(int n, int m) {
		// generate a simple list of all moves from the empty board
		// There are 2*n*m+n+m possible moves in the beginning
		int actionNum = 2 * n * m + n + m;
		ArrayList<int[]> actions = new ArrayList<int[]>(actionNum);

		// run through horizontal rows first
		// There are m+1 horizontal rows
		for (int i = 0; i <= m; i++) {
			// In each row there are n lines
			for (int j = 0; j < n; j++) {
				int[] action = new int[3];
				action[0] = 0; // horizontal line
				action[1] = i; // row-index of line
				action[2] = j; // column index of line
				actions.add(action);
			}
		}

		// Now run through vertical lines
		// There are n+1 vertical line-columns
		for (int i = 0; i <= n; i++) {
			// In each column are m lines
			for (int j = 0; j < m; j++) {
				int[] action = new int[3];
				action[0] = 1; // vertical line
				action[1] = i; // column-index of line
				action[2] = j; // row-index of line
				actions.add(action);
			}
		}
		assert actions.size() == actionNum;

		return actions;

	}

	// public int[] getNextMove(boolean random) {
	// return null;
	// }

	public boolean advance(boolean random) {
		// do only, if moves are left
		int actionsLeft = actions.size();
		if (actionsLeft > 0) {
			int actionIndex = (random ? RND.nextInt(actionsLeft) : 0);
			return advance(actionIndex);
		}
		return false;
	}

	// public boolean advance(int[] action) {
	// TODO: we need to overwride equals in order to remove an action from the
	// actionList
	// But maybe better not to provide this method
	// }

	public boolean advance(int actionIndex) {
		int[] action = actions.get(actionIndex);
		makeMove(action);
		// This action has to be removed from the movelist
		actions.remove(actionIndex);
		return true;
	}

	private void makeMove(int[] action) {
		makeMove(action[0], action[1], action[2]);
	}

	private void makeMove(int rowCol, int i, int j) {
		// make move
		lines[rowCol][i][j] = 1; // Binary states

		// Player only has to be swapped, if a box was closed
		boolean swapPlayer = adjustBoxCounters(rowCol, i, j);

		// invert player to move
		if (swapPlayer)
			p = -p;
	}

	/**
	 * @param horiz
	 *            Select horizontal lines if true, otherwise select vertical
	 *            lines.
	 * @param i
	 *            Select line-no. i.
	 * @param j
	 *            Select element j in the line
	 * @return could the move be performed? (legal move)
	 */
	// Do we need this at all?
	@SuppressWarnings("unused")
	private void makeMove(int i, int j, boolean horiz) {
		int index = (horiz ? 0 : 1);
		makeMove(index, i, j);
	}

	private boolean adjustBoxCounters(int rowCol, int i, int j) {
		boolean swapPlayer = true;
		// Typically a line affects two boxes
		if (rowCol == 0) {
			if (i < m) {
				boxCounter[j][i]++;
				if (boxCounter[j][i] == NUM_BOX_EDGES) {
					boxOwner[j][i] = p;
					boxDiff += p;
					swapPlayer = false;
				}
			}
			if (i > 0) {
				boxCounter[j][i - 1]++;
				if (boxCounter[j][i - 1] == NUM_BOX_EDGES) {
					boxOwner[j][i - 1] = p;
					boxDiff += p;
					swapPlayer = false;
				}
			}
		} else {
			// For vertical
			if (i < n) {
				boxCounter[i][j]++;
				if (boxCounter[i][j] == NUM_BOX_EDGES) {
					boxOwner[i][j] = p;
					boxDiff += p;
					swapPlayer = false;
				}
			}
			if (i > 0) {
				boxCounter[i - 1][j]++;
				if (boxCounter[i - 1][j] == NUM_BOX_EDGES) {
					boxOwner[i - 1][j] = p;
					boxDiff += p;
					swapPlayer = false;
				}
			}
		}
		return swapPlayer;
	}

	@Override
	public String toString() {
		String s = new String();

		// final int n = lines[0].length - 1;
		// final int m = lines[1].length - 1;

		int i, j;

		// Get the Board
		// For every box-row
		for (j = 0; j < m + 1; j++) {
			for (i = 0; i < n; i++) {
				// print horizontal lines first
				s = s + "+";
				s += (lines[0][j][i] != 0 ? "---" : "   ");
			}
			s += "+\n";

			for (i = 0; i < n + 1 && j < m; i++) {
				// now print vertical lines
				s += (lines[1][i][j] != 0 ? "| " : "  ");

				// print, A,B, acoording to who owns the box
				if (i < n && boxOwner[i][j] != 0) {
					s += (boxOwner[i][j] > 0 ? "A" : "B");
				}
				// If box is not occupied yet, write down the number of edges,
				// that are set.
				else if (i < n)
					s += (boxCounter[i][j] + "");
				s += " ";
			}
			s += "\n";
		}
		return s;

	}

	@Override
	protected Object clone() {
		// Clone all attributes of the Object

		return null;
	}

	protected GameStateArray fastClone() {
		// TODO: Maybe only clone a few attributes and set others to null, e.g.
		// boxCounter and boxOwner.
		return null;
	}

	public int getBoxDifference() {
		return boxDiff;
	}

	public int[][][] getLines() {
		return lines;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GameStateArray s = new GameStateArray(5, 3);

		// s.makeMove(0, 0, true);
		// s.makeMove(0, 1, true);
		// s.makeMove(2, 2, true);
		// s.makeMove(3, 2, true);
		// s.makeMove(0, 0, false);
		// s.makeMove(0, 1, false);
		// s.makeMove(2, 2, false);
		// s.makeMove(3, 2, false);
		// s.makeMove(1, 0, false);
		// s.makeMove(1, 0, true);
		// System.out.println(s);

		while (s.advance(true))
			System.out.println(s);
		System.out.println("boxDiff: " + s.getBoxDifference());

	}

}
