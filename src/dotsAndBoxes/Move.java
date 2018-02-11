package dotsAndBoxes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;

/**
 * When a game state {@link GameState} advances to a new state, then all
 * {@link GameState#advance()} methods return a Move-object. This object
 * basically returns all information about the old state and the action
 * performed. This information is needed to undo a move again later. The
 * visibility of all attributes of this class is private, to ensure that the
 * attributes are not changed and the rollback to a previous state can be
 * performed without problems.
 * 
 * @author Markus Thill
 * 
 */
public class Move implements Comparable<Move> {
	protected final int direction;
	protected final int actionIndex;
	protected Byte bitIndex;
	protected short oldBoxDiff;
	protected int[][] oldBoxOwner;
	protected byte oldP;
	protected boolean allowUndo;

	public Move(int direction, Byte bitIndex) {
		this.direction = direction;
		this.bitIndex = Byte.valueOf(bitIndex);
		// Set action-index to -1. A linear search has to be performed, in order
		// to find the corresponding action in the action-List
		this.actionIndex = -1;
	}

	public Move(Move mv) {
		direction = mv.direction;
		actionIndex = mv.actionIndex;
		bitIndex = mv.bitIndex;
		oldBoxDiff = mv.oldBoxDiff;
		oldBoxOwner = mv.oldBoxOwner;
		oldP = mv.oldP;
		allowUndo = mv.allowUndo;
	}

	public Move(int direction, int actionIndex) {
		this.direction = direction;
		this.actionIndex = actionIndex;
		// When generating a move like this, we do not want to allow someone
		// to undo this move, since this move was not performed yet. If the
		// advance Method is called with this Move-Object, the allow-Undo
		// attribute will be set to true.
		allowUndo = false;
	}

	void set(Byte bitIndex, short oldBoxDiff, int[][] oldBoxOwner, byte oldP,
			boolean allowUndo) {
		this.bitIndex = bitIndex;
		this.oldBoxDiff = oldBoxDiff;
		this.oldBoxOwner = oldBoxOwner;
		this.oldP = oldP;
		this.allowUndo = allowUndo;
	}

	public Byte getBitIndex() {
		return bitIndex;
	}

	public static ArrayList<Move> cloneMoveList(ArrayList<Move> x) {
		int size = x.size();
		ArrayList<Move> y = new ArrayList<Move>(size);
		for (Move i : x)
			y.add(new Move(i));
		return y;
	}

	@SuppressWarnings("unused")
	private static Deque<Move> cloneUndoMoveStack(Deque<Move> x) {
		int size = x.size();
		Deque<Move> y = new ArrayDeque<Move>(size);
		for (Move i : x)
			y.add(new Move(i));
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Move) {
			Move mv = (Move) o;
			// two moves are considered to be equal here, if the same line
			// on the board are set. Nothing else.
			return (mv.bitIndex.equals(bitIndex) && mv.direction == direction);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new int[] { direction, bitIndex });
	}

	@Override
	public String toString() {
		return "(" + direction + "," + bitIndex + ")";
	}

	@Override
	public int compareTo(Move o) {
		Move mv = (Move) o;
		if (equals(mv))
			return 0;
		return -1;
	}
}