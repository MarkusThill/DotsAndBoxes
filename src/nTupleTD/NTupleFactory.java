package nTupleTD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import nTupleTD.NTupleParams.TupleGeneration;
import dotsAndBoxes.GameStateFactory;

//TODO: make sure, that n-tuples are not the same under
// rotation/reflection in the generation-process
// Increment the number of training games played
public class NTupleFactory {
	private static final Random RND = tools.SRandom.RND;

	// TODO: undo again
	public static class SamplePoint implements Comparable<SamplePoint> {
		int direction;
		int bitIndex;

		public SamplePoint(int direction, int bitIndex) {
			this.direction = direction;
			this.bitIndex = bitIndex;
		}

		@Override
		public boolean equals(Object o) {
			SamplePoint s = (SamplePoint) o;
			return (s.direction == direction && s.bitIndex == bitIndex);
		}

		@Override
		public int compareTo(SamplePoint o) {
			if (direction != o.direction)
				return direction - o.direction;
			// Sign not really needed, but is nicer to have +1, 0, -1
			return Integer.signum(bitIndex - o.bitIndex);
		}

		@Override
		public String toString() {
			return "(" + direction + ", " + bitIndex + ")";
		}
	}

	public static NTuplePoint[] createNTuple(int[] direction, int[] bitIndex) {
		assert (direction.length == bitIndex.length);
		SamplePoint[] s = new SamplePoint[direction.length];
		for (int i = 0; i < direction.length; i++)
			s[i] = new SamplePoint(direction[i], bitIndex[i]);
		return createNTuple(s);
	}

	public static NTuplePoint[] createNTuple(SamplePoint[] s) {
		NTuplePoint[] tup = new NTuplePoint[s.length];
		for (int i = 0; i < s.length; i++) {
			tup[i] = new NTuplePoint(s[i].direction, 1L << s[i].bitIndex);
		}
		return tup;
	}

	public static List<SamplePoint> randomBoxWalk(NTupleParams nPar, int m,
			int n) throws TimeoutException {
		ArrayList<SamplePoint> sp = new ArrayList<>();
		int MAX_TRYS = 40;

		//
		// Starting point
		//
		int i = RND.nextInt(m);
		int j = RND.nextInt(n);

		int[][] bitIndexes = GameStateFactory.getBoxLines(i, j, m, n);
		for (int[] k : bitIndexes) {
			SamplePoint p = new SamplePoint(k[0], k[1]);
			sp.add(p);
		}

		while (sp.size() < nPar.tupleLen) {
			int timeOutCounter = 0;
			boolean addedLines = false;
			do {
				//
				// We can only make a step in one direction
				//
				int di;
				int dj;
				do {
					di = RND.nextInt(3) - 1;
					dj = RND.nextInt(3) - 1;
				} while (Math.abs(di) == Math.abs(dj));
				//
				// Check, if step in this direction is legal
				//
				if (i + di >= 0 && i + di < m && j + dj >= 0 && j + dj < n) {
					i += di;
					j += dj;
					bitIndexes = GameStateFactory.getBoxLines(i, j, m, n);
					for (int[] k : bitIndexes) {
						SamplePoint p = new SamplePoint(k[0], k[1]);
						if (!sp.contains(p)) {
							addedLines = true;
							sp.add(p);
						}
					}
				}
				if (!addedLines)
					timeOutCounter++;
				if (timeOutCounter >= MAX_TRYS)
					throw new TimeoutException();
			} while (!addedLines);

		}
		return sp;

	}

	public static NTuplePoint[] createNTuple(NTupleParams nPar, int m, int n)
			throws TimeoutException {

		int tupleLen = nPar.tupleLen;
		boolean randLength = nPar.randomLength;
		ArrayList<SamplePoint> al = new ArrayList<SamplePoint>(tupleLen);
		int[] p = null, oldP = null;
		SamplePoint s = null;
		int trys = 0;
		int MAX_TRYS = 40;

		//
		// If we have a random box-walk, generate the sample points already
		// here, since every box adds 3-4 lines
		//
		List<SamplePoint> boxWalkSP = null;
		if (nPar.tupleGen == TupleGeneration.RAND_BOX_WALK)
			boxWalkSP = randomBoxWalk(nPar, m, n);

		for (int i = 0; i < tupleLen; i++) {
			do {
				// The algorithm can get stuck somewhere in certain cases,
				// handle this
				if (trys >= MAX_TRYS) {
					throw new TimeoutException(
							"Could not find a valid Sampling point");
				}
				switch (nPar.tupleGen) {
				case RAND_LINES:
					s = createRandomPoint(m, n);
					break;
				case RAND_LINE_WALK:
					if (s == null) {
						p = GameStateFactory.randomStep(null, m, n);
					} else {
						p = GameStateFactory.randomStep(p, m, n);
						int bitIndex = GameStateFactory.getLineBitIndex(oldP,
								m, n);
						s = new SamplePoint(p[0], bitIndex);
					}
					int bitIndex = GameStateFactory.getLineBitIndex(p, m, n);
					s = new SamplePoint(p[0], bitIndex);
					break;
				case RAND_BOX_WALK:
					s = boxWalkSP.remove(0);
					break;
				default:
					throw new UnsupportedOperationException(
							"Not supported yet!");

				}
				trys++;
			} while (!randLength && al.contains(s));
			if (!al.contains(s))
				al.add(s);
			oldP = p;
		}

		assert (randLength || al.size() == tupleLen);

		// Get sample points as an array
		SamplePoint[] ss = al.toArray(new SamplePoint[al.size()]);
		Arrays.sort(ss);
		return createNTuple(ss);
	}

	private static SamplePoint createRandomPoint(int m, int n) {
		SamplePoint s;
		int dir = RND.nextInt(2); // 0 or 1
		int bit = GameStateFactory.getRandomBitIndex(dir, m, n);
		s = new SamplePoint(dir, bit);
		return s;
	}

	public static NTuplePoint[][] createNTupleList(NTupleParams nPar, int m,
			int n) {
		int numTuples = nPar.tupleNum;
		ArrayList<NTuplePoint[]> al = new ArrayList<NTuplePoint[]>(numTuples);
		int counter = 0;
		while (al.size() < numTuples) {
			NTuplePoint[] tuple = null;
			counter = 0;
			do {
				try {
					tuple = createNTuple(nPar, m, n);
				} catch (TimeoutException e) {
				}
				counter++;
				if (counter >= 100)
					throw new UnsupportedOperationException(
							"Could not gnerate more n-tuples. This may"
									+ " be due to the fact that no more"
									+ " unique n-tuples can be generated.");

			} while (tuple != null && contains(al, tuple));
			if (tuple != null)
				al.add(tuple);
		}
		assert (al.size() == numTuples);
		return al.toArray(new NTuplePoint[numTuples][]);
	}

	/**
	 * Check if a List of n-tuples contains the given tuple
	 * 
	 * @param tupleList
	 *            The list of n-tuples
	 * @param tuple
	 *            The n-tuple that shall be searched in the list
	 * @return true, if tuple was found in tupleList, otherwise false
	 */
	public static boolean contains(ArrayList<NTuplePoint[]> tupleList,
			NTuplePoint[] tuple) {
		boolean contains = false;
		for (Iterator<NTuplePoint[]> i = tupleList.iterator(); i.hasNext()
				&& !contains;) {
			contains = NTuplePoint.equals(i.next(), tuple);
		}
		return contains;
	}

	public static String toString(NTuplePoint[] tup, int m, int n) {
		long[] lines = new long[2];
		for (int i = 0; i < tup.length; i++)
			lines[tup[i].direction] |= tup[i].mask;

		return GameStateFactory.toString(lines, m, n);
	}

	public static void main(String[] args) throws TimeoutException {
		int m = 4, n = 4;
		NTupleParams nPar = new NTupleParams();
		nPar.tupleLen = 14;
		nPar.tupleNum = 10;
		nPar.tupleGen = TupleGeneration.RAND_LINES;
		nPar.randomLength = false;

		for (int i = 0; i < 100; i++) {
			try {
				NTuplePoint[] tuple = createNTuple(nPar, m, n);
				long lines[] = new long[2];
				for (NTuplePoint p : tuple) {
					lines[p.direction] |= p.mask;
				}

				System.out.println(GameStateFactory.toString(lines, m, n));
			} catch (TimeoutException e) {

			}

		}
	}
}
