package nTupleTD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class NTuplePoint implements Comparable<NTuplePoint>,  Serializable {
	private static final long serialVersionUID = -7202442399212447491L;
	public final int direction;
	public final long mask;

	/**
	 * Not really needed. The JAXB-interface, however, requires a
	 * default-constructor.
	 */
	@SuppressWarnings("unused")
	private NTuplePoint() {
		direction = 0;
		mask = 0;
	}

	public NTuplePoint(int direction, long mask) {
		this.direction = direction;
		this.mask = mask;
	}

	@Override
	public String toString() {
		return "(" + direction + "," + mask + ")";
	}

	@Override
	public int compareTo(NTuplePoint o) {
		if (direction != o.direction)
			return direction - o.direction;
		return Long.signum(mask - o.mask);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NTuplePoint) {
			NTuplePoint p = (NTuplePoint) o;
			return p.direction == direction && p.mask == mask;
		}
		return false;
	}

	public static boolean equals(NTuplePoint[] o1, NTuplePoint[] o2) {
		// Two n-tuples are the same, if they contain the same points, the
		// ordering does not matter
		if (o1.length != o2.length)
			return false; // both n-tuples cannot be equal
		ArrayList<NTuplePoint> a1 = new ArrayList<NTuplePoint>(o1.length);
		ArrayList<NTuplePoint> a2 = new ArrayList<NTuplePoint>(o2.length);

		for (int i = 0; i < o1.length; i++) {
			a1.add(o1[i]);
			a2.add(o2[i]);
		}

		boolean equals = true;
		for (Iterator<NTuplePoint> i = a1.iterator(); i.hasNext() && equals;) {
			equals = a2.contains(i.next());
		}
		return equals;
	}

}