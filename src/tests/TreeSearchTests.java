package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import treeSearch.AlphaBeta;
import treeSearch.MiniMax;
import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateSnapshot;
import dotsAndBoxes.Move;

public class TreeSearchTests {

	@Test
	public void testImpartiality() {
		int m = 3, n = 3;
		GameState s = new GameState(m, n);

		s.advance(1, (byte) 3);
		s.advance(1, (byte) 2);
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 17);
		s.advance(0, (byte) 8);
		s.advance(0, (byte) 0);
		s.advance(0, (byte) 18);

		AlphaBeta ab = new AlphaBeta();

		List<Move> list1 = ab.getBestMoves(s);
		double value1 = ab.getValue(s);

		// Now set box-diff to zero. Results should still be the same
		s.resetBoxDiff();
		List<Move> list2 = ab.getBestMoves(s);
		double value2 = ab.getValue(s);
		assertTrue(list1.equals(list2));
		assertEquals(value1, value2 - 1.0, 0.001);

		// Some more tests
		int advance = 18;
		int runs = 4000;
		for (int i = 0; i < runs; i++) {
			s = new GameState(m, n);
			// create a random state with 15 lines
			for (int j = 0; j < advance; j++)
				s.advance(true);

			// First with original values
			list1 = ab.getBestMoves(s);
			value1 = ab.getValue(s);
			int bDiff = s.getBoxDifference();

			// Now reset box-diff and try again
			s.resetBoxDiff();
			list2 = ab.getBestMoves(s);
			value2 = ab.getValue(s);
			assertTrue(list1.equals(list2));
			assertEquals(value1, value2 + bDiff, 0.001);

		}

		// Also the player to move is not relevant. Show this...
		// Some more tests
		advance = 18;
		runs = 4000;
		for (int i = 0; i < runs; i++) {
			s = new GameState(m, n);
			// create a random state with 15 lines
			for (int j = 0; j < advance; j++)
				s.advance(true);

			// First with original values
			list1 = ab.getBestMoves(s);
			value1 = ab.getValue(s);
			int bDiff = s.getBoxDifference();

			// Now reset box-diff and invert player and try again
			s.resetBoxDiff();
			s.invertPlayer();

			list2 = ab.getBestMoves(s);
			value2 = ab.getValue(s);
			assertTrue(list1.equals(list2));
			assertEquals(value1, -value2 + bDiff, 0.001);

		}
	}

	@Test
	public void testCornerSymmetry() {
		System.out.println("Be patient....");
		int m = 3, n = 3;
		GameState s = new GameState(3, 3);

		GameStateSnapshot[] ss, mostS = null;
		int count = 20;
		int totalNum = 0;
		for (int i = 0; i < 1000; i++) {
			s = new GameState(m, n);
			while (s.advance(true)) {
				ss = s.getAllSymmetricSnapshots();
				if (ss.length >= 60 && count > 0 && s.actionsLeft() < 14) {
					mostS = ss;
					count--;

					// We found a position with 128 symmetries
					// Get the values for all symmetric positions
					AlphaBeta ab = new AlphaBeta();

					ArrayList<Double> values = new ArrayList<Double>(100);
					for (GameStateSnapshot j : mostS) {
						GameState ss1 = new GameState(j);
						double value = ab.getValue(ss1);
						values.add(value);
						totalNum++;
					}

					HashSet<Double> set = new HashSet<Double>(200);
					set.addAll(values);
					assertEquals(1, set.size());
				}
			}
		}
		System.out.println("Total number of boards analyzed: " + totalNum
				+ ". All corner-symmetric boards had the same value. Puh :)");
	}

	/**
	 * Can take quite some time, since Minimax is rather slow, even for small
	 * board sizes.
	 */
	@Test
	public void testScores() {
		MiniMax minimax = new MiniMax();
		AlphaBeta ab = new AlphaBeta();
		for (int i = 1; i <= 2; i++)
			for (int j = 1; j <= 2; j++) {
				GameState s = new GameState(i, j);
				double score1 = minimax.getValue(s);
				double score2 = ab.getValue(s);
				assertEquals(score1, score2, 1e-4);

				// repeat n-times
				for (int k = 0; k < 10; k++) {
					s = new GameState(i, j);
					while (s.advance(true)) {
						score1 = minimax.getValue(s);
						score2 = ab.getValue(s);
						assertEquals(score1, score2, 1e-4);
					}
				}
			}
	}

	@Test
	public void testBestMoves() {
		MiniMax minimax = new MiniMax();
		AlphaBeta ab = new AlphaBeta();
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				if (i + j <= 4) {
					GameState s = new GameState(i, j);
					List<Move> best1 = minimax.getBestMoves(s);
					List<Move> best2 = ab.getBestMoves(s);
					Iterator<Move> I = best2.iterator();
					while (I.hasNext()) {
						assertTrue(best1.contains(I.next()));
					}

					for (int k = 0; k < 10; k++) {
						s = new GameState(i, j);
						while (s.advance(true)) {
							best1 = minimax.getBestMoves(s);
							best2 = ab.getBestMoves(s);
							I = best2.iterator();
							while (I.hasNext()) {
								assertTrue(best1.contains(I.next()));
							}
						}
					}
				}
			}

		}

	}

}
