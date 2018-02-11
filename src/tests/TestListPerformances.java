package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import tools.DeepArrayFactory;
import dotsAndBoxes.GameStateArray;
import dotsAndBoxes.GameStateFactory;

public class TestListPerformances {
	private static final Random rand = new Random();
	private static final int ITERATIONS = 10000000;
	int [][] actions;
	
	// ArrayList
	private ArrayList <int[]> init;
	
	// ArrayList
	private ArrayList <int[]> al;
	
	// Hash Set
	HashSet <int[]> hs;
	
	// Hash-Map
	HashMap <Integer, int[]> hm;
	
	// Linked List
	LinkedList <int[]> ll;
	
	public TestListPerformances() {
		init = GameStateArray.generateMoveList(5, 5);		
	}
	
	
	public void speedArrayList() {
		long time = 0;
		long start;
		long stop;
		for(int i=0;i<ITERATIONS;i++) {
			// First create list
			al = new ArrayList<int[]>(init);
			
			start = System.nanoTime();
			while(!al.isEmpty()) {
				//int size = al.size();
				//al.remove(rand.nextInt(size));
				al.remove(0);
			}
			stop = System.nanoTime();
			
			time += (stop - start);
		}
		System.out.println("Time for Array-List: " + time/1e6 + "ms");
	}
	
	public void speedHashMap() {
		long time = 0;
		long start;
		long stop;
		for(int i=0;i<ITERATIONS;i++) {
			hm = new HashMap<Integer, int[]>(init.size());
			for(int j=0;j<init.size();j++)
				hm.put(j, init.get(j));
			
			start = System.nanoTime();
			int j = 0;
			while(!hm.isEmpty()) {
				//int[] x = hm.get(j);
				hm.remove(j);
				j++;
			}
			stop = System.nanoTime();
			
			time += (stop - start);
		}
		System.out.println("Time for Hash-Map: " + time/1e6 + "ms");
	}
	
	public void speedLinkedList() {
		long time = 0;
		long start;
		long stop;
		for(int i=0;i<ITERATIONS;i++) {
			// First create list
			ll = new LinkedList<int[]>(init);
			
			start = System.nanoTime();
			while(!ll.isEmpty()) {
				int size = ll.size();
				ll.remove(rand.nextInt(size));
			}
			stop = System.nanoTime();
			
			time += (stop - start);
		}
		System.out.println("Time for Linked-List: " + time/1e6 + "ms");
	}
	
	public void testNestedListCopy() {
		ArrayList<ArrayList<Byte>> x = GameStateFactory.generateMoveList(7, 7);
		ArrayList<ArrayList<Byte>> y = DeepArrayFactory.clone(x);
		
		x.get(0).remove(0);
		System.out.println(x.get(0).size() + " " + y.get(0).size());
		@SuppressWarnings("unused")
		Byte f = x.get(0).get(0);
		System.out.println(x.get(0).get(0) + " " + y.get(0).get(0));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestListPerformances t = new TestListPerformances();
		//t.speedArrayList();
		//t.speedHashMap();
		//t.speedLinkedList();
		t.testNestedListCopy();
	}

}
