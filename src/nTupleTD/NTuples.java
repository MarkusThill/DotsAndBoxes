package nTupleTD;

import java.io.Serializable;
import java.util.Arrays;
import dotsAndBoxes.GameStateSnapshot;

public class NTuples implements Serializable {
	private static final long serialVersionUID = 2999287901723132870L;

	/**
	 * Hash-map to hash index-sets. This should improve the speed significantly,
	 * since the sampling of the board and its symmetric equivalents for every
	 * nTuple requires a lot of time.
	 */
	private transient IndexHashTable iht;

	// TODO: Since every sampling-point of the board will only be part of a few
	// n-tuples, the indexes of many n-tuples will not change if a new action
	// was performed. Maybe we can maintain a list that for every action gives
	// the n-tuples for which new indexes have to be generated.

	// [ntuple][samp point]
	// TODO: Maybe put as the last mask for every n-tuple a mask with all
	// sampling-points set. In this way we can check in the beginning if the
	// index will be unequal zero.
	private final NTuplePoint[][] nTuples;
	private final NTupleParams nPar;

	public NTuples(NTupleParams nPar, int m, int n) {
		this.nPar = nPar;
		iht = (nPar.useIndexHashTable ? new IndexHashTable() : null);

		//
		// Generate new n-tuples, if the current n-tuple list is empty or if
		// desired.
		//
		if(nPar.alwaysGenerateNewNtuples || nPar.nTupleMasks == null) {
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, m, n);
		}

		// Save often used parameters as class-attributes
		this.nTuples = nPar.nTupleMasks;

	}

	/**
	 * Samples the board (lines) for a certain n-tuple and returns the
	 * corresponding index for this n-tuple state
	 * 
	 * @param nTuple
	 *            The n-tuple used to sample the board
	 * @param lines
	 *            The board (lines of the board)
	 * @return The corresponding index
	 */
	public int sample(int nTuple, GameStateSnapshot s) {
		int index = 0;
		int pow = 1;
		final NTuplePoint[] tuple = nTuples[nTuple];
		final long[] lines = s.getLines();
		NTuplePoint p;
		for (int i = 0; i < tuple.length; i++) {
			p = tuple[i];
			if ((p.mask & lines[p.direction]) != 0L)
				index += pow;
			// Assume each sampling-point has 2 possible states
			pow <<= 1; // multiply by 2
		}
		return index;
	}

	public int[][] getIndexSet(GameStateSnapshot s) {
		// Get all equivilant positions based on mirroring and rotation
		// Snapshots are just static states, which cannot be changed in any way.
		// Saves some time to generate snapshots instead of complete game-states
		GameStateSnapshot[] equiv;
		if (nPar.useSymmetry) {
			equiv = nPar.useCornerSymmetry ? s.getAllSymmetricSnapshots() : s
					.getMirrorSymmetricSnapshots();
		} else
			equiv = new GameStateSnapshot[] { s };

		int[][] indexSet = null;

		// First do a hash-lookup to avoid sampling the whole board again
		// It seems like the sampling procedure is so fast, that a hash-table
		// does not really help in improving the speed
		if (iht != null) {
			GameStateSnapshot[] lookup = nPar.useSymmetricHashing ? equiv
					: new GameStateSnapshot[] { equiv[0] };
			indexSet = hashLookupIndexSet(lookup);
		}

		if (indexSet != null)
			return indexSet;

		// Otherwise generateIndexSet
		indexSet = generateIndexSet(equiv);

		// hash indexSet
		if (iht != null)
			iht.put(equiv[0], indexSet);
		return indexSet;
	}

	/**
	 * Should generally not be called from other places than this class. Samples
	 * for every n-tuple all given equivalent boards (a set of equivalent
	 * game-states under mirroring/rotation) of the current state.
	 * 
	 * @param equiv
	 *            A set of equivalent game-states (under mirroring/rotation).
	 * @return A set of indexes, identifying the current state of each n-tuple.
	 *         The first index addresses the n-tuple, the second the index of
	 *         all equivalent boards of a state.
	 */
	public int[][] generateIndexSet(GameStateSnapshot[] equiv) {
		int numTuples = nTuples.length;
		int numEquiv = equiv.length;
		int index, i, j, k;
		int[][] indexSet = new int[numTuples][];
		int[] tmp = new int[numEquiv];
		// For all n-tuples generate the indexes
		for (i = 0; i < numTuples; i++) {
			for (j = 0, k = 0; j < numEquiv; j++) {
				index = sample(i, equiv[j]);
				// Since some indexes may be duplicate, store these only once in
				// the indexSet. Therefore, check if index is already contained
				// in the current set
				if (!contains(tmp, index, k))
					tmp[k++] = index;
			}
			indexSet[i] = Arrays.copyOf(tmp, k);
		}
		return indexSet;
	}

	/**
	 * Look for the value x in the array until a certain index.
	 * 
	 * @param array
	 *            Array to search for value.
	 * @param x
	 *            value that we want to find in the array.
	 * @param until
	 *            search beginning with index 0 to this given index (exclusive).
	 * @return True, if we find value x in the array, otherwise false.
	 */
	private boolean contains(int[] array, int x, int until) {
		assert (until <= array.length);
		for (int i = 0; i < until; i++)
			if (array[i] == x)
				return true;
		return false;
	}

	public int[][] hashLookupIndexSet(GameStateSnapshot[] equivLines) {
		// For all equivalent positions
		int[][] indexSet = null;
		for (int i = 0; i < equivLines.length && indexSet == null; i++) {
			indexSet = iht.get(equivLines[i]);
		}
		return indexSet;
	}

	public String hashStatistics() {
		return iht.toString();
	}

	/**
	 * @return The number of theoretically possible states for an n-tuple
	 */
	public int getStateCount(int nTuple) {
		return (1 << nTuples[nTuple].length);
	}

	/**
	 * @return The number of n-tuples for this n-tuple system
	 */
	public int getTupleCount() {
		return nTuples.length;
	}
}
