package tests;

import static org.junit.Assert.*;

import java.util.Iterator;
import org.junit.Test;
import dotsAndBoxes.GameState;
import dotsAndBoxes.Move;

public class MoveIteratorTest {

	@Test
	public void testMoveIterator() {
		// Just test, if iterator can be created...
		GameState s = new GameState(3, 3);
		@SuppressWarnings("unused")
		Iterator<Move> i = s.iterator();
	}

	@Test
	public void testHasNext() {
		GameState s = new GameState(3, 3);
		Iterator<Move> i = s.iterator();
		for(int k=0;k<2*3*4;k++) {
			assertTrue(i.hasNext());
			Move mv = i.next();
			s.advance(mv);
			s.undo();
		}
		assertFalse(i.hasNext());
	}

	@Test
	public void testNext() {
		GameState s = new GameState(7, 7);
		GameState s1 = new GameState(7, 7);
		Iterator<Move> i = s.iterator();

		int k = 0, j = 0, direction = 0;
		while (i.hasNext()) {
			s.advance(i.next());
			s1.advance(direction, j);
			j += direction;
			direction = 1 - direction;

			assertEquals(s, s1);
			s.undo();
			s1.undo();
			k++;
		}
		assertEquals(2 * 7 * 8, k);
	}

	@Test
	public void testCheckForChanges() {
		boolean thrown = false;
		GameState s = new GameState(7, 7);
		Iterator<Move> i = s.iterator();
		i.hasNext();
		i.next();
		s.advance(true);
		try {
			i.hasNext();
		} catch (UnsupportedOperationException e) {
			thrown = true;
		}
		assertTrue(thrown);
		try {
			i.next();
		} catch (UnsupportedOperationException e) {
			thrown = true;
		}

		i = s.iterator();
		i.hasNext();
		i.next();

	}

	@Test
	public void testRemove() {

	}

}
