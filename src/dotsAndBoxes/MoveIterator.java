package dotsAndBoxes;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Returns an Iterator through the Move-list of the current state. Make sure,
 * that the Move-list is not changed while iterating through it (for instance
 * change the corresponding state), otherwise the behavior is not defined and
 * errors will occur. If the corresponding game-state is changed, make sure that
 * the state is rolled back to its original state, before the next element is
 * requested from the iterator.
 * 
 * @author Markus Thill
 * 
 */
public class MoveIterator implements Iterator<Move> {
	/**
	 * 0: horizontal lines, 1: vertical lines.
	 */
	private int direction;
	private int[] i;
	private final ArrayList<ArrayList<Byte>> actions;
	/**
	 * When creating the iterator, make sure to save the size. Because it is not
	 * allowed to change the underlying list (actions) while iterating through
	 * it. If this is done, throw an exception.
	 */
	private final int[] size;

	/**
	 * Creates a new Iterator for the move-list of the state.
	 * 
	 * @param actions
	 */
	public MoveIterator(ArrayList<ArrayList<Byte>> actions) {
		direction = 0;
		i = new int[2];
		this.actions = actions;
		size = new int[2];
		size[0] = actions.get(0).size();
		size[1] = actions.get(1).size();
	}

	/**
	 * We want the iterator to swap the directions permanently, so that after a
	 * horizontal move always a vertical move will be returned by the iterator
	 * and vice versa.
	 */
	private void toggleDirection() {
		direction = 1 - direction;
	}

	/**
	 * @return Returns a new move (direction-index pair) and increments the
	 *         counter for the iterator for the specified direction.
	 */
	private Move getMove() {
		Move mv = new Move(direction, i[direction]);
		i[direction]++;
		return mv;
	}
	
	private void checkForChanges() {
		int size0 =  actions.get(0).size();
		int size1 =  actions.get(1).size();
		if( size0 != size[0] || size1 != size[1])
			throw new UnsupportedOperationException("The move-list was changed inbetween. Cannot iterate through the list further");
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		checkForChanges();
		
		// Check, if moves are available for the first direction
		boolean hasNext = i[direction] < actions.get(direction).size();

		// Check, if moves are available for the other direction
		return hasNext || i[1 - direction] < actions.get(1 - direction).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Move next() {
		checkForChanges();
		
		int size = actions.get(direction).size();
		Move mv;
		if (i[direction] < size) {
			mv = getMove();
			toggleDirection();
		} else {
			toggleDirection();
			mv = getMove();
		}
		return mv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove not supported!");
	}

}
