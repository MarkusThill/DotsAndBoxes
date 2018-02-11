package tests;

import static org.junit.Assert.*;
import nTupleTD.IndexHashTable;
import org.junit.Test;
import dotsAndBoxes.GameState;

public class IndexHashTableTest {

	@Test
	public void testPut() {
		int numIterations = (int) 1e6;
		IndexHashTable iht = new IndexHashTable();
		

		long start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			GameState s = new GameState(3, 3, false);
			while (s.advance(true) ) {
				int[][] x = new int[100][8];
				x[99][4] = 0;
				if (iht.get(s) == null) {
					iht.put(s, x);
					assertTrue(x == iht.get(s));
				}
			}
			if(i % 1000 == 0)
				System.gc();
		}
		long end = System.nanoTime();
		long time = end -start;
		System.out.println("Buckets used: " + iht.getBucketsUsed()
				+ "! Num-Elements: " + iht.getNumElements());
		System.out.println("Time: " + time/1e6 + "ms");

	}
}
