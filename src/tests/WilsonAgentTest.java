package tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import solvers.WilsonAgent;

import dotsAndBoxes.GameState;
import dotsAndBoxes.Move;

public class WilsonAgentTest {

	@Test
	public void test() {
		int m = 3, n = 3;
		GameState s = new GameState(m, n);
		WilsonAgent wa = new WilsonAgent(m, n);

		assertEquals(-3, wa.getValue(s), 0.001);

		List<Move> mvl = wa.getBestMoves(s);
		assertEquals(24, mvl.size());

		s.advance(new int[] { 1, 0, 0 });
		assertEquals(-3, wa.getValue(s), 0.001);

		s.advance(new int[] { 0, 1, 1 });
		assertEquals(-1, wa.getValue(s), 0.001);
		Move mv = wa.getBestMove(s);
		assertTrue(mv.equals(new Move(1, Byte.valueOf((byte) 25))));
		mvl = wa.getBestMoves(s);
		assertEquals(1, mvl.size());
		assertTrue(mvl.get(0).equals(new Move(1, Byte.valueOf((byte) 25))));

		s.advance(1, (byte) 25);
		assertEquals(-1, wa.getValue(s), 0.001);
		mvl = wa.getBestMoves(s);
		assertEquals(17, mvl.size());
		
		s.advance(new int[]{0,1,2});
		//System.out.println(s);
		//assertEquals(+1, wa.getValue(s), 0.001);
		mv = wa.getBestMove(s);
		//System.out.println(s);
		assertTrue(mv.equals(new Move(1, Byte.valueOf((byte) 9))));
		mvl = wa.getBestMoves(s);
		assertEquals(1, mvl.size());

		System.out.println(s);
	}

}
