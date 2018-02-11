package treeSearch;

import dotsAndBoxes.GameStateSnapshot;

public class TranspositionTable {
	public static class Entry {
		Entry(GameStateSnapshot s, short value, byte flag) {
			// Make a snapshot of s, since s will be changed afterwards
			this.s = s;
			this.value = value;
			this.flag = flag;
		}

		protected final GameStateSnapshot s;
		protected final short value;
		protected final byte flag;

		public static final byte EXACT = 1;
		public static final byte UPPER = 2;
		public static final byte LOWER = 3;
	}

	private static final int DEFAULT_SIZE = (int) Math.pow(2, 24); // 24

	private int size;
	private int numElements;

	Entry[] hashTable;

	public TranspositionTable() {
		this(DEFAULT_SIZE);
	}

	public TranspositionTable(int size) {
		if (Integer.bitCount(size) != 1)
			throw new IllegalArgumentException("Size must be a power of 2");
		this.size = size;
		hashTable = new Entry[size];
		numElements = 0;
	}

	public void put(GameStateSnapshot s, short value, byte flag) {
		assert (s instanceof GameStateSnapshot);

		int hash = s.hashCode();
		int index = hash & (size - 1);

		// always override last entry
		Entry e = hashTable[index];
		if (e == null)
			numElements++;
		hashTable[index] = new Entry(s, value, flag);

	}

	public int getNumElements() {
		return numElements;
	}

	public Entry get(GameStateSnapshot s) {
		int hash = s.hashCode();// GameStateB.linesHashCode(lines);
		int bucketIndex = hash & (size - 1);
		Entry e = hashTable[bucketIndex];
		if (e != null && e.s.equals(s))
			return e;
		return null;
	}

	@Override
	public String toString() {
		String s = new String();
		s += "Size: " + size + "\n";
		return s;
	}
}
