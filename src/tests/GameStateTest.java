package tests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateSnapshot;
import dotsAndBoxes.GameStateFactory;
import dotsAndBoxes.Move;

public class GameStateTest {
	
	@Test
	public void testCornerSymmetry1x2() {
		int m =1, n =2;
		GameState s = new GameState(m, n);
		s.advance(1, (byte)0);
		@SuppressWarnings("unused")
		GameStateSnapshot[] x = GameState.getCornerSymmetricSnapshots(s, m, n);
		//TODO: 
		
	}

//	@Test
//	public void testFindChain() {
//		int m = 3, n = 3;
//		
//		GameState s = new GameState(m, n);
//		ChainsList c = new ChainsList(s);
//		ChainSegment cs = new ChainSegment(0, 0);
//		ArrayList<Chain> x = c.findChain(cs);
//		assertTrue(x == null);
//
//		s.advance(0, (byte) 16);
//		s.advance(0, (byte) 17);
//		s.advance(1, (byte) 3);
//		cs = new ChainSegment(1, 3);
//		x = c.findChain(cs);
//		assertEquals(1, x.size());
//		assertEquals(1, x.get(0).size());
//
//		s.advance(0, (byte) 24);
//		s.advance(0, (byte) 25);
//		cs = new ChainSegment(1, 3);
//		x = c.findChain(cs);
//		assertEquals(1, x.size());
//		assertEquals(2, x.get(0).size());
//		ChainSegment[] xx = x.get(0).toArray();
//		assertEquals(1, xx[0].direction);
//		assertEquals(1, xx[0].bitIndex);
//		assertEquals(1, xx[1].direction);
//		assertEquals(2, xx[1].bitIndex);
//		//
//
//		//
//		s.advance(0, (byte) 3);
//		s.advance(1, (byte) 0);
//		cs = new ChainSegment(1, 3);
//		x = c.findChain(cs);
//		xx = x.get(0).toArray();
//		assertEquals(0, xx[0].direction);
//		assertEquals(2, xx[0].bitIndex);
//		assertEquals(1, xx[1].direction);
//		assertEquals(1, xx[1].bitIndex);
//		assertEquals(1, xx[2].direction);
//		assertEquals(2, xx[2].bitIndex);
//		assertEquals(Chain.ChainType.HALFOPEN,x.get(0).getType());
//		//
//
//		//
//		s.advance(1, (byte) 8);
//		s.advance(1, (byte) 9);
//		cs = new ChainSegment(1, 3);
//		x = c.findChain(cs);
//		xx = x.get(0).toArray();
//		assertEquals(0, xx[0].direction);
//		assertEquals(1, xx[0].bitIndex);
//		assertEquals(0, xx[1].direction);
//		assertEquals(2, xx[1].bitIndex);
//		assertEquals(1, xx[2].direction);
//		assertEquals(1, xx[2].bitIndex);
//		assertEquals(1, xx[3].direction);
//		assertEquals(2, xx[3].bitIndex);
//		assertEquals(Chain.ChainType.HALFOPEN,x.get(0).getType());
//		//
//
//		//
//		s.advance(1, (byte) 19);
//		s.advance(0, (byte) 0);
//		cs = new ChainSegment(1, 3);
//		x = c.findChain(cs);
//		xx = x.get(0).toArray();
//		assertEquals(1, xx[0].direction);
//		assertEquals(18, xx[0].bitIndex);
//		assertEquals(0, xx[1].direction);
//		assertEquals(1, xx[1].bitIndex);
//		assertEquals(0, xx[2].direction);
//		assertEquals(2, xx[2].bitIndex);
//		assertEquals(1, xx[3].direction);
//		assertEquals(1, xx[3].bitIndex);
//		assertEquals(1, xx[4].direction);
//		assertEquals(2, xx[4].bitIndex);
//		assertEquals(Chain.ChainType.HALFOPEN,x.get(0).getType());
//		//
//
//		//
//		s.advance(0, (byte) 8);
//		s.advance(0, (byte) 9);
//		cs = new ChainSegment(1, 3);
//		x = c.findChain(cs);
//		xx = x.get(0).toArray();
//		assertEquals(1, xx[0].direction);
//		assertEquals(17, xx[0].bitIndex);
//		assertEquals(1, xx[1].direction);
//		assertEquals(18, xx[1].bitIndex);
//		assertEquals(0, xx[2].direction);
//		assertEquals(1, xx[2].bitIndex);
//		assertEquals(0, xx[3].direction);
//		assertEquals(2, xx[3].bitIndex);
//		assertEquals(1, xx[4].direction);
//		assertEquals(1, xx[4].bitIndex);
//		assertEquals(1, xx[5].direction);
//		assertEquals(2, xx[5].bitIndex);
//		assertEquals(Chain.ChainType.HALFOPEN,x.get(0).getType());
//		//
//
//		//
//		s.advance(0, (byte) 19);
//		s.advance(1, (byte) 16);
//		cs = new ChainSegment(1, 3);
//		x = c.findChain(cs);
//		xx = x.get(0).toArray();
//		assertEquals(0, xx[0].direction);
//		assertEquals(18, xx[0].bitIndex);
//		assertEquals(1, xx[1].direction);
//		assertEquals(17, xx[1].bitIndex);
//		assertEquals(1, xx[2].direction);
//		assertEquals(18, xx[2].bitIndex);
//		assertEquals(0, xx[3].direction);
//		assertEquals(1, xx[3].bitIndex);
//		assertEquals(0, xx[4].direction);
//		assertEquals(2, xx[4].bitIndex);
//		assertEquals(1, xx[5].direction);
//		assertEquals(1, xx[5].bitIndex);
//		assertEquals(1, xx[6].direction);
//		assertEquals(2, xx[6].bitIndex);
//		assertEquals(Chain.ChainType.HALFOPEN,x.get(0).getType());
//		//
//
//		//
//		s.advance(1, (byte) 24);
//		cs = new ChainSegment(1, 3);
//		x = c.findChain(cs);
//		xx = x.get(0).toArray();
//		assertEquals(1, xx[0].direction);
//		assertEquals(25, xx[0].bitIndex);
//		assertEquals(0, xx[1].direction);
//		assertEquals(18, xx[1].bitIndex);
//		assertEquals(1, xx[2].direction);
//		assertEquals(17, xx[2].bitIndex);
//		assertEquals(1, xx[3].direction);
//		assertEquals(18, xx[3].bitIndex);
//		assertEquals(0, xx[4].direction);
//		assertEquals(1, xx[4].bitIndex);
//		assertEquals(0, xx[5].direction);
//		assertEquals(2, xx[5].bitIndex);
//		assertEquals(1, xx[6].direction);
//		assertEquals(1, xx[6].bitIndex);
//		assertEquals(1, xx[7].direction);
//		assertEquals(2, xx[7].bitIndex);
//		assertEquals(Chain.ChainType.CLOSED,x.get(0).getType());
//		//
//
//		// New Test
//		//
//		s = new GameState(m, n);
//		s.advance(0, (byte) 16);
//		s.advance(0, (byte) 24);
//		s.advance(0, (byte) 3);
//		s.advance(0, (byte) 17);
//		s.advance(0, (byte) 25);
//		s.advance(1, (byte) 8);
//		s.advance(1, (byte) 9);
//		s.advance(1, (byte) 0);
//		s.advance(1, (byte) 18);
//		s.advance(1, (byte) 19);
//
//		s.advance(0, (byte) 2);
//		c = new ChainsList(s);
//		cs = new ChainSegment(0, 2);
//		x = c.findChain(cs);
//		assertEquals(2, x.size());
//		xx = x.get(0).toArray();
//		assertEquals(1, xx[0].direction);
//		assertEquals(3, xx[0].bitIndex);
//		assertEquals(1, xx[1].direction);
//		assertEquals(2, xx[1].bitIndex);
//		assertEquals(1, xx[2].direction);
//		assertEquals(1, xx[2].bitIndex);
//		assertEquals(Chain.ChainType.HALFOPEN,x.get(0).getType());
//
//		xx = x.get(1).toArray();
//		assertEquals(0, xx[0].direction);
//		assertEquals(0, xx[0].bitIndex);
//		assertEquals(0, xx[1].direction);
//		assertEquals(1, xx[1].bitIndex);
//		assertEquals(Chain.ChainType.HALFOPEN,x.get(0).getType());
//
//		//
//		// Try a loop
//		//
//		s = new GameState(m, n);
//		c = new ChainsList(s);
//		s.advance(0, (byte) 16);
//		s.advance(0, (byte) 24);
//		s.advance(0, (byte) 3);
//		s.advance(0, (byte) 19);
//		s.advance(0, (byte) 8);
//		s.advance(0, (byte) 0);
//		s.advance(0, (byte) 25);
//		s.advance(0, (byte) 9);
//		s.advance(1, (byte) 3);
//		s.advance(1, (byte) 24);
//		s.advance(1, (byte) 16);
//		s.advance(1, (byte) 0);
//		s.advance(1, (byte) 8);
//		s.advance(1, (byte) 19);
//		s.advance(1, (byte) 25);
//		s.advance(1, (byte) 9);
//		
//		s.advance(1, (byte) 18);
//		cs = new ChainSegment(1, 18);
//		x = c.findChain(cs);
//		assertEquals(1, x.size());
//		xx = x.get(0).toArray();
//		assertEquals(0, xx[0].direction);
//		assertEquals(1, xx[0].bitIndex);
//		assertEquals(0, xx[1].direction);
//		assertEquals(2, xx[1].bitIndex);
//		assertEquals(1, xx[2].direction);
//		assertEquals(1, xx[2].bitIndex);
//		assertEquals(1, xx[3].direction);
//		assertEquals(2, xx[3].bitIndex);
//		assertEquals(0, xx[4].direction);
//		assertEquals(17, xx[4].bitIndex);
//		assertEquals(0, xx[5].direction);
//		assertEquals(18, xx[5].bitIndex);
//		assertEquals(1, xx[6].direction);
//		assertEquals(17, xx[6].bitIndex);
//		assertEquals(Chain.ChainType.CLOSED,x.get(0).getType());
//
//		System.out.println(s);
//		
//		// Divide loop into two segments. This leads to two closed chains
//		s.advance(1, (byte) 2);
//		cs = new ChainSegment(1, 2);
//		x = c.findChain(cs);
//		assertEquals(2, x.size());
//		xx = x.get(0).toArray();
//		
//		System.out.println(s);
//		assertEquals(1, xx[0].direction);
//		assertEquals(17, xx[0].bitIndex);
//		assertEquals(0, xx[1].direction);
//		assertEquals(18, xx[1].bitIndex);
//		assertEquals(0, xx[2].direction);
//		assertEquals(17, xx[2].bitIndex);
//		assertEquals(Chain.ChainType.CLOSED,x.get(0).getType());
//		//
//		xx = x.get(1).toArray();
//		assertEquals(0, xx[0].direction);
//		assertEquals(1, xx[0].bitIndex);
//		assertEquals(0, xx[1].direction);
//		assertEquals(2, xx[1].bitIndex);
//		assertEquals(1, xx[2].direction);
//		assertEquals(1, xx[2].bitIndex);
//		assertEquals(Chain.ChainType.CLOSED,x.get(1).getType());
//		
//	}

	@Test
	public void testGetHorLineCoord() {
		int m = 3, n = 3;
		int b = GameStateFactory.getHorLineBitIndex(0, 0, m, n);
		int[] c = GameStateFactory.getHorLineCoord(b, m, n);
		assertEquals(0, c[0]);
		assertEquals(0, c[1]);

		b = GameStateFactory.getHorLineBitIndex(1, 0, m, n);
		c = GameStateFactory.getHorLineCoord(b, m, n);
		assertEquals(1, c[0]);
		assertEquals(0, c[1]);

		for (int i = 1; i <= GameState.MAX_DIMENSION; i++)
			for (int j = 1; j <= GameState.MAX_DIMENSION; j++) {
				for (int ii = 0; ii < i; ii++)
					for (int jj = 0; jj <= j; jj++) {
						b = GameStateFactory.getHorLineBitIndex(ii, jj, i, j);
						c = GameStateFactory.getHorLineCoord(b, i, j);
						assertEquals(ii, c[0]);
						assertEquals(jj, c[1]);
					}
			}
	}

	@Test
	public void testGetVertLineCoord() {
		int m = 3, n = 3;
		int b = GameStateFactory.getVertLineBitIndex(0, 0, m, n);
		int[] c = GameStateFactory.getVertLineCoord(b, m, n);
		assertEquals(0, c[0]);
		assertEquals(0, c[1]);

		b = GameStateFactory.getVertLineBitIndex(1, 0, m, n);
		c = GameStateFactory.getVertLineCoord(b, m, n);
		assertEquals(1, c[0]);
		assertEquals(0, c[1]);

		for (int i = 1; i <= GameState.MAX_DIMENSION; i++)
			for (int j = 1; j <= GameState.MAX_DIMENSION; j++) {
				for (int ii = 0; ii <= i; ii++)
					for (int jj = 0; jj < j; jj++) {
						b = GameStateFactory.getVertLineBitIndex(ii, jj, i, j);
						c = GameStateFactory.getVertLineCoord(b, i, j);
						assertEquals(ii, c[0]);
						assertEquals(jj, c[1]);
					}
			}
	}

	@Test
	public void testCreatedCapturable() {
		int m = 3, n = 3;
		GameState s = new GameState(m, n);
		s.advance(1, (byte) 3);
		assertFalse(s.createdCapturable(1, 3));
		s.advance(1, (byte) 2);
		assertFalse(s.createdCapturable(1, 2));
		s.advance(0, (byte) 17);
		assertTrue(s.createdCapturable(0, 17));
		s.advance(0, (byte) 16);
		assertFalse(s.createdCapturable(0, 16));

		s.advance(0, (byte) 24);
		assertFalse(s.createdCapturable(0, 24));
		s.advance(0, (byte) 9);
		assertFalse(s.createdCapturable(0, 9));
		s.advance(1, (byte) 25);
		assertFalse(s.createdCapturable(1, 25));
		s.advance(0, (byte) 25);
		assertTrue(s.createdCapturable(0, 25));
		s.advance(1, (byte) 1);
		assertFalse(s.createdCapturable(1, 1));
		s.advance(1, (byte) 9);
		assertFalse(s.createdCapturable(1, 9));

		s.advance(0, (byte) 18);
		assertTrue(s.createdCapturable(0, 18));
		s.advance(1, (byte) 24);
		assertFalse(s.createdCapturable(1, 24));

		s.advance(1, (byte) 17);
		assertFalse(s.createdCapturable(1, 17));
		s.advance(0, (byte) 19);
		assertTrue(s.createdCapturable(0, 19));
		s.advance(1, (byte) 16);
		assertFalse(s.createdCapturable(1, 16));

		s.advance(0, (byte) 3);
		assertFalse(s.createdCapturable(0, 3));
		s.advance(0, (byte) 1);
		assertFalse(s.createdCapturable(0, 1));
		s.advance(0, (byte) 2);
		assertTrue(s.createdCapturable(0, 2));
		s.advance(1, (byte) 0);
		assertFalse(s.createdCapturable(1, 0));
		s.advance(1, (byte) 8);
		assertFalse(s.createdCapturable(1, 8));

		s.advance(1, (byte) 18);
		assertTrue(s.createdCapturable(1, 18));
		s.advance(0, (byte) 8);
		assertFalse(s.createdCapturable(0, 8));
		s.advance(0, (byte) 0);
		assertTrue(s.createdCapturable(0, 0));
		s.advance(1, (byte) 19);
		assertFalse(s.createdCapturable(1, 19));

		System.out.println(s);
	}

	@Test
	public void testSnapshotToState() {
		int m = 3, n = 3;
		GameState s = new GameState(m, n);
		for (int i = 0; i < 5; i++)
			s.advance(true);

		GameStateSnapshot ss = s.getGameStateSnapshot();
		GameState s1 = new GameState(ss);

		assertEquals(s.actionsLeft(), s1.actionsLeft());
		assertTrue(s1.equals(s1));

		// Now perform all remaining moves
		while (s1.advance(true))
			;
		assertTrue(s1.isTerminal());
	}

	@Test
	public void testGetAllSymmetricSnapshots() {
		int m = 3, n = 3;
		GameState s = new GameState(m, n);
		GameStateSnapshot[] ss = s.getAllSymmetricSnapshots();
		assertEquals(1, ss.length);

		s.advance(1, (byte) 0);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(8, ss.length);

		s.advance(1, (byte) 3);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(16, ss.length);

		s.advance(1, (byte) 16);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(32, ss.length);

		s.advance(1, (byte) 1);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(64, ss.length);

		s.advance(1, (byte) 17);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(64, ss.length);
		//

		m = 7;
		n = 7;
		s = new GameState(m, n);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(1, ss.length);

		s.advance(1, (byte) 0);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(8, ss.length);

		s.advance(1, (byte) 7);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(16, ss.length);

		s.advance(1, (byte) 24);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(32, ss.length);

		s.advance(1, (byte) 31);
		ss = s.getAllSymmetricSnapshots();
		assertEquals(32, ss.length);

		int most = 0;
		m = 3;
		n = 3;
		boolean printed = false;
		for (int i = 0; i < 1000; i++) {
			s = new GameState(m, n);
			while (s.advance(true)) {
				ss = s.getAllSymmetricSnapshots();
				most = Math.max(most, ss.length);
				if (most > 100 && !printed) {
					for (GameStateSnapshot j : ss)
						System.out.println(j);
					printed = true;
				}
			}
		}

		System.out.println("Most number of symmetric positions: " + most);

		// for(GameStateSnapshot i : ss)
		// System.out.println(i);
	}

	@Test
	public void testSwapCornerEdge() {
		int m = 3, n = 3;
		//
		GameStateSnapshot s = new GameStateSnapshot(m, n);
		s.setLine(0, 0);
		long[] lines = s.getLines();

		GameStateFactory.swapCornerEdge(lines, 0, m, n);
		assertEquals(lines[0], 1L);
		assertEquals(lines[1], 0L);

		GameStateFactory.swapCornerEdge(lines, 1, m, n);
		assertEquals(lines[0], 1L);
		assertEquals(lines[1], 0L);

		GameStateFactory.swapCornerEdge(lines, 2, m, n);
		assertEquals(lines[0], 1L);
		assertEquals(lines[1], 0L);

		GameStateFactory.swapCornerEdge(lines, 3, m, n);
		assertEquals(lines[0], 0L);
		assertEquals(lines[1], 1L << 19);

		//
		s = new GameStateSnapshot(m, n);
		s.setLine(0, 19);
		lines = s.getLines();
		GameStateFactory.swapCornerEdge(lines, 2, m, n);
		assertEquals(lines[0], 0L);
		assertEquals(lines[1], 1L << 16);

		//
		s = new GameStateSnapshot(m, n);
		s.setLine(0, 3);
		lines = s.getLines();
		GameStateFactory.swapCornerEdge(lines, 1, m, n);
		assertEquals(lines[0], 0L);
		assertEquals(lines[1], 1L << 0);

		//
		s = new GameStateSnapshot(m, n);
		s.setLine(0, 16);
		lines = s.getLines();
		GameStateFactory.swapCornerEdge(lines, 0, m, n);
		assertEquals(lines[0], 0L);
		assertEquals(lines[1], 1L << 3);

		m = 5;
		n = 3;

		s = new GameStateSnapshot(m, n);
		s.setLine(1, 0);
		lines = s.getLines();
		GameStateFactory.swapCornerEdge(lines, 1, m, n);
		assertEquals(lines[0], 1L << 3);
		assertEquals(lines[1], 0L);

		s = new GameStateSnapshot(m, n);
		s.setLine(1, 5);
		lines = s.getLines();
		GameStateFactory.swapCornerEdge(lines, 0, m, n);
		assertEquals(lines[0], 1L << 24);
		assertEquals(lines[1], 0L);

		s = new GameStateSnapshot(m, n);
		s.setLine(1, 16);
		lines = s.getLines();
		GameStateFactory.swapCornerEdge(lines, 2, m, n);
		assertEquals(lines[0], 1L << 27);
		assertEquals(lines[1], 0L);

		s = new GameStateSnapshot(m, n);
		s.setLine(1, 21);
		lines = s.getLines();
		GameStateFactory.swapCornerEdge(lines, 3, m, n);
		assertEquals(lines[0], 1L << 0);
		assertEquals(lines[1], 0L);

		// System.out.println(GameStateFactory.toString(lines, m, n));
		// System.out.println(s);
	}

	@Test
	public void testGetCornerSymmetricLines() {
		ArrayList<long[]> result = GameStateFactory.getCornerSymmetricLines(
				new long[2], 3, 3);
		assertEquals(1, result.size()); // only 1 symmetric position for empty
										// board

		int m = 3, n = 3;

		//
		GameState s = new GameState(m, n);
		s.setLine(0, 0);
		long[] lines = s.getLines();
		result = GameStateFactory.getCornerSymmetricLines(lines, m, n);
		assertEquals(2, result.size());
		//

		//
		s = new GameState(m, n);
		s.advance(1, (byte) 0);
		s.advance(1, (byte) 3);
		s.advance(1, (byte) 16);
		s.advance(1, (byte) 19);
		lines = s.getLines();
		result = GameStateFactory.getCornerSymmetricLines(lines, m, n);
		assertEquals(16, result.size());
		for (long[] i : result) {
			System.out.println(GameStateFactory.toString(i, m, n));
		}
		//

		//
		s = new GameState(m, n);
		s.advance(1, (byte) 0);
		s.advance(1, (byte) 3);
		s.advance(1, (byte) 16);
		lines = s.getLines();
		result = GameStateFactory.getCornerSymmetricLines(lines, m, n);
		assertEquals(8, result.size());
		//

		//
		s = new GameState(m, n);
		s.advance(1, (byte) 0);
		s.advance(1, (byte) 3);
		lines = s.getLines();
		result = GameStateFactory.getCornerSymmetricLines(lines, m, n);
		assertEquals(4, result.size());
		//

		// System.out.println(s);
		// System.out.println(GameStateFactory.toString(lines, m, n));
	}

	// @Test
	// public void testRandomStep() {
	// // Visual test
	// int m = 4, n = 3;
	// GameStateSnapshot s = new GameStateSnapshot(m, n);
	// int[] p = null, oldP = null;
	// int bitIndex;
	// while (s.actionsLeft()) {
	// do {
	// p = GameStateFactory.randomStep(oldP, m, n);
	// bitIndex = GameStateFactory.getLineBitIndex(p, m, n);
	// } while (!s.advance(p[0], (byte) bitIndex));
	// oldP = p;
	// System.out.println(s);
	// }
	// }

	@Test
	public void testIsCornerEdge() {
		GameStateSnapshot s = new GameStateSnapshot(3, 2);
		int b = s.getHorLineBitIndex(0, 0);
		assertTrue(s.isCornerEdge(0, b));
		b = s.getHorLineBitIndex(2, 0);
		assertTrue(s.isCornerEdge(0, b));
		b = s.getHorLineBitIndex(0, 2);
		assertTrue(s.isCornerEdge(0, b));
		b = s.getHorLineBitIndex(2, 2);
		assertTrue(s.isCornerEdge(0, b));
	}

	@Test
	public void testActionListOrdering() {
		GameState s = new GameState(3, 3);
		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.println(s);
			s.undo();
		}
	}

	@Test
	public void testActionList() {
		// For all Game-States
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 7; j++) {
				ArrayList<ArrayList<Byte>> a = GameStateFactory
						.generateMoveList(i + 1, j + 1);
				List<Byte> list = a.get(0);
				Set<Byte> uniqueSet = new HashSet<Byte>(list);
				for (Byte temp : uniqueSet) {
					// System.out.println(temp + ": " +
					// Collections.frequency(list, temp));
					assertEquals(1, Collections.frequency(list, temp));
				}
				// System.out.println();
			}
	}

	@Test
	public void testAfterStateSnapshot() {
		GameState s = new GameState(3, 3);
		GameStateSnapshot s1 = s.getAfterstateSnapshot(0, 0);
		GameState s2 = new GameState(3, 3);
		s2.advance(0, 0);

		assertNotSame(s, s1);
		assertEquals(s2, s1);

		s1 = s2.getAfterstateSnapshot(1, 2);
		s2.advance(1, 2);
		assertEquals(s1, s2);

		while (s2.isActionLeft(1)) {
			s1 = s2.getAfterstateSnapshot(1, 0);
			s2.advance(1, 0);
			assertEquals(s1, s2);
		}

		while (s2.isActionLeft(0)) {
			s1 = s2.getAfterstateSnapshot(0, 0);
			s2.advance(0, 0);
			assertEquals(s1, s2);
		}

	}

	@Test
	public void testUndo() {
		GameState s = new GameState(3, 3);
		GameState s1 = new GameState(s, false);
		s.advance(true);
		s.undo();
		assertEquals(s1, s);
		ArrayList<GameState> ss = new ArrayList<GameState>();
		while (!s.isTerminal()) {
			// add old state to the list
			ss.add(new GameState(s, false));
			s.advance(true);
		}

		// Now undo all moves again, and check if the this is done correctly
		while (s.actionsLeft() < s.actionsLeft()) {
			s.undo();
			assertEquals(ss.remove(ss.size() - 1), s);
		}
	}

	@Test
	public void testAdvance() {
		int numIterations = 10000;
		long start = System.nanoTime(), end;
		for (int i = 0; i < numIterations; i++) {
			GameState s = new GameState(4, 3);
			while (s.advance(true))
				;

		}
		end = System.nanoTime();
		long time = end - start;
		System.out.println("Time to play " + numIterations / 1e6
				+ " mio. random games " + time / 1e6 + "ms");

		// non-random
		start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			GameState s = new GameState(4, 3);
			while (s.advance(false))
				;

		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("Time to play " + numIterations / 1e6
				+ " mio. non-random games " + time / 1e6 + "ms");
	}

	@Test
	public void testIsTerminal() {
		for (int i = 1; i <= GameStateSnapshot.MAX_DIMENSION; i++)
			for (int j = 1; j <= GameStateSnapshot.MAX_DIMENSION; j++) {
				GameState s = new GameState(4, 4);
				assertFalse(s.isTerminal());
				int counter = 0;
				while (s.advance(true))
					if (s.isTerminal())
						counter++;
				assertTrue(s.isTerminal());
				assertEquals(1, counter);
			}
	}

	@Test
	public void testGetStaticSnapshot() {
		int numIterations = 10000000;
		GameState s = new GameState(4, 4);

		long start = System.nanoTime(), end;
		for (int i = 0; i < numIterations; i++)
			s.getGameStateSnapshot();
		end = System.nanoTime();
		long time = end - start;
		System.out.println("Time to make " + numIterations / 1e6
				+ " mio. snapshots of the board: " + time / 1e6 + "ms");
	}

	@Test
	public void testIsValidStep() {
		boolean b = GameStateFactory.isValidStep(1, 1, +1, +1);
		assertFalse(b);
	}

	@Test
	public void testGetEquivalentBoards() {
		GameStateSnapshot s = new GameStateSnapshot(3, 3);
		s.setLine(0, 0);
		long[][] equiv = s.getMirrorSymmetricBoards();
		// ror
		assertEquals(1L << 16, equiv[1][1]);
		assertEquals(0L, equiv[1][0]);
		// ror
		assertEquals(1L << 16, equiv[2][0]);
		assertEquals(0L, equiv[2][1]);
		// ror
		assertEquals(1L << 0, equiv[3][1]);
		assertEquals(0L, equiv[3][0]);
		// flip
		assertEquals(1L << 19, equiv[4][1]);
		assertEquals(0L, equiv[4][0]);
		// ror
		assertEquals(1L << 19, equiv[5][0]);
		assertEquals(0L, equiv[5][1]);
		// ror
		assertEquals(1L << 3, equiv[6][1]);
		assertEquals(0L, equiv[6][0]);
		// ror
		assertEquals(1L << 3, equiv[7][0]);
		assertEquals(0L, equiv[7][1]);

		s = new GameStateSnapshot(4, 3);
		s.setLine(0, 0);
		equiv = s.getMirrorSymmetricBoards();
		// vertical
		assertEquals(1L << 3, equiv[1][0]);
		assertEquals(0L, equiv[1][1]);
		// horizontal
		assertEquals(1L << 19, equiv[2][0]);
		assertEquals(0L, equiv[2][1]);
		// vertical + horizontal
		assertEquals(1L << 16, equiv[3][0]);
		assertEquals(0L, equiv[3][1]);
	}

	@Test
	public void testLinesHashCode2() {
		long lines[] = new long[] { 1, 2 };
		long lines2[] = new long[] { 1, 2 };
		assertEquals(GameStateSnapshot.linesHashCode(lines2),
				GameStateSnapshot.linesHashCode(lines));
	}

	@Test
	public void testSampling() {
		int numIterations = (int) 0.1e6;
		long[] lines = new long[2];
		lines[0] = 0xabcd23ef012L;
		lines[1] = 0x12345678905L;

		long[] lines1 = new long[2];

		Random rnd = new Random();
		byte x[] = new byte[100];
		// fill Bytes
		rnd.nextBytes(x);
		long y[] = new long[x.length];

		for (int i = 0; i < y.length; i++) {
			y[i] = (1L << x[i]);
		}

		// sample with bytes first
		long start = System.nanoTime(), end;
		for (int i = 0; i < numIterations; i++)
			for (int j = 0; j < y.length; j++)
				lines1[0] = (lines[0] & (1L << x[j]));
		end = System.nanoTime();
		long time = end - start;
		System.out.println("Time to calculate " + numIterations / 1e6
				+ " mio. samplings: " + time / 1e6 + "ms");

		// sample with bytes first
		start = System.nanoTime();
		for (int i = 0; i < numIterations; i++)
			for (int j = 0; j < y.length; j++)
				lines1[0] = (lines[0] & y[j]);
		end = System.nanoTime();
		time = end - start;
		System.out.println("Time to calculate " + numIterations / 1e6
				+ " mio. samplings: " + time / 1e6 + "ms");

	}

	@Test
	public void testLinesHashCode() {
		int numIterations = (int) 0.1e6;
		GameState s = new GameState(7, 7);
		GameState s1 = new GameState(7, 7);

		// Different move-sequences can lead to the same position. Hash-code has
		// to be the same for this
		// easy example
		s.setLine(0, 1);
		s.setLine(1, 20);//
		s1.setLine(1, 20);
		s1.setLine(0, 1);
		assertEquals(s.linesHashCode(), s1.linesHashCode());

		s.setLine(0, 10);
		s.setLine(1, 21);
		s.setLine(0, 14);
		s.setLine(1, 15);
		s.setLine(0, 45);
		s.setLine(1, 46);
		s.setLine(0, 47);
		s.setLine(1, 48);// //
		s1.setLine(1, 48);
		s1.setLine(0, 14);
		s1.setLine(1, 15);
		s1.setLine(0, 47);
		s1.setLine(0, 10);
		s1.setLine(1, 21);
		s1.setLine(0, 45);
		s1.setLine(1, 46);
		assertEquals(s.linesHashCode(), s1.linesHashCode());
		s1.setLine(1, 50);
		assertFalse(s.linesHashCode() == s1.linesHashCode());

		while (s.advance(true))
			;
		s1 = new GameState(s, false);
		assertEquals(s.linesHashCode(), s1.linesHashCode());

		// System.out.println(s);
		// System.out.println(s1);

		long start = System.nanoTime(), end;
		int hash;
		for (int i = 0; i < numIterations; i++) {
			hash = s.linesHashCode();
			end = hash;
			hash = (int) end;
		}
		end = System.nanoTime();
		long time = end - start;
		System.out.println("Time to calculate " + numIterations / 1e6
				+ " mio. Line-Hash-Codes of a 7x7 board: " + time / 1e6 + "ms");
	}

	@Test
	public void testHashCode() {
		int numIterations = (int) 0.1e6;
		GameState s = new GameState(7, 7);
		GameState s1 = new GameState(7, 7);

		s.advance(false);
		s1.advance(false);
		assertEquals(s.hashCode(), s1.hashCode());
		s.advance(false);
		assertFalse(s.hashCode() == s1.hashCode());
		s1.advance(false);
		assertEquals(s.hashCode(), s1.hashCode());

		while (s.advance(true))
			;
		assertFalse(s.hashCode() == s1.hashCode());
		s1 = new GameState(s, false);
		assertEquals(s.hashCode(), s1.hashCode());

		// new ----------------------------------------------
		s = new GameState(7, 7);
		/* @formatter:off */
/*		+---+   +   +   +   +   +   +
		| A |                          
		+---+   +   +   +   +   +   +
		                               
		+   +   +   +   +   +   +   +
		                               
		+   +   +   +   +   +   +   +
		                               Box-difference: 0
		+   +   +   +   +   +   +   +
		                               Player to move: A
		+   +   +   +   +   +   +   +
		                               
		+   +   +   +   +   +   +---+
		                        | B |  
		+   +   +   +   +   +   +---+*/
		/* @formatter:on */
		s.advance(0, (byte) 1);
		s.advance(0, (byte) 0);
		s.advance(1, (byte) 38);
		s.advance(1, (byte) 39);

		s.advance(0, (byte) 32);
		s.advance(0, (byte) 33);
		s.advance(1, (byte) 6);
		s.advance(1, (byte) 7);

		s1 = new GameState(7, 7);
		/* @formatter:off */
/*		+---+   +   +   +   +   +   +
		| B |                          
		+---+   +   +   +   +   +   +
		                               
		+   +   +   +   +   +   +   +
		                               
		+   +   +   +   +   +   +   +
		                               Box-difference: 0
		+   +   +   +   +   +   +   +
		                               Player to move: A
		+   +   +   +   +   +   +   +
		                               
		+   +   +   +   +   +   +---+
		                        | A |  
		+   +   +   +   +   +   +---+*/
		/* @formatter:on */
		s1.advance(0, (byte) 32);
		s1.advance(0, (byte) 33);
		s1.advance(1, (byte) 6);
		s1.advance(1, (byte) 7);

		s1.advance(0, (byte) 1);
		s1.advance(0, (byte) 0);
		s1.advance(1, (byte) 38);
		s1.advance(1, (byte) 39);
		// Since the Box-difference is the same, the hash-Code should be the
		// same as well
		assertEquals(s.hashCode(), s1.hashCode());
		// ------------------------------------------------
		s.advance(0, (byte) 6);
		s.advance(0, (byte) 7);
		s.advance(1, (byte) 0);
		s.advance(1, (byte) 1);

		s.advance(0, (byte) 38);
		s.advance(0, (byte) 39);
		s.advance(1, (byte) 32);
		s.advance(1, (byte) 33);

		s1.advance(0, (byte) 6);
		s1.advance(0, (byte) 7);
		s1.advance(1, (byte) 0);
		s1.advance(1, (byte) 33);

		s1.advance(0, (byte) 38);
		s1.advance(0, (byte) 39);
		s1.advance(1, (byte) 32);
		s1.advance(1, (byte) 1);

		/* @formatter:off */
		/* 
		 * State s:
		 * 
		+---+   +   +   +   +   +---+
		| A |                   | B |  
		+---+   +   +   +   +   +---+
		                               
		+   +   +   +   +   +   +   +
		                               
		+   +   +   +   +   +   +   +
		                               Box-difference: 0
		+   +   +   +   +   +   +   +
		                               Player to move: A
		+   +   +   +   +   +   +   +
		                               
		+---+   +   +   +   +   +---+
		| A |                   | B |  
		+---+   +   +   +   +   +---+


		State s1:

		+---+   +   +   +   +   +---+
		| B |                   | A |  
		+---+   +   +   +   +   +---+
		                               
		+   +   +   +   +   +   +   +
		                               
		+   +   +   +   +   +   +   +
		                               Box-difference: 2
		+   +   +   +   +   +   +   +
		                               Player to move: A
		+   +   +   +   +   +   +   +
		                               
		+---+   +   +   +   +   +---+
		| A |                   | A |  
		+---+   +   +   +   +   +---+*/
		/* @formatter:on */

		assertFalse(s.hashCode() == s1.hashCode());

		// System.out.println(s);
		// System.out.println(s1);

		long start = System.nanoTime(), end;
		int hash;
		for (int i = 0; i < numIterations; i++) {
			hash = s.hashCode();
			end = hash;
			hash = (int) end;
		}
		end = System.nanoTime();
		long time = end - start;
		System.out.println("Time to calculate " + numIterations / 1e6
				+ " mio. standard hash-codes of a 7x7 board: " + time / 1e6
				+ "ms");
	}

	@Test
	public void testGetHorLineBitIndex() {
		GameStateSnapshot s = new GameStateSnapshot(7, 7);
		assertEquals(s.getHorLineBitIndex(0, 0), 32);
		assertEquals(s.getHorLineBitIndex(0, 7), 39);
		assertEquals(s.getHorLineBitIndex(1, 0), 40);
		assertEquals(s.getHorLineBitIndex(3, 3), 59);

		s = new GameStateSnapshot(4, 4);
		assertEquals(s.getHorLineBitIndex(0, 0), 16);
		assertEquals(s.getHorLineBitIndex(0, 1), 17);
		assertEquals(s.getHorLineBitIndex(0, 4), 20);
		assertEquals(s.getHorLineBitIndex(1, 0), 24);
		assertEquals(s.getHorLineBitIndex(1, 4), 28);
		assertEquals(s.getHorLineBitIndex(2, 0), 12);
		assertEquals(s.getHorLineBitIndex(2, 4), 8);
		assertEquals(s.getHorLineBitIndex(3, 1), 3);
		assertEquals(s.getHorLineBitIndex(3, 4), 0);

		s = new GameStateSnapshot(2, 2);
		assertEquals(s.getHorLineBitIndex(0, 0), 8);
		assertEquals(s.getHorLineBitIndex(0, 1), 9);
		assertEquals(s.getHorLineBitIndex(0, 2), 10);
		assertEquals(s.getHorLineBitIndex(1, 0), 2);
		assertEquals(s.getHorLineBitIndex(1, 1), 1);
		assertEquals(s.getHorLineBitIndex(1, 2), 0);

		s = new GameStateSnapshot(3, 2);
		assertEquals(s.getHorLineBitIndex(0, 0), 16);
		assertEquals(s.getHorLineBitIndex(0, 1), 17);
		assertEquals(s.getHorLineBitIndex(0, 2), 18);
		assertEquals(s.getHorLineBitIndex(1, 0), 24);
		assertEquals(s.getHorLineBitIndex(1, 1), 25);
		assertEquals(s.getHorLineBitIndex(1, 2), 8);
		assertEquals(s.getHorLineBitIndex(2, 0), 2);
		assertEquals(s.getHorLineBitIndex(2, 1), 1);
		assertEquals(s.getHorLineBitIndex(2, 2), 0);

	}

	@Test
	public void testGetVertLineBitIndex() {
		GameStateSnapshot s = new GameStateSnapshot(7, 7);
		assertEquals(s.getVertLineBitIndex(0, 0), 7);
		assertEquals(s.getVertLineBitIndex(7, 0), 0);
		assertEquals(s.getVertLineBitIndex(0, 1), 15);
		assertEquals(s.getVertLineBitIndex(4, 0), 3);
		assertEquals(s.getVertLineBitIndex(2, 2), 21);
		assertEquals(s.getVertLineBitIndex(0, 2), 23);
		assertEquals(s.getVertLineBitIndex(0, 3), 56);
		assertEquals(s.getVertLineBitIndex(1, 3), 57);
		assertEquals(s.getVertLineBitIndex(2, 3), 58);
		assertEquals(s.getVertLineBitIndex(3, 3), 59);
		assertEquals(s.getVertLineBitIndex(4, 3), 27);
		assertEquals(s.getVertLineBitIndex(5, 3), 26);
		assertEquals(s.getVertLineBitIndex(6, 3), 25);
		assertEquals(s.getVertLineBitIndex(7, 3), 24);
		assertEquals(s.getVertLineBitIndex(0, 4), 48);
		assertEquals(s.getVertLineBitIndex(0, 5), 40);
		assertEquals(s.getVertLineBitIndex(1, 4), 49);
		assertEquals(s.getVertLineBitIndex(1, 5), 41);
		assertEquals(s.getVertLineBitIndex(7, 4), 55);
		assertEquals(s.getVertLineBitIndex(7, 5), 47);
		assertEquals(s.getVertLineBitIndex(7, 6), 39);
		assertEquals(s.getVertLineBitIndex(3, 6), 35);
		assertEquals(s.getVertLineBitIndex(0, 6), 32);

		s = new GameStateSnapshot(2, 2);
		assertEquals(2, s.getVertLineBitIndex(0, 0));
		assertEquals(1, s.getVertLineBitIndex(1, 0));
		assertEquals(0, s.getVertLineBitIndex(2, 0));
		assertEquals(8, s.getVertLineBitIndex(0, 1));
		assertEquals(9, s.getVertLineBitIndex(1, 1));
		assertEquals(10, s.getVertLineBitIndex(2, 1));

		s = new GameStateSnapshot(4, 4);
		assertEquals(4, s.getVertLineBitIndex(0, 0));
		assertEquals(3, s.getVertLineBitIndex(1, 0));
		assertEquals(2, s.getVertLineBitIndex(2, 0));
		assertEquals(1, s.getVertLineBitIndex(3, 0));
		assertEquals(0, s.getVertLineBitIndex(4, 0));
		assertEquals(12, s.getVertLineBitIndex(0, 1));
		assertEquals(11, s.getVertLineBitIndex(1, 1));
		assertEquals(10, s.getVertLineBitIndex(2, 1));
		assertEquals(9, s.getVertLineBitIndex(3, 1));
		assertEquals(8, s.getVertLineBitIndex(4, 1));

		assertEquals(24, s.getVertLineBitIndex(0, 2));
		assertEquals(25, s.getVertLineBitIndex(1, 2));
		assertEquals(26, s.getVertLineBitIndex(2, 2));
		assertEquals(27, s.getVertLineBitIndex(3, 2));
		assertEquals(28, s.getVertLineBitIndex(4, 2));

		assertEquals(16, s.getVertLineBitIndex(0, 3));
		assertEquals(17, s.getVertLineBitIndex(1, 3));
		assertEquals(18, s.getVertLineBitIndex(2, 3));
		assertEquals(19, s.getVertLineBitIndex(3, 3));
		assertEquals(20, s.getVertLineBitIndex(4, 3));

		s = new GameStateSnapshot(4, 3);
		assertEquals(0, s.getVertLineBitIndex(4, 0));
		assertEquals(1, s.getVertLineBitIndex(3, 0));
		assertEquals(2, s.getVertLineBitIndex(2, 0));
		assertEquals(3, s.getVertLineBitIndex(1, 0));
		assertEquals(4, s.getVertLineBitIndex(0, 0));

		assertEquals(20, s.getVertLineBitIndex(4, 2));
		assertEquals(19, s.getVertLineBitIndex(3, 2));
		assertEquals(18, s.getVertLineBitIndex(2, 2));
		assertEquals(17, s.getVertLineBitIndex(1, 2));
		assertEquals(16, s.getVertLineBitIndex(0, 2));

		assertEquals(8, s.getVertLineBitIndex(4, 1));
		assertEquals(9, s.getVertLineBitIndex(3, 1));
		assertEquals(26, s.getVertLineBitIndex(2, 1));
		assertEquals(25, s.getVertLineBitIndex(1, 1));
		assertEquals(24, s.getVertLineBitIndex(0, 1));

	}

	@Test
	public void testClosedBoxes() {
		GameState s = new GameState(2, 2);
		s.setLine(0, 8);
		assertEquals(0, s.boxesClosed(0, 8));
		s.setLine(0, 2);
		assertEquals(0, s.boxesClosed(0, 2));
		s.setLine(0, 9);
		assertEquals(0, s.boxesClosed(0, 9));
		s.setLine(1, 2);
		assertEquals(0, s.boxesClosed(1, 2));
		s.setLine(1, 1);
		assertEquals(1, s.boxesClosed(1, 1));
		s.setLine(1, 8);
		assertEquals(0, s.boxesClosed(1, 8));
		s.setLine(1, 10);
		assertEquals(0, s.boxesClosed(1, 10));
		s.setLine(0, 10);
		assertEquals(0, s.boxesClosed(0, 10));
		s.setLine(0, 0);
		assertEquals(0, s.boxesClosed(0, 0));
		s.setLine(0, 1);
		assertEquals(0, s.boxesClosed(0, 1));
		s.setLine(1, 9);
		assertEquals(2, s.boxesClosed(1, 9));
		s.setLine(1, 0);
		assertEquals(1, s.boxesClosed(1, 0));
		//
		assertEquals(1, s.boxesClosed(0, 0));
		assertEquals(2, s.boxesClosed(0, 1));
		assertEquals(1, s.boxesClosed(0, 2));
		assertEquals(1, s.boxesClosed(0, 8));
		assertEquals(2, s.boxesClosed(0, 9));
		assertEquals(1, s.boxesClosed(0, 10));
		//
		assertEquals(1, s.boxesClosed(1, 0));
		assertEquals(2, s.boxesClosed(1, 1));
		assertEquals(1, s.boxesClosed(1, 2));
		assertEquals(1, s.boxesClosed(1, 8));
		assertEquals(2, s.boxesClosed(1, 9));
		assertEquals(1, s.boxesClosed(1, 10));
		int numIterations = (int) 0.1e6;

		long start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			s.boxesClosed(0, i % 3);
		}
		long end = System.nanoTime();
		long time = end - start;
		System.out
				.println("Time to check if all boxes on a 2x2 board are closed for "
						+ numIterations
						/ 1e6
						+ " mio. iterations: "
						+ time
						/ 1e6 + "ms");

		// ------------------------------------
		// For a 3x3 board
		s = new GameState(3, 3);
		s.setLine(0, 0);
		s.setLine(0, 1);
		assertEquals(0, s.boxesClosed(0, 0));
		assertEquals(0, s.boxesClosed(0, 1));
		s.setLine(1, 19);
		assertEquals(0, s.boxesClosed(1, 19));
		s.setLine(1, 18);
		assertEquals(1, s.boxesClosed(1, 18));
		s.setLine(1, 3);
		s.setLine(1, 1);
		s.setLine(0, 17);
		s.setLine(0, 24);
		s.setLine(0, 16);
		s.setLine(0, 25);
		s.setLine(1, 2);
		assertEquals(2, s.boxesClosed(1, 2));
		s.setLine(0, 3);
		s.setLine(0, 2);
		s.setLine(1, 0);
		assertEquals(1, s.boxesClosed(1, 0));
		s.setLine(1, 24);
		s.setLine(1, 25);
		s.setLine(1, 8);
		s.setLine(1, 9);
		s.setLine(0, 9);
		assertEquals(1, s.boxesClosed(0, 9));
		s.setLine(0, 18);
		assertEquals(1, s.boxesClosed(0, 18));
		s.setLine(0, 8);
		s.setLine(0, 19);
		s.setLine(1, 17);
		assertEquals(1, s.boxesClosed(1, 17));
		s.setLine(1, 16);
		assertEquals(1, s.boxesClosed(1, 16));

		// 7 x 7 board
		s = new GameState(7, 7);
		s.setLine(0, 32);
		s.setLine(0, 33);
		s.setLine(0, 40);
		s.setLine(0, 41);
		s.setLine(1, 7);
		s.setLine(1, 5);
		s.setLine(1, 6);
		assertEquals(2, s.boxesClosed(1, 6));

		s.setLine(0, 1);
		s.setLine(0, 9);
		s.setLine(0, 0);
		s.setLine(0, 8);
		s.setLine(1, 39);
		s.setLine(1, 38);
		assertEquals(1, s.boxesClosed(1, 38));
		s.setLine(1, 37);
		assertEquals(1, s.boxesClosed(1, 37));

		s.setLine(0, 59);
		s.setLine(0, 27);
		s.setLine(1, 59);
		s.setLine(1, 27);
		assertEquals(1, s.boxesClosed(0, 59));
		assertEquals(1, s.boxesClosed(0, 27));
		assertEquals(1, s.boxesClosed(1, 59));
		assertEquals(1, s.boxesClosed(1, 27));

		assertEquals(0, s.boxesClosed(1, 33));
		assertEquals(0, s.boxesClosed(1, 32));
		assertEquals(0, s.boxesClosed(0, 38));
		assertEquals(0, s.boxesClosed(0, 39));
		s.setLine(1, 33);
		s.setLine(1, 32);
		s.setLine(0, 38);
		s.setLine(0, 39);
		assertEquals(1, s.boxesClosed(1, 33));
		assertEquals(1, s.boxesClosed(1, 32));
		assertEquals(1, s.boxesClosed(0, 38));
		assertEquals(1, s.boxesClosed(0, 39));

		assertEquals(0, s.boxesClosed(1, 0));
		assertEquals(0, s.boxesClosed(1, 1));
		assertEquals(0, s.boxesClosed(0, 7));
		assertEquals(0, s.boxesClosed(0, 6));
		s.setLine(1, 0);
		s.setLine(1, 1);
		s.setLine(0, 7);
		s.setLine(0, 6);
		assertEquals(1, s.boxesClosed(1, 0));
		assertEquals(1, s.boxesClosed(1, 1));
		assertEquals(1, s.boxesClosed(0, 7));
		assertEquals(1, s.boxesClosed(0, 6));

		for (int i = 0; i < Long.SIZE; i++) {
			s.setLine(0, i);
			s.setLine(1, i);
		}

		assertEquals(1, s.boxesClosed(0, 0));
		assertEquals(2, s.boxesClosed(0, 1));
		assertEquals(2, s.boxesClosed(0, 2));
		assertEquals(2, s.boxesClosed(0, 3));
		assertEquals(2, s.boxesClosed(0, 4));
		assertEquals(2, s.boxesClosed(0, 5));
		assertEquals(2, s.boxesClosed(0, 6));
		assertEquals(1, s.boxesClosed(0, 7));
		assertEquals(1, s.boxesClosed(0, 8));
		assertEquals(2, s.boxesClosed(0, 9));
		assertEquals(2, s.boxesClosed(0, 10));
		assertEquals(2, s.boxesClosed(0, 11));
		assertEquals(2, s.boxesClosed(0, 12));
		assertEquals(2, s.boxesClosed(0, 13));
		assertEquals(2, s.boxesClosed(0, 14));
		assertEquals(1, s.boxesClosed(0, 15));

		assertEquals(1, s.boxesClosed(0, 16));
		assertEquals(2, s.boxesClosed(0, 17));
		assertEquals(2, s.boxesClosed(0, 18));
		assertEquals(2, s.boxesClosed(0, 19));
		assertEquals(2, s.boxesClosed(0, 20));
		assertEquals(2, s.boxesClosed(0, 21));
		assertEquals(2, s.boxesClosed(0, 22));
		assertEquals(1, s.boxesClosed(0, 23));

		assertEquals(1, s.boxesClosed(0, 32));
		assertEquals(2, s.boxesClosed(0, 33));
		assertEquals(2, s.boxesClosed(0, 34));
		assertEquals(2, s.boxesClosed(0, 35));
		assertEquals(2, s.boxesClosed(0, 36));
		assertEquals(2, s.boxesClosed(0, 37));
		assertEquals(2, s.boxesClosed(0, 38));
		assertEquals(1, s.boxesClosed(0, 39));

		assertEquals(1, s.boxesClosed(0, 24));
		assertEquals(2, s.boxesClosed(0, 25));
		assertEquals(2, s.boxesClosed(0, 26));
		assertEquals(2, s.boxesClosed(0, 27));
		assertEquals(1, s.boxesClosed(0, 56));
		assertEquals(2, s.boxesClosed(0, 57));
		assertEquals(2, s.boxesClosed(0, 58));
		assertEquals(2, s.boxesClosed(0, 59));

		assertEquals(1, s.boxesClosed(1, 0));
		assertEquals(2, s.boxesClosed(1, 1));
		assertEquals(2, s.boxesClosed(1, 2));
		assertEquals(2, s.boxesClosed(1, 3));
		assertEquals(2, s.boxesClosed(1, 4));
		assertEquals(2, s.boxesClosed(1, 5));
		assertEquals(2, s.boxesClosed(1, 6));
		assertEquals(1, s.boxesClosed(1, 7));
		assertEquals(1, s.boxesClosed(1, 8));
		assertEquals(2, s.boxesClosed(1, 9));
		assertEquals(2, s.boxesClosed(1, 10));
		assertEquals(2, s.boxesClosed(1, 11));
		assertEquals(2, s.boxesClosed(1, 12));
		assertEquals(2, s.boxesClosed(1, 13));
		assertEquals(2, s.boxesClosed(1, 14));
		assertEquals(1, s.boxesClosed(1, 15));

		assertEquals(1, s.boxesClosed(1, 16));
		assertEquals(2, s.boxesClosed(1, 17));
		assertEquals(2, s.boxesClosed(1, 18));
		assertEquals(2, s.boxesClosed(1, 19));
		assertEquals(2, s.boxesClosed(1, 20));
		assertEquals(2, s.boxesClosed(1, 21));
		assertEquals(2, s.boxesClosed(1, 22));
		assertEquals(1, s.boxesClosed(1, 23));

		assertEquals(1, s.boxesClosed(1, 32));
		assertEquals(2, s.boxesClosed(1, 33));
		assertEquals(2, s.boxesClosed(1, 34));
		assertEquals(2, s.boxesClosed(1, 35));
		assertEquals(2, s.boxesClosed(1, 36));
		assertEquals(2, s.boxesClosed(1, 37));
		assertEquals(2, s.boxesClosed(1, 38));
		assertEquals(1, s.boxesClosed(1, 39));

		assertEquals(1, s.boxesClosed(1, 24));
		assertEquals(2, s.boxesClosed(1, 25));
		assertEquals(2, s.boxesClosed(1, 26));
		assertEquals(2, s.boxesClosed(1, 27));
		assertEquals(1, s.boxesClosed(1, 56));
		assertEquals(2, s.boxesClosed(1, 57));
		assertEquals(2, s.boxesClosed(1, 58));
		assertEquals(2, s.boxesClosed(1, 59));
	}

	@Test
	public void testadvance() {
		int numIterations = (int) 0.001e6;
		GameState s = new GameState(7, 7);
		GameState s1;
		// 7x7 board
		long start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			s1 = new GameState(s, false);
			while (s1.advance(true))
				; // copyConstructor = 283ms, std.Constructor=2564ms
		}
		long end = System.nanoTime();
		long time = end - start;
		System.out.println("Time to play " + numIterations / 1e6
				+ " mio. random-move games on a 7x7 board: " + time / 1e6
				+ "ms (with extra info.)");

		start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			s1 = new GameState(s, true);
			while (s1.advance(true))
				;
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("Time to play " + numIterations / 1e6
				+ " mio. random-move games on a 7x7 board: " + time / 1e6
				+ "ms (no extra info.)");
	}

	@Test
	public void testCopyConstructor() {
		int numIterations = (int) 0.1e6;
		GameState s = new GameState(7, 7);
		GameState s1;

		// Fast copy
		long start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			s1 = new GameState(s, true);
			s1.advance(false);
		}
		long end = System.nanoTime();
		long time = end - start;
		System.out.println("Time to clone a 7x7 board for " + numIterations
				/ 1e6 + " mio. times: " + time / 1e6 + "ms (Fast Copy)");

		// Standard copy
		start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			s1 = new GameState(s, false);
			s1.advance(false);
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("Time to clone a 7x7 board for " + numIterations
				/ 1e6 + " mio. times: " + time / 1e6 + "ms (Standard Copy)"); // 1186
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testRotateBoardRight() {
		int numIterations = (int) 0.1e6;

		// Start with a 7x7 board
		GameStateSnapshot s = new GameStateSnapshot(7, 7);
		s.setLine(0, 3, 0);
		s.setLine(0, 3, 3);
		long start = System.nanoTime();
		for (int i = 0; i < numIterations; i++)
			s.rotateBoardRight();
		long end = System.nanoTime();
		long time = end - start;
		System.out.println("Time to rotate a 7x7 board " + numIterations / 1e6
				+ " mio. times: " + time / 1e6 + "ms");

		// 4x4 Board
		s = new GameStateSnapshot(4, 4);
		s.setLine(0, 3, 0);
		s.setLine(0, 3, 3);

		start = System.nanoTime();
		for (int i = 0; i < numIterations; i++)
			s.rotateBoardRight();
		end = System.nanoTime();
		time = end - start;

		System.out.println("Time to rotate a 4x4 board " + numIterations / 1e6
				+ " mio. times: " + time / 1e6
				+ "ms. (should be the same for all board sizes)");

	}

	@Test
	public void testFlipHorizontal() {
		GameStateSnapshot s = new GameStateSnapshot(4, 3);
		long[] lines = new long[2];

		lines[1] = (1L << 0);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 4));

		lines[1] = (1L << 1);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 3));

		lines[1] = (1L << 2);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 2));

		lines[1] = (1L << 3);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 1));

		lines[1] = (1L << 4);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 0));

		lines[1] = (1L << 8);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 24));

		lines[1] = (1L << 24);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 8));

		lines[1] = (1L << 9);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 25));

		lines[1] = (1L << 25);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 26);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 26));

		lines[1] = (1L << 16);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 20));

		lines[1] = (1L << 17);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 19));

		lines[1] = (1L << 18);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 18));

		lines[0] = (1L << 0);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 19));

		lines[0] = (1L << 8);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 27));

		lines[0] = (1L << 1);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 18));

		lines[0] = (1L << 9);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 26));

		lines[0] = (1L << 26);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 9));

		lines[0] = (1L << 18);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 2);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 17));

		lines[0] = (1L << 10);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 25));

		lines[0] = (1L << 25);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 10));

		lines[0] = (1L << 17);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 3);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 16));

		lines[0] = (1L << 16);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 3));

		lines[0] = (1L << 11);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 24));

		lines[0] = (1L << 24);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 11));

		// 3 x 4
		s = new GameStateSnapshot(3, 4);
		lines[0] = (1L << 0);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 20));

		lines[0] = (1L << 20);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 0));

		lines[0] = (1L << 0);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 20));

		lines[0] = (1L << 1);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 19));

		lines[0] = (1L << 19);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 2);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 18));

		lines[0] = (1L << 18);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 3);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 17));

		lines[0] = (1L << 17);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 3));

		lines[0] = (1L << 4);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 16));

		lines[0] = (1L << 16);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 4));

		lines[0] = (1L << 8);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 8));

		lines[0] = (1L << 9);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 9));

		lines[0] = (1L << 24);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 24));

		lines[0] = (1L << 25);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 25));

		lines[0] = (1L << 26);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 26));

		lines[1] = (1L << 0);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 3));

		lines[1] = (1L << 3);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 0));

		lines[1] = (1L << 1);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 2));

		lines[1] = (1L << 2);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 1));

		lines[1] = (1L << 8);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 11));

		lines[1] = (1L << 11);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 8));

		lines[1] = (1L << 10);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 9);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 10));

		lines[1] = (1L << 16);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 19));

		lines[1] = (1L << 19);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 16));

		lines[1] = (1L << 17);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 18));

		lines[1] = (1L << 18);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 17));

		lines[1] = (1L << 24);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 27));

		lines[1] = (1L << 27);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 24));

		lines[1] = (1L << 25);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 26));

		lines[1] = (1L << 26);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 25));

		// 3 x 3
		s = new GameStateSnapshot(3, 3);
		lines[0] = (1L << 0);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 19));

		lines[0] = (1L << 19);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 0));

		lines[0] = (1L << 1);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 18));

		lines[0] = (1L << 18);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 2);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 17));

		lines[0] = (1L << 17);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 3);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 16));

		lines[0] = (1L << 16);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 3));

		lines[0] = (1L << 24);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 24));

		lines[0] = (1L << 25);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 25));

		lines[0] = (1L << 8);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 8));

		lines[0] = (1L << 9);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[0], (1L << 9));

		lines[1] = (1L << 0);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 3));

		lines[1] = (1L << 3);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 0));

		lines[1] = (1L << 8);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 24));

		lines[1] = (1L << 24);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 8));

		lines[1] = (1L << 19);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 16));

		lines[1] = (1L << 16);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 19));

		lines[1] = (1L << 1);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 2));

		lines[1] = (1L << 2);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 1));

		lines[1] = (1L << 9);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 25));

		lines[1] = (1L << 25);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 17);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 18));

		lines[1] = (1L << 18);
		lines = s.flipHorizontal(lines);
		assertEquals(lines[1], (1L << 17));
	}

	@Test
	public void testFlipVertical() {
		int numIterations = (int) 0.1e6;

		// 4x3 Board, Test all Bits
		GameStateSnapshot s = new GameStateSnapshot(4, 3);
		s.setLine(0, 0);
		long[] lines = s.flipVertical(s.getLines());
		assertEquals(lines[0], (1L << 3));
		lines[0] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 3);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 0));

		lines[0] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 11));

		lines[0] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 10));

		lines[0] = (1L << 10);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 9));

		lines[0] = (1L << 11);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 8));

		lines[0] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 27));

		lines[0] = (1L << 25);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 26));

		lines[0] = (1L << 27);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 24));

		lines[0] = (1L << 16);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 19));

		lines[0] = (1L << 17);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 18));

		lines[0] = (1L << 18);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 17));

		lines[0] = (1L << 19);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 16));

		// Vertical now (4x3) board
		lines[1] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 20));

		lines[1] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 19));

		lines[1] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 18));

		lines[1] = (1L << 3);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 17));

		lines[1] = (1L << 4);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 16));

		lines[1] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 24));

		lines[1] = (1L << 25);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 25));

		lines[1] = (1L << 26);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 26));

		lines[1] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 8));

		lines[1] = (1L << 16);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 4));

		lines[1] = (1L << 17);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 3));

		lines[1] = (1L << 18);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 2));

		lines[1] = (1L << 19);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 1));

		lines[1] = (1L << 20);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 0));

		// --------------------------------------------------

		// Test a 3x3 Board
		s = new GameStateSnapshot(3, 3);
		lines[0] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 3));

		lines[0] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 3);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 0));

		lines[0] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 24));

		lines[0] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 25));

		lines[0] = (1L << 25);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 9));

		lines[0] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 8));

		lines[0] = (1L << 19);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 16));

		lines[0] = (1L << 18);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 17));

		lines[0] = (1L << 17);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 18));

		//
		lines[1] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 19));

		lines[1] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 18));

		lines[1] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 17));

		lines[1] = (1L << 3);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 16));

		lines[1] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 8));

		lines[1] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 24));

		lines[1] = (1L << 16);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 3));

		lines[1] = (1L << 17);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 2));

		lines[1] = (1L << 18);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 1));

		lines[1] = (1L << 19);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 0));

		// Now a 4x4 board
		s = new GameStateSnapshot(4, 4);
		lines[0] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 4));

		lines[0] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 3));

		lines[0] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 3);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 4);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 0));

		lines[0] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 12));

		lines[0] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 11));

		lines[0] = (1L << 10);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 10));

		lines[0] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 28));

		lines[0] = (1L << 25);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 27));

		lines[0] = (1L << 26);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 26));

		lines[0] = (1L << 27);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 25));

		lines[0] = (1L << 28);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 24));

		lines[0] = (1L << 16);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 20));

		lines[0] = (1L << 17);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 19));

		lines[0] = (1L << 18);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 18));

		lines[0] = (1L << 19);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 17));

		lines[0] = (1L << 20);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 16));

		lines[1] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 20));

		lines[1] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 19));

		lines[1] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 18));

		lines[1] = (1L << 3);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 17));

		lines[1] = (1L << 4);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 16));

		lines[1] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 28));

		lines[1] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 27));

		lines[1] = (1L << 10);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 26));

		lines[1] = (1L << 11);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 25));

		lines[1] = (1L << 12);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 24));

		lines[1] = (1L << 16);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 4));

		lines[1] = (1L << 17);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 3));

		lines[1] = (1L << 18);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 2));

		lines[1] = (1L << 19);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 1));

		lines[1] = (1L << 20);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 0));

		lines[1] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 12));

		lines[1] = (1L << 25);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 11));

		lines[1] = (1L << 26);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 10));

		lines[1] = (1L << 27);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 28);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 8));

		// Try 2x2 Board
		s = new GameStateSnapshot(2, 2);
		lines[0] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 0));

		lines[0] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 10));

		lines[0] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 9));

		lines[0] = (1L << 10);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 8));

		lines[1] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 10));

		lines[1] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 8));

		lines[1] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 2));

		lines[1] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 1));

		lines[1] = (1L << 10);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 0));

		// 2 x 1
		s = new GameStateSnapshot(2, 1);
		lines[1] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 0));

		lines[1] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 1));

		lines[1] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 2));

		lines[0] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 9));

		lines[0] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 8));

		// 1 x 2
		s = new GameStateSnapshot(1, 2);
		lines[0] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 0));

		lines[1] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 8));

		lines[1] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 8));

		lines[1] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 0));

		// 7x7
		s = new GameStateSnapshot(7, 7);

		lines[0] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 7));

		lines[0] = (1L << 7);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 0));

		lines[0] = (1L << 4);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 3));

		lines[0] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 6));

		lines[0] = (1L << 3);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 4));

		lines[0] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 15));

		lines[0] = (1L << 15);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 8));

		lines[0] = (1L << 11);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 12));

		lines[0] = (1L << 12);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 11));

		lines[0] = (1L << 23);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 16));

		lines[0] = (1L << 20);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 19));

		lines[0] = (1L << 59);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 27));

		lines[0] = (1L << 27);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 59));

		lines[0] = (1L << 54);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 49));

		lines[0] = (1L << 43);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 44));

		lines[0] = (1L << 32);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 39));

		lines[0] = (1L << 39);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 32));

		lines[0] = (1L << 35);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 36));

		lines[0] = (1L << 36);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 35));

		lines[0] = (1L << 33);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 38));

		lines[1] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 39));

		lines[1] = (1L << 39);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 0));

		lines[1] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 24));

		lines[1] = (1L << 25);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 25));

		lines[1] = (1L << 26);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 26));

		lines[1] = (1L << 56);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 56));

		lines[1] = (1L << 57);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 57));

		lines[1] = (1L << 58);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 58));

		lines[1] = (1L << 59);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 59));

		lines[1] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 46));

		lines[1] = (1L << 46);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 17);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 54));

		lines[1] = (1L << 7);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 32));

		lines[1] = (1L << 14);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 41));

		lines[1] = (1L << 50);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 21));

		lines[1] = (1L << 13);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 42));

		lines[1] = (1L << 34);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 5));

		// 7 x 3
		s = new GameStateSnapshot(7, 3);
		lines[0] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 3));

		lines[0] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 1));

		lines[0] = (1L << 3);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 0));

		lines[0] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 11));

		lines[0] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 10));

		lines[0] = (1L << 10);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 9));

		lines[0] = (1L << 11);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 8));

		lines[0] = (1L << 16);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 19));

		lines[0] = (1L << 18);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 17));

		lines[0] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 56));

		lines[0] = (1L << 56);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 24));

		lines[0] = (1L << 25);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 57));

		lines[0] = (1L << 57);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 25));

		lines[0] = (1L << 32);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 35));

		lines[0] = (1L << 41);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 42));

		lines[0] = (1L << 48);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 51));

		lines[0] = (1L << 50);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 49));

		lines[1] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 23));

		lines[1] = (1L << 23);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 0));

		lines[1] = (1L << 22);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 1));

		lines[1] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 22));

		lines[1] = (1L << 21);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 2));

		lines[1] = (1L << 3);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 20));

		lines[1] = (1L << 20);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 3));

		lines[1] = (1L << 4);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 19));

		lines[1] = (1L << 19);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 4));

		lines[1] = (1L << 18);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 5));

		lines[1] = (1L << 6);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 17));

		lines[1] = (1L << 7);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 16));

		lines[1] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 24));

		lines[1] = (1L << 25);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 25));

		lines[1] = (1L << 26);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 26));

		lines[1] = (1L << 27);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 27));

		lines[1] = (1L << 8);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 8));

		lines[1] = (1L << 9);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 9));

		lines[1] = (1L << 10);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 10));

		lines[1] = (1L << 11);
		lines = s.flipVertical(lines);
		assertEquals(lines[1], (1L << 11));

		// 3 x 4
		s = new GameStateSnapshot(3, 4);
		lines[0] = (1L << 0);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 4));

		lines[0] = (1L << 1);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 3));

		lines[0] = (1L << 2);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 2));

		lines[0] = (1L << 24);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 8));

		lines[0] = (1L << 25);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 9));

		// int x = s.getHorLineBitIndex(1, 2);

		lines[0] = (1L << 26);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 26));

		lines[0] = (1L << 20);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 16));

		lines[0] = (1L << 17);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 19));

		lines[0] = (1L << 18);
		lines = s.flipVertical(lines);
		assertEquals(lines[0], (1L << 18));

		// Start with a 7x7 board
		s = new GameStateSnapshot(7, 7);
		s.setLine(0, 3, 0);
		s.setLine(0, 3, 3);

		long start = System.nanoTime();
		for (int i = 0; i < numIterations; i++)
			s.flipVertical();
		long end = System.nanoTime();
		long time = end - start;

		System.out.println("Time to flip a 7x7 board " + numIterations / 1e6
				+ " mio. times: " + time / 1e6 + "ms");

		// 4x4 Board
		s = new GameStateSnapshot(4, 4);
		s.setLine(0, 3, 0);
		s.setLine(0, 3, 3);

		start = System.nanoTime();
		for (int i = 0; i < numIterations; i++)
			s.flipVertical();
		end = System.nanoTime();
		time = end - start;

		System.out.println("Time to flip a 4x4 board " + numIterations / 1e6
				+ " mio. times: " + time / 1e6 + "ms");

		// 3x3 Board
		s = new GameStateSnapshot(3, 3);
		s.setLine(0, 3, 0);
		s.setLine(0, 3, 3);

		start = System.nanoTime();
		for (int i = 0; i < numIterations; i++)
			s.flipVertical();
		end = System.nanoTime();
		time = end - start;

		System.out.println("Time to flip a 3x3 board " + numIterations / 1e6
				+ " mio. times: " + time / 1e6 + "ms");

	}

}
