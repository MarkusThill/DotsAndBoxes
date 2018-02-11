package tools;

public class BitFiddling {
	public static final long[] BYTEMASKS;

	static {
		BYTEMASKS = generateByteMasks();
	}

	private static long[] generateByteMasks() {
		// Masks for all bytes of a long-variable
		long m = 0xFFL;
		// We have 8 bytes in a long
		long[] masks = new long[Long.SIZE / Byte.SIZE];
		masks[0] = m;
		for (int i = 1; i < masks.length; i++) {
			m <<= Byte.SIZE;
			masks[i] = m;
		}
		return masks;
	}

	// TODO: Move to another class
	public static long rol(long x, int distance, int bitSubset) {
		// We have to make sure that values larger than 63 for bitSubset the
		// value of mask is -1. The problem is, that a shift by a value x>63
		// results in a shift by x mod 64. This is not what we want.
		long mask = ~-((bitSubset & 0x40) == 0 ? 1L << bitSubset : 0L);
		return ((x & mask) >>> (bitSubset - distance))
				| ((x << distance) & mask);
	}

	// TODO: Move to another class
	public static long swapTwoBits(long x, long mask) {
		long bitTest = x & mask;
		// If both bits are zero, no swap needed
		if (bitTest != 0L) {
			// if only one bit is set, we have to swap the bits
			if ((bitTest & (bitTest - 1)) == 0L)
				return x ^ mask;
			// If both bits are set, no swap is needed
		}
		return x;
	}

	public static long swapTwoBits(long x, int i, int j) {
		// precondition: i > j
		int d = i - j;
		long y = (x ^ (x >>> d)) & (1L << j);
		return x ^ y ^ (y << d);
	}
	
	public static long maskBytes(long x, int[] bytes) {
		long mask = 0L;
		for(int i : bytes)
			mask |= BYTEMASKS[i];
		return x & mask;
	}
	
	public static long getByteMask(int[] indexes) {
		long mask = 0L;
		for (int i : indexes) {
			mask |= BYTEMASKS[i];
		}
		return mask;
	}

	public static long getMask(int[] indexes, boolean bytes) {
		long mask = 0L;
		if(!bytes)
		for (int i : indexes) {
			mask |= (1L << i);
		}
		else
			for (int i : indexes) {
				mask |= BYTEMASKS[i];
			}
		return mask;
	}

	public static long swapBytes(long x, int i, int j) {
		assert (i < j);
		int distance = j - i;
		// shift lower byte i to the higher byte j
		long result = (x & BYTEMASKS[i]) << distance * Byte.SIZE;
		
		// shift byte j to the lower byte i
		result |= (x & BYTEMASKS[j]) >>> distance * Byte.SIZE;

		// add the remaining bytes
		result |= x & (-1L ^ BYTEMASKS[i] ^ BYTEMASKS[j]);
		return result;
	}
	
	public static long swapAndShiftBytes(long x, int i, int j, int offSetR) {
		assert (i < j);
		int distance = j - i;
		// shift lower byte i to the higher byte j
		// A shift to the right with shiftR reduces the shift to the left 
		long result = (x & BYTEMASKS[i]) << (distance * Byte.SIZE - offSetR);
		
		// shift byte j to the lower byte i
		result |= (x & BYTEMASKS[j]) >>> (distance * Byte.SIZE + offSetR);

		// add the remaining bytes
		result |= x & (-1L ^ BYTEMASKS[i] ^ BYTEMASKS[j]);
		return result;
	}
	
	/**
	 * Fast Algorithm to calculate the position of a set Bit in a long-variable.
	 * Algorithm-complexity for variables with the length n: O(log(n))
	 * 
	 * @param x
	 *            Number in the form 2^n
	 * @return binary logarithm (Position of the set Bit in a long-variable)
	 */
	public static int ld(long x) {
		int ld = 0;
		long mask = 0xFFFFFFFFL;
		int n = 32;

		while (n != 0) {
			if ((x & mask) == 0l) {
				ld += n;
				x >>= n;
			}
			n >>= 1;
			mask >>= n;
		}
		return ld;
	}

}
