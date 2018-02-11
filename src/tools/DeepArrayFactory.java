package tools;

import java.util.ArrayList;
import java.util.Arrays;


public class DeepArrayFactory {

	public static int[][][] clone(int[][][] x) {
		int[][][] y;
		y = Arrays.copyOf(x, x.length);
		for (int i = 0; i < y.length; i++)
			y[i] = Arrays.copyOf(x[i], x[i].length);
		for (int i = 0; i < y.length; i++)
			for (int j = 0; j < y[i].length; j++)
				y[i][j] = Arrays.copyOf(x[i][j], x[i][j].length);
		// y[i][j] = x[i][j].clone(); Arrays.copyof() is slightly faster
		return y;
	}

	public static int[][] clone(int[][] x) {
		int[][] y;
		y = Arrays.copyOf(x, x.length);
		for (int i = 0; i < y.length; i++)
			y[i] = Arrays.copyOf(x[i], x[i].length);
		return y;
	}
	
	public static ArrayList<ArrayList<Byte>> clone(ArrayList<ArrayList<Byte>> x) {
		int size = x.size();
		ArrayList<ArrayList<Byte>> y = new ArrayList<ArrayList<Byte>>(size);
		for(int i=0;i<size;i++) {
			ArrayList<Byte> nest = new ArrayList<Byte>(x.get(i));
			y.add(nest);
		}
		return y;
	}
	
	public static long deepHash(int[][][] x) {
		return Arrays.deepHashCode(x);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
