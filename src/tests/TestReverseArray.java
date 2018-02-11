package tests;

import java.util.Arrays;

public class TestReverseArray {

	private static int NUMITERATIONS = 100000000;

	public static int[] xorSwap(int[] array) {

		int i, left, right;
		int r;
		final int length = array.length;
		for (i = 0, r = length - 1; i < r; i += 1, r -= 1) {
			left = array[i];
			right = array[r];
			left ^= right;
			right ^= left;
			left ^= right;
			array[i] = left;
			array[r] = right;
		}
		return array;
	}

	public static int[] xorSwapHalf(int[] array) {
		int r;
		int left;
		int right;
		final int length = array.length;
		for (int i = 0; i < length / 2; i += 1) {
			r = length - 1 - i;
			left = array[i];
			right = array[r];
			left ^= right;
			right ^= left;
			left ^= right;
			array[i] = left;
			array[r] = right;
		}
		return array;
	}

	public static int[] forCopySwap(int[] array) {
		final int length = array.length;
		int[] copy = Arrays.copyOf(array, length);
		for (int left = 0, right = length - 1; left < right; left++, right--) {
			copy[left] = array[right];
			copy[right] = array[left];
		}
		return copy;
	}

	public static int[] forCopy(int[] array) {
		final int length = array.length;
		int[] copy = Arrays.copyOf(array, length);
		for (int left = 0, right = length - 1; left < length; left++, right--) {
			copy[left] = array[right];
			copy[right] = array[left];
		}
		return copy;
	}

	public static int[] forSwap(int[] array) {
		int left;
		int right;
		final int length = array.length;
		for (left = 0, right = length - 1; left < right; left += 1, right -= 1) {
			int temporary = array[left];
			array[left] = array[right];
			array[right] = temporary;
		}
		return array;
	}

	public static int[] forSwapHalf(int[] array) {
		int left;
		int right;
		final int length = array.length;
		for (left = 0; left < length / 2; left += 1) {
			right = length - 1 - left;
			int temporary = array[left];
			array[left] = array[right];
			array[right] = temporary;
		}
		return array;
	}

	public static void reverseBits() {
		long x = 0xab, z, y = 0xab;
		long start, end, time;
		start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			x = y + i;
			z = Long.reverse(x);
			y = Long.reverseBytes(z);
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("Bit-reversal for " + NUMITERATIONS / 1e6
				+ " mio. iterations: " + time / 1e6 + " ms!");
	}

	public static void measure() {

		long start, end, time;
		int[] x = new int[] { 4, 3, 2, 1, 0 };

		System.out.println("For " + NUMITERATIONS / 1e6 + " mio. iterations: ");

		start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			x = xorSwap(x);
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("xorSwap: " + time / 1e6 + " ms!");

		//
		start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			x = xorSwapHalf(x);
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("xorSwapHalf: " + time / 1e6 + " ms!");

		//
		start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			x = forSwap(x);
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("forSwap: " + time / 1e6 + " ms!");

		//
		start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			x = forSwapHalf(x);
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("forSwapHalf: " + time / 1e6 + " ms!");

		//
		start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			x = forCopy(x);
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("forCopy: " + time / 1e6 + " ms!");

		//
		start = System.nanoTime();
		for (int i = 0; i < NUMITERATIONS; i++) {
			x = forCopySwap(x);
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("forCopySwap: " + time / 1e6 + " ms!");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// int[] x = new int[] { 4, 3, 2, 1, 0 };
		// int[] y = forCopy(x);
		// System.out.println(Arrays.toString(y));
		// measure();
		// Long x = 0xABCDEFABL;
		// Long y = Long.reverse(x);
		// System.out.println(Long.toHexString(y));

		reverseBits();
	}

}
