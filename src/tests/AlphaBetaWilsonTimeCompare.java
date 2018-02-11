package tests;

import java.util.List;
import org.junit.Test;
import dotsAndBoxes.GameState;
import dotsAndBoxes.Move;
import solvers.WilsonAgent;
import treeSearch.AlphaBeta;

public class AlphaBetaWilsonTimeCompare {
	
	@Test
	public void test3x3() {
		//
		// Test for a 2x2 board, when the Alpha-Beta search will become faster
		// than Wilsons-solver.

		runTest(3, 3, 100, 0, 12);

		//// @formatter:off
		// Result
		// NUMLINES	Wilson		AlphaBeta
//		0		476ms		4173ms
//		1		235ms		8389ms
//		2		493ms		13342ms
//		3		840ms		20317ms
//		4		1071ms		20030ms
//		5		1296ms		17769ms
//		6		1909ms		15259ms
//		7		2010ms		10302ms
//		8		2059ms		5012ms
//		9		1998ms		3308ms
//		10		2200ms		1845ms
//		11		2068ms		1260ms
		// @formatter:on
	}
	
	@Test
	public void test2x3() {
		//
		// Test for a 2x2 board, when the Alpha-Beta search will become faster
		// than Wilsons-solver.

		runTest(2, 3, 1000, 0, 12);

		//// @formatter:off
		// Result
		//
//		NUMLINES	Wilson		AlphaBeta
//		0		5436ms		715ms
//		1		3834ms		166ms
//		2		5844ms		413ms
//		3		7450ms		534ms
//		4		9609ms		638ms
//		5		10033ms		800ms
//		6		13377ms		695ms
//		7		15292ms		520ms
//		8		17114ms		504ms
//		9		17020ms		411ms
//		10		21463ms		63ms
//		11		24190ms		17ms
		// @formatter:on
	}

	@Test
	public void test2x2() {
		//
		// Test for a 2x2 board, when the Alpha-Beta search will become faster
		// than Wilsons-solver.

		runTest(2, 2, 1000, 0, 12);

		//// @formatter:off
		// Result
		//
		
//		NUMLINES	Wilson		AlphaBeta
//		0			4841ms			208ms
//		1			3697ms			24ms
//		2			5801ms			11ms
//		3			6919ms			108ms
//		4			8845ms			13ms
//		5			9815ms			9ms
//		6			12546ms			7ms
//		7			11690ms			5ms
//		8			16554ms			2ms
//		9			18691ms			0ms
//		10			19917ms			3ms
//		11			17785ms			0ms
//
		// @formatter:on
	}

	public void runTest(int m, int n, int NUMBOARDS, int STARTNUMLINES,
			int NUMLINES) {
		WilsonAgent wa = new WilsonAgent(m, n);

		// int NUMBOARDS = 10000;

		System.out.println("NUMLINES\tWilson\t\tAlphaBeta");
		for (int k = STARTNUMLINES; k < NUMLINES; k++) {
			long timeWilson = 0;
			long timeAlphaBeta = 0;
			AlphaBeta ab = new AlphaBeta(); // to be fair...
			for (int i = 0; i < NUMBOARDS; i++) {
				GameState s = new GameState(m, n);
				for (int j = 0; j < k; j++) {
					s.advance(true);
				}

				//
				// Wilsons Solver
				//
				long start = System.currentTimeMillis();
				List<Move> x = wa.getBestMoves(s);
				x.remove(0);
				long end = System.currentTimeMillis();
				long time = end - start;
				timeWilson += time;

				//
				// Alpha-Beta
				//

				start = System.currentTimeMillis();
				x = ab.getBestMoves(s);
				end = System.currentTimeMillis();
				time = end - start;
				timeAlphaBeta += time;
			}
			System.out.print(k + "\t\t");
			System.out.print(timeWilson + "ms\t\t");
			System.out.print(timeAlphaBeta + "ms\n");
		}
	}

}
