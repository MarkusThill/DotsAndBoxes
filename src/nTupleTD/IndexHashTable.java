package nTupleTD;

import java.io.Serializable;

import dotsAndBoxes.GameStateSnapshot;

public class IndexHashTable implements Serializable {

	private static final long serialVersionUID = 3879167970278040114L;

	private static class BucketEntry implements Serializable {
		private static final long serialVersionUID = -8655551465893131035L;
		BucketEntry(GameStateSnapshot s, int[][] indexSet) {
			this.s = s;
			this.indexSet = indexSet;
		}

		final GameStateSnapshot s;
		final int[][] indexSet;
		BucketEntry nextEntry = null;

	}

	private static int MAX_BUCKET_SIZE = 2;
	private static final int DEFAULT_SIZE = (int) Math.pow(2, 10);

	private int size;
	private int bucketsUsed;
	private int numElements;

	BucketEntry[] bucketList;

	public IndexHashTable() {
		this(DEFAULT_SIZE);
	}

	public IndexHashTable(int size) {
		if (Integer.bitCount(size) != 1)
			throw new IllegalArgumentException("Size must be a power of 2");
		this.size = size;
		bucketList = new BucketEntry[size];
		bucketsUsed = 0;
		numElements = 0;
	}

	// chose indexSet[][] over indexSet[], since every line of the 2-dim. array
	// will address the same LUT, which should be faster.
	public void put(GameStateSnapshot s, int[][] indexSet) {
		//assert (lines.length == 2);
		int hash = s.hashCode();//GameStateB.linesHashCode(lines);
		int bucketIndex = hash & (size - 1);
		int bucketSize = 1;

		// Iterate through bucket until the end is reached
		BucketEntry e = bucketList[bucketIndex];
		if (e == null) {
			bucketList[bucketIndex] = new BucketEntry(s, indexSet);
			bucketsUsed++;
			numElements++;
			return;
		}

		// Linked List
		//assert (false == Arrays.equals(lines, e.lines));
		while (e.nextEntry != null) {
			// Check if entry can be found. Should normally not be the case
			assert (false == s.linesEquals(e.s));
			e = e.nextEntry;
			bucketSize++;
			// If the bucket-size exceeds a certain limit, then delete the first
			// entry, which is the oldest
			if (bucketSize >= MAX_BUCKET_SIZE) {
				BucketEntry first = bucketList[bucketIndex];
				BucketEntry newE = new BucketEntry(s, indexSet);
				newE.nextEntry = first.nextEntry;
				bucketList[bucketIndex] = newE;
				return;
			}
		}
		// add new entry to the end of the list
		e.nextEntry = new BucketEntry(s, indexSet);
		numElements++;

	}

	public int getBucketsUsed() {
		return bucketsUsed;
	}

	public int getNumElements() {
		return numElements;
	}

	public int[][] get(GameStateSnapshot s) {
		int hash = s.hashCode();//GameStateB.linesHashCode(lines);
		int bucketIndex = hash & (size - 1);
		BucketEntry e = bucketList[bucketIndex];
		while (e != null) {
			if (s.linesEquals(e.s))
				return e.indexSet;
			e = e.nextEntry;
		}
		return null;
	}

	@Override
	public String toString() {
		String s = new String();
		s += "Size: " + size + "\n";
		s += "Buckets used: " + bucketsUsed + " ("
				+ (bucketsUsed / (float) size * 100) + "%) \n";
		s += "Total elements: " + numElements + " (avg. "
				+ ((float) numElements / bucketsUsed) + " elements per bucket)";
		return s;
	}

}
