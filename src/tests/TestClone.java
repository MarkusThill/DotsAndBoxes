package tests;

import java.util.ArrayList;
import java.util.Arrays;

import tools.DeepArrayFactory;

import dotsAndBoxes.GameStateArray;

public class TestClone {

	private static final int NUMITERATIONS = 10000000;

	public static void testCopyArrayList() {
		// Test, how much time is needed for copying an array n times.
		ArrayList<int[]> al = GameStateArray.generateMoveList(4, 4);

		long start = System.nanoTime();
		// Make a shallow copy of al
		for (int i = 0; i < NUMITERATIONS; i++) {
			ArrayList<int[]> x = new ArrayList<int[]>(al);
			// Just something random
			x.add(new int[3]);
		}
		long end = System.nanoTime();
		long time = end - start;
		System.out.println("Time for copying move-list " + NUMITERATIONS / 1e6
				+ " mio. iterations: " + time / 1e6 + "ms");
	}
	
	public static void testCopyArrayListByte() {
		// Test, how much time is needed for copying an array n times.
		//ArrayList<int[]> al = GameState.generateMoveList(4, 4);

		ArrayList<Byte> al  = new ArrayList<Byte>(2*4*4+8);
		
		long start = System.nanoTime();
		// Make a shallow copy of al
		for (int i = 0; i < NUMITERATIONS; i++) {
			ArrayList<Byte> x = new ArrayList<Byte>(al);
			// Just something random
			Byte p = (byte)1;
			x.add(p);
		}
		long end = System.nanoTime();
		long time = end - start;
		System.out.println("Time for copying move-list of Bytes" + NUMITERATIONS / 1e6
				+ " mio. iterations: " + time / 1e6 + "ms");
	}

	public static void deepCopyArray() {
		//
		GameStateArray s = new GameStateArray(4, 4);

		while (s.advance(true))
			;
		int[][][] x = s.getLines();

		// Start deep copy of the lines array
		long start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			x[0][0][0]++; // Just something
			int[][][] y = DeepArrayFactory.clone(x);
			y[0][0][0] = 1; // Just something random
		}

		long end = System.nanoTime();
		long time = end - start;
		System.out.println("Time for copying lines-arrays for " + NUMITERATIONS
				/ 1e6 + " mio. iterations: " + time / 1e6 + "ms");
	}

	public static void deepCloneState() {
		//
		GameStateArray s = new GameStateArray(7, 7);
		// Start deep copy of the lines array
		long start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			@SuppressWarnings("unused")
			GameStateArray x = new GameStateArray(s);
		}

		long end = System.nanoTime();
		long time = end - start;
		System.out.println("Time for copying a GameState (7x7) for " + NUMITERATIONS
				/ 1e6 + " mio. iterations: " + time / 1e6 + "ms");

	}

	public static void deepHash() {
		// A different sequence of moves should lead to the same hash-code,
		// since the state is the same
		GameStateArray s = new GameStateArray(4, 4);
		s.advance(3);
		s.advance(2);
		int[][][] x = s.getLines();
		long hash1 = Arrays.deepHashCode(x);

		GameStateArray s2 = new GameStateArray(4, 4);
		s2.advance(2);
		s2.advance(2); // since action 3 was removed
		int[][][] x2 = s2.getLines();
		long hash2 = Arrays.deepHashCode(x2);

		System.out.println("Hash1: " + hash1 + " hash2: " + hash2 + "!");

		//
		long start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			long hash = Arrays.deepHashCode(x);
			hash1 = hash;
		}

		long end = System.nanoTime();
		long time = end - start;
		System.out.println("Time for calculating a State-hashCode for "
				+ NUMITERATIONS / 1e6 + " mio. iterations: " + time / 1e6
				+ "ms");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testCopyArrayList();
		//testCopyArrayListByte();
		// deepCopyArray();
		deepCloneState();
		
		deepHash();
	}

}
