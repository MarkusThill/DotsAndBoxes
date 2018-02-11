package solvers;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

import javax.naming.TimeLimitExceededException;

import org.junit.Test;
import tools.SRandom;
import treeSearch.AlphaBeta;
import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateFactory;
import dotsAndBoxes.Move;
import dotsAndBoxes.MoveValue;

public class WilsonInterfaceTest {

	@Test
	public void testWilsonSolverBug() throws FileNotFoundException {
		//@formatter:off
		/**
test4x4Board: Number of positions evaluated: 2421 (Of in total: 10000)
+   +   +   +   +
    |   |   |      
+   +---+---+---+
|   |       |      
+---+   +   +---+
|   |   |       |  		Box-difference: 0
+   +   +   +   +
|   |   |   |   |  		Player to move: A
+---+---+---+---+




   a  b  c  d  e  f  g  h  i

a  +     +-----+-----+     +
         |     |     |      
b        |  B  |  A  |        
         |     |     |      
c  +-----+-----+-----#-----#
   |     |           |     |
d  |  A  |   -10     |  B  |  
   |     |           |     |
e  +-----+-----+ -6  +-----+
   |     |     |     |     |
f  |  B  |  B  |  B  |  B  |  
   |     |     |     |     |
g  +-----+-----+-----+-----+
   |     |     |     |     |
h  |  B  |  B  |  B  |  B  |  
   |     |     |     |     |
i  +-----+-----+-----+-----+

Enter move for player B (two letters); .=stop; <=back; !=reset: 
Move-sequence to reach GameState s:
[(1,17), (0,11), (1,9), (1,18), (1,2), (0,18), (1,3), (1,24), (0,25), (0,0), (1,11), (1,28), (0,20), (1,1), (0,28), (0,2), (1,25), (0,8), (1,26), (1,20), (1,19), (1,12), (1,16), (0,3)]

		 */
//@formatter:on
		
		int m = 4, n = 4;
		GameState s = new GameState(m, n);
		WilsonInterface wi = new WilsonInterface();
		wi.loadProblem("4x4", 4, 4);
		
		// Try to create this state
		s.advance(1, (byte)17);
		s.advance(0,(byte)11);
		s.advance(1,(byte)9);
		s.advance(1,(byte)18);
		s.advance(1,(byte)2);
		s.advance(0,(byte)18);
		s.advance(1,(byte)3);
		s.advance(1,(byte)24);
		s.advance(0,(byte)25);
		s.advance(0,(byte)0);
		s.advance(1,(byte)11);
		s.advance(1,(byte)28);
		s.advance(0,(byte)20);
		s.advance(1,(byte)1);
		s.advance(0,(byte)28);
		s.advance(0,(byte)2);
		s.advance(1,(byte)25);
		s.advance(0,(byte)8);
		s.advance(1,(byte)26);
		s.advance(1,(byte)20);
		s.advance(1,(byte)19);
		//s.advance(1,(byte)12);
		//s.advance(1,(byte)16);
		//s.advance(0,(byte)3);
		
		System.out.println(s);
		SolverResponse wv = wi.getRelativeValueList(s);
		System.out.println(wv.response);
		//assertEquals(-4, wv.values[0][3][2], 0.0001);
		//assertEquals(4, wv.values[0][3][3], 0.0001);
	}

	@Test
	public void testParse4x4Board() throws FileNotFoundException {
		int m = 4, n = 4;
		GameState s = new GameState(m, n);
		WilsonInterface wi = new WilsonInterface();
		wi.loadProblem("4x4", 4, 4);
		// Tested 1000 boards
		s.advance(1, (byte) 0);

		s.advance(new int[] { 1, 4, 3 });
		s.advance(new int[] { 1, 4, 2 });
		s.advance(new int[] { 1, 3, 3 });
		s.advance(new int[] { 1, 3, 2 });
		s.advance(new int[] { 1, 4, 3 });
		s.advance(new int[] { 0, 3, 4 });

		// System.out.println(s);
		SolverResponse wv = wi.getRelativeValueList(s);
		// System.out.println(wv.response);
		assertEquals(-4, wv.values[0][3][2], 0.0001);
		assertEquals(4, wv.values[0][3][3], 0.0001);
	}

	@Test
	public void test4x4Board() throws FileNotFoundException {
		// compared 10.000 positions
		int NUMBOARDS = 10000;
		int NUM_LINES_MIN = 15;
		int NUM_LINES_RND_EXTRA = 10;

		int m = 4, n = 4;
		GameState s = new GameState(m, n);
		WilsonInterface wi = new WilsonInterface();
		SolverResponse wv = wi.loadProblem("4x4", 4, 4);

		AlphaBeta ab = new AlphaBeta();
		List<MoveValue> valueList;

		//
		// Try for an empty board
		//
		wv = wi.getRelativeValueList(s);
		Double v[][][] = wv.values;
		// all have to be zero
		for (int i = 0; i < v.length; i++)
			for (int j = 0; j < v[i].length; j++)
				for (int k = 0; k < v[i][j].length; k++)
					assertEquals(0.0, v[i][j][k], 0.0001);

		//
		// Now check, with a random number of lines set
		//
		int positionWithChainCounter = 0;
		int badPositionCounter = 0;
		for (int i = 0; i < NUMBOARDS; i++) {
			if (i % 1 == 0) {
				System.out
						.println("test4x4Board: Number of positions evaluated: "
								+ i + " (Of in total: " + NUMBOARDS + ")");
			}
			s = new GameState(m, n);
			int numMoves = NUM_LINES_MIN
					+ SRandom.RND.nextInt(NUM_LINES_RND_EXTRA);
			for (int j = 0; j < numMoves; j++) {
				s.advance(true);
			}
			//
			// Relative values
			//
			s.resetBoxDiff();
			wv = wi.getRelativeValueList(s);

			if (true) {
				System.out.println(s);
				System.out.println(wv.response);
				System.out.println("Move-sequence to reach GameState s:");
				System.out.println(s.getActionSequence());
			}

			valueList = ab.getActionValues(s);

			if (!wv.linesAddedBySolver) {
				checkMoveValuesEquality(valueList, wv, m, n);
			} else {
				int oldPlayer = s.getPlayerToMove();
				List<int[]> mv = wv.extraLines;
				while (!mv.isEmpty()) {
					int oldBoxDiff = s.getBoxDifference();
					int[] p = mv.remove(0); // List is sorted, so always get
											// first element.
					s.advance(p);
					assertTrue(s.getBoxDifference() != oldBoxDiff);
					assertEquals(oldPlayer, s.getPlayerToMove());
				}
				positionWithChainCounter++;

				//
				// Now get the values for the state with closed chains
				//
				s.resetBoxDiff();
				wv = wi.getRelativeValueList(s);
				assertFalse(wv.linesAddedBySolver);
				valueList = ab.getActionValues(s);
				checkMoveValuesEquality(valueList, wv, m, n);

			}
			System.out.println("OK.........");
			System.out.println();
		}
		System.out.println("Bad-Positions: " + badPositionCounter);
		System.out
				.println("Positions with chains: " + positionWithChainCounter);
	}

	@Test
	public void testWilsonBugExceptionError() throws FileNotFoundException {
		// This position leads to a wierd situation. Normally a exception should
		// be thrown, since the solver wrongly closes "de" and creates a closed
		// chain. Correctly, the solver should have captured a box. Therefore a
		// Wilson-Bug-Exception should be thrown in this case. However, this is
		// not done for this position. Test this and correct.
		//@formatter:off
		
/*		  
 *            a  b  c  d  e  f  g

		   a  +-----+-----+     +
		      |           |     |
		   b  |     1  B  |     |  
		      |           |     |
		   c  + -3  +-----+     +
		      |     |            
		   d  |     |              
		      |     |            
		   e  #-----+     +     +
		      |                  
		   f  |                    
		      |                  
		   g  #     +-----+     +
		   */
		//@formatter:on

		int m = 3, n = 3;
		GameState s = new GameState(m, n);
		WilsonInterface wi = new WilsonInterface();
		SolverResponse wv = wi.loadProblem("3x3", 3, 3);

		s.advance(1, (byte) 3);
		s.advance(1, (byte) 0);
		s.advance(1, (byte) 1);
		s.advance(0, (byte) 18);
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 8);
		s.advance(1, (byte) 24);
		s.advance(1, (byte) 25);
		s.advance(0, (byte) 25);
		s.advance(0, (byte) 24);
		s.advance(1, (byte) 16);
		wv = wi.getRelativeValueList(s);

		System.out.println(wv.response);
	}

	@Test
	public void testEXTENSIVEgetRelativeValueList3x3()
			throws FileNotFoundException {
		// Tested 100.000 boards
		int NUMBOARDS = 100000;
		int NUM_LINES = 15;
		int m = 3, n = 3;
		GameState s = new GameState(m, n);
		WilsonInterface wi = new WilsonInterface();
		SolverResponse wv = wi.loadProblem("3x3", 3, 3);

		AlphaBeta ab = new AlphaBeta();

		List<MoveValue> valueList;

		//
		// Now check, with a random number of lines set
		//
		int badPositionCounter = 0;
		for (int i = 0; i < NUMBOARDS; i++) {
			if (i % 100 == 0) {
				System.out
						.println("testEXTENSIVEgetRelativeValueList: Number of positions evaluated: "
								+ i + " (Of in total: " + NUMBOARDS + ")");
			}
			s = new GameState(m, n);
			int numMoves = SRandom.RND.nextInt(NUM_LINES);
			for (int j = 0; j < numMoves; j++) {
				s.advance(true);
			}
			//
			// Relative values
			//
			s.resetBoxDiff();
			// try {
			wv = wi.getRelativeValueList(s);
			// } catch (WilsonSolverBugException e) {
			// continue;
			// }

			valueList = ab.getActionValues(s);
			if (!wv.linesAddedBySolver) {
				// try {
				checkMoveValuesEquality(valueList, wv, m, n);
				// } catch (AssertionError e) {
				// e.printStackTrace();
				// System.err.println(s);
				// System.err.println(wv.response);
				// System.err.println("Line-Count: ");
				// System.err.println(wv.lineCount);
				// System.err.println("Pre-Processed response: ");
				// System.err.println(wv.processedBoard);
				// System.err.println("Real state: ");
				// System.err.println(s);
				// System.err.println("Real move sequence: ");
				// System.err.println(s.getActionSequence().toString());
				// wv = wi.getRelativeValueList(s);
				// }
			} else {
				// badPositionCounter++;
				// System.out.println("Bad Position: ");
				// System.out.println(wv.response);
				// System.out.println("Real position: ");
				// System.out.print(s);
				// System.out.print("extra Lines: ");
				// int[][] array = wv.extraLines.toArray(new
				// int[wv.extraLines.size()][]);
				// System.out.println(Arrays.deepToString(array));

				//
				// This means, that all moves now, are capturing moves (in most
				// cases). Try this
				//
				int oldPlayer = s.getPlayerToMove();
				List<int[]> mv = wv.extraLines;
				while (!mv.isEmpty()) {
					int oldBoxDiff = s.getBoxDifference();
					int[] p = mv.remove(0); // List is sorted, so always get
											// first element.
					s.advance(p);

					// try{
					assertTrue(s.getBoxDifference() != oldBoxDiff);
					// }catch(AssertionError e) {
					// e.printStackTrace();
					// // Debug: remove try catch again
					// wv = wi.getRelativeValueList(s);
					// }
					assertEquals(oldPlayer, s.getPlayerToMove());
				}

			}
		}
		System.out.println("Bad-Positions: " + badPositionCounter);
	}

	@Test
	public void testGetRelativeValueList3x3() throws FileNotFoundException {
		int m = 3, n = 3;
		GameState s = new GameState(m, n);
		WilsonInterface wi = new WilsonInterface();
		SolverResponse wv = wi.loadProblem("3x3", 3, 3);

		AlphaBeta ab = new AlphaBeta();

		// Check values for empty board first...
		List<MoveValue> valueList = ab.getActionValues(s);
		checkMoveValuesEquality(valueList, wv, m, n);

		// Now try all positions with 1 line set
		Iterator<Move> ii = s.iterator();
		while (ii.hasNext()) {
			Move mv = ii.next();
			s.advance(mv);
			wv = wi.getRelativeValueList(s);
			valueList = ab.getActionValues(s);
			s.undo();
			checkMoveValuesEquality(valueList, wv, m, n);
		}

		// Now randomly check boards with two lines set
		for (int i = 0; i < 30; i++) {
			s = new GameState(m, n);
			s.advance(true);
			s.advance(true);
			wv = wi.getRelativeValueList(s);
			valueList = ab.getActionValues(s);
			checkMoveValuesEquality(valueList, wv, m, n);
		}

		// Now randomly check boards with 3 lines set
		for (int i = 0; i < 30; i++) {
			s = new GameState(m, n);
			s.advance(true);
			s.advance(true);
			s.advance(true);
			wv = wi.getRelativeValueList(s);
			valueList = ab.getActionValues(s);
			if (wv.values != null)
				checkMoveValuesEquality(valueList, wv, m, n);
			else
				System.out.println(wv.response);
		}

		wi.close();
	}

	private void checkMoveValuesEquality(List<MoveValue> valueList,
			SolverResponse wv, int m, int n) {
		// for every move check, if the values are equal
		Iterator<MoveValue> i = valueList.iterator();
		while (i.hasNext()) {
			MoveValue mvv = i.next();
			int coord[] = GameStateFactory.getLineCoord(mvv.mv, m, n);
			Double wilsonValue = wv.values[coord[0]][coord[1]][coord[2]];
			//
			// Do not compare if wilsonValue is NaN. This can happen in some
			// situation where only a few moves are considered to be optimal
			//
			if (!Double.isNaN(wilsonValue)) // wilsonValue != NaN does not work
				assertEquals(mvv.value, wilsonValue, 0.001);
		}
	}

	@Test
	public void testGetValueList() throws FileNotFoundException {
		int m = 3, n = 3;
		GameState s = new GameState(m, n);
		WilsonInterface wi = new WilsonInterface();
		SolverResponse wv = wi.loadProblem("3x3", 3, 3);

		s.advance(0, (byte) 16);
		s.advance(0, (byte) 17);
		wv = wi.getRelativeValueList(s);
		assertEquals(0, wv.boxDiff);
		assertEquals(2, wv.lineCount);
		assertEquals(1, wv.p);

		AlphaBeta ab = new AlphaBeta();
		List<MoveValue> x = ab.getActionValues(s);
		checkMoveValuesEquality(x, wv, m, n);

		System.out.println(wv.response);

		//
		// Test a loop
		//

		wi.close();
	}

	@Test
	public void test() throws TimeLimitExceededException, FileNotFoundException {
		WilsonInterface wi = new WilsonInterface();
		SolverResponse wv = wi.loadProblem("3x3", 3, 3);

		assertEquals(0, wv.boxDiff);
		assertEquals(0, wv.lineCount);
		assertEquals(2, wv.values.length);

		wv = wi.advance(0, 1, 1);
		assertEquals(0, wv.boxDiff);
		assertEquals(1, wv.lineCount);

		wv = wi.advance(0, 1, 0);
		assertEquals(0, wv.boxDiff);
		assertEquals(2, wv.lineCount);
		assertEquals(-1, wv.values[1][1][1], 0.001);
		assertEquals(-1, wv.values[1][2][1], 0.001);
		assertEquals(-3, wv.values[0][2][0], 0.001);
		assertEquals(-3, wv.values[1][2][0], 0.001);
		assertEquals(Double.NaN, wv.values[0][1][0], 0.001);
		assertEquals(Double.NaN, wv.values[0][1][1], 0.001);

		wv = wi.advance(1, 1, 0);
		assertEquals(-1, wv.boxDiff);
		assertEquals(4, wv.lineCount);

		wv = wi.advance(1, 1, 0);
		// assertNull(wv);

		wv = wi.advance(1, 2, 2);
		assertEquals(-1, wv.boxDiff);
		assertEquals(5, wv.lineCount);

		wv = wi.advance(1, 2, 1);
		assertEquals(-1, wv.boxDiff);
		assertEquals(6, wv.lineCount);

		// System.out.println(wv.response);

		wv = wi.advance(1, 3, 0);
		assertEquals(-1, wv.boxDiff);
		assertEquals(7, wv.lineCount);

		wv = wi.advance(1, 3, 1);
		assertEquals(-1, wv.boxDiff);
		assertEquals(8, wv.lineCount);

		wv = wi.advance(1, 3, 2);
		assertEquals(-1, wv.boxDiff);
		assertEquals(9, wv.lineCount);
		assertEquals(1, wv.values[1][0][1], 0.001);

		wv = wi.advance(1, 0, 1);
		assertEquals(-1, wv.boxDiff);
		assertEquals(10, wv.lineCount);

		wv = wi.advance(0, 2, 1);
		System.out.println(wv.response);
		assertEquals(0, wv.boxDiff);
		assertEquals(12, wv.lineCount);

		wv = wi.advance(0, 2, 3);
		System.out.println(wv.response);
		assertEquals(-2, wv.boxDiff);
		assertEquals(14, wv.lineCount);

		wv = wi.advance(1, 1, 1);
		System.out.println(wv.response);

		wv = wi.advance(1, 1, 2);
		System.out.println(wv.response);

		wv = wi.advance(0, 0, 1);
		System.out.println(wv.response);

		wv = wi.advance(0, 0, 0);
		System.out.println(wv.response);

		wv = wi.advance(1, 0, 2);
		System.out.println(wv.response);

		wv = wi.reset();
		System.out.println(wv.response);

		wv = wi.advance(1, 0, 0);
		System.out.println(wv.response);

		wv = wi.advance(1, 1, 0);
		System.out.println(wv.response);

		wv = wi.advance(1, 1, 1);
		System.out.println(wv.response);

		wv = wi.advance(1, 2, 1);
		System.out.println(wv.response);

		wv = wi.advance(1, 1, 2);
		System.out.println(wv.response);

		wv = wi.advance(0, 1, 0);
		System.out.println(wv.response);

		wv = wi.advance(0, 2, 0);
		System.out.println(wv.response);

		wv = wi.advance(1, 3, 2);
		System.out.println(wv.response);

		wv = wi.advance(1, 3, 1);
		System.out.println(wv.response);

		wv = wi.advance(1, 2, 2);
		System.out.println(wv.response);

		wv = wi.advance(1, 0, 1);
		System.out.println(wv.response);

		wv = wi.advance(0, 0, 1);
		System.out.println(wv.response);

		wv = wi.advance(1, 3, 0);
		System.out.println(wv.response);

		wv = wi.advance(1, 0, 2);
		System.out.println(wv.response);

		wv = wi.advance(1, 2, 0);
		System.out.println(wv.response);

		wv = wi.advance(0, 1, 2);
		System.out.println(wv.response);

		wv = wi.undo();
		System.out.println(wv.response);

		wv = wi.advance(0, 1, 3);
		System.out.println(wv.response);

		wi.close();
	}

}
