package nTupleTD;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import dotsAndBoxes.GameStateSnapshot;

@XmlRootElement
public class NTupleParams implements Serializable {
	private static final long serialVersionUID = 8298487633843620354L;

	@XmlElement
	public int tupleLen = 10;

	@XmlElement
	public int tupleNum = 100;

	@XmlElement
	public boolean randomLength = false;

	/**
	 * If set to true, then always new n-tuples are generated in each
	 * training-run. Otherwise, n-tuples are only then generated for this params
	 * set, if {@link NTupleParams#nTupleMasks} is null.
	 */
	@XmlElement
	public boolean alwaysGenerateNewNtuples = false;

	@XmlRootElement
	public enum TupleGeneration {
		RAND_LINES, RAND_LINE_WALK, RAND_BOX_WALK
	}

	@XmlElement
	public TupleGeneration tupleGen = TupleGeneration.RAND_LINES;

	/**
	 * At the moment, it looks like that hashing cannot improve the speed
	 * significantly. The sampling algorithm is almost as fast as a hash-lookup.
	 */
	@XmlElement
	public boolean useIndexHashTable = true;

	@XmlElement
	public boolean useSymmetricHashing = false;

	/**
	 * If turned off, only the given board will be sampled. All symmetric
	 * positions (equivalent under mirroring rotation) will not be evaluated.
	 * Turning this parameter off will typically decrease the training-progress
	 * of the agent.
	 */
	@XmlElement
	public boolean useSymmetry = true;

	/**
	 * Using the corner-symmetry as described by Barker & Korf in their paper
	 * can drastically increase the number of symmetric positions (up to 128 for
	 * a quadratic board). Used when retrieving the index-set for a position.
	 */
	@XmlElement
	public boolean useCornerSymmetry = true;

	/**
	 * * Based on the Game-State it can be sensible to have different LUTs. A
	 * n-tuple system only samples the line of the board, other information such
	 * as the box-difference or the player to move are not considered there.
	 * Therefore, identical n-tuple states can be created from board-positions
	 * with different game-theoretic values. In such cases it can be useful to
	 * create several LUTs for different box-differences and/or separate LUTs
	 * for both players. This function defines, which LUT-subset is selected.
	 * For example, we could define the function in a way, that a box-difference
	 * of -9 (on a 3x3 board) would correspond to an LUT-subset index of 0, a
	 * box-difference of 0 would correspond to an LUT-subset index of 9, and a
	 * box-difference of +9 would correspond to an LUT-subset index of 18. Used
	 * to be an Interface, to allow more general definitions of a LUT-Set
	 * selection function. In order to save the file as an XML-file, now just
	 * allow a simple linear function.
	 */
	@XmlRootElement
	public static class LUTSelection implements Serializable {
		private static final long serialVersionUID = 2069397638828521915L;

		@XmlElement
		private float offset;

		@XmlElement
		private float aP;

		@XmlElement
		private float aBoxDiff;

		@XmlElement
		private float aP_BoxDiff;

		public LUTSelection() {
			this.offset = 0.0f;
			this.aP = 0.0f;
			this.aBoxDiff = 0.0f;
			this.aP_BoxDiff = 0.0f;
		}

		/**
		 * A simple function, that selects the current LUT-set based on the
		 * player to move and the current box-difference. The coefficients are
		 * defined, when an instance of this class is generated. <br>
		 * Example 1: If we want to select the LUTs simply based on the player
		 * to move (A, B), we have to define the coefficients in the following
		 * way: <br>
		 * <code> &nbsp;&nbsp; offset = 0.5f <br>&nbsp;&nbsp; aP = -0.5f 
		 * <br>&nbsp;&nbsp; aBoxDiff = 0.0f <br>&nbsp;&nbsp; aP_BoxDiff = 0.0f </code>
		 * <br>
		 * <br>
		 * Example 2: If we want to select the LUT-set depending on the player
		 * to move (A, B) and the box-difference for a 2x2 board (-3 to +3,
		 * since positions with +-4 are terminal positions and therefore not
		 * learnt) we can define our function in the following way:
		 * <code> &nbsp;&nbsp; offset = 6.5f <br>&nbsp;&nbsp; aP = -3.5f 
		 * <br>&nbsp;&nbsp; aBoxDiff = 1.0f <br>&nbsp;&nbsp; aP_BoxDiff = 0.0f </code>
		 * 
		 * @param s
		 *            The current game-state as a snapshot
		 * @return Selected LUT
		 */
		public LUTSelection(float offset, float aP, float aBoxDiff,
				float aP_BoxDiff) {
			this.offset = offset;
			this.aP = aP;
			this.aBoxDiff = aBoxDiff;
			this.aP_BoxDiff = aP_BoxDiff;
		}

		/**
		 * A simple function, that selects the current LUT-set based on the
		 * player to move and the current box-difference. The coefficients are
		 * defined, when an instance of this class is generated. <br>
		 * Example 1: If we want to select the LUTs simply based on the player
		 * to move (A, B), we have to define the coefficients in the following
		 * way: <br>
		 * <code> &nbsp;&nbsp; offset = 0.5f <br>&nbsp;&nbsp; aP = -0.5f 
		 * <br>&nbsp;&nbsp; aBoxDiff = 0.0f <br>&nbsp;&nbsp; aP_BoxDiff = 0.0f </code>
		 * <br>
		 * <br>
		 * Example 2: If we want to select the LUT-set depending on the player
		 * to move (A, B) and the box-difference for a 2x2 board (-3 to +3,
		 * since positions with +-4 are terminal positions and therefore not
		 * learnt) we can define our function in the following way:
		 * <code> &nbsp;&nbsp; offset = 6.5f <br>&nbsp;&nbsp; aP = -3.5f 
		 * <br>&nbsp;&nbsp; aBoxDiff = 1.0f <br>&nbsp;&nbsp; aP_BoxDiff = 0.0f </code>
		 * 
		 * @param s
		 *            The current game-state as a snapshot
		 * @return Selected LUT
		 */
		public int selectLUTset(GameStateSnapshot s) {
			int p = s.getPlayerToMove();
			int b = s.getBoxDifference();
			int y = Math.round(offset + aP * p + aBoxDiff * b + aP_BoxDiff * p * b);
			return y;
		}
	}

	@XmlElement
	public int numLUTSets = 1;

	@XmlElement
	public LUTSelection lutSelection;

	@XmlElement
	public NTuplePoint[][] nTupleMasks;
}
