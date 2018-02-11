package nTupleTD;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Random;

import nTupleTD.NTupleParams.LUTSelection;
import adaptableLearningRates.LearningRates;
import dotsAndBoxes.GameStateSnapshot;

public class NTupleNet implements Serializable {
	private static final long serialVersionUID = -8612799623235025701L;

	private final NTupleParams nPar;

	/**
	 * The set of n-tuples
	 * */
	private final NTuples nTuples;

	/**
	 * Extended weights set. Based on the box-difference and the player, use a
	 * different set of weights extW[g(boxDiff,p)]. Define g(boxDiff,p) in a way
	 * that it returns values between 0 and 2xmxn. 0 means boxDiff has no effect
	 * all, 2 x m x n creates a weight-set for every possible boxdifference (-m
	 * x n .. + m x n)
	 */
	private final float[/* subset */][/* LUT */][/* weights */] extW;

	/**
	 * Learning rates
	 */
	private LearningRates lr;

	/**
	 * Eligibility traces. Implement as Tree-map, since the trace vector is very
	 * sparse. Maybe define a new class for the traces.
	 */
	private final EligibilityTraces[/* LUT-subset */][/* LUT */] e;

	/**
	 * 
	 * @param par
	 */
	public NTupleNet(TrainingParams par) {
		nPar = par.nPar;

		nTuples = new NTuples(par.nPar, par.m, par.n);

		// init Weights
		extW = initWeights(nTuples, par.tdPar, par.nPar);

		// init learning rates
		lr = LearningRates.initLearningRates(par.lrPar, extW);

		if (par.tdPar.lambda != 0.0) {
			e = new EligibilityTraces[nPar.numLUTSets][nPar.tupleNum];
			for (EligibilityTraces[] e_l : e) {
				for (int i = 0; i < e_l.length; i++)
					e_l[i] = new EligibilityTraces();
			}
		} else
			e = null;
	}

	/**
	 * 
	 * @param nTuples
	 *            Required, to make sure, that nTuples are already initialized.
	 *            For every n-tuple create a lookup table containing the
	 *            corresponding weights of the system
	 */
	private static float[][][] initWeights(NTuples nTuples, TDParams tdPar,
			NTupleParams nPar) {
		final Random rnd = tools.SRandom.RND;
		float w[][][];
		int numLUTSets = nPar.numLUTSets;
		int numTuples = nTuples.getTupleCount();
		w = new float[numLUTSets][numTuples][];
		for (int j = 0; j < numLUTSets; j++)
			for (int i = 0; i < numTuples; i++)
				w[j][i] = new float[nTuples.getStateCount(i)];
		if (tdPar.randValueFunctionInit) {
			float eps = tdPar.randValueFunctionEps;
			for (int k = 0; k < numLUTSets; k++)
				for (int i = 0; i < w.length; i++)
					for (int j = 0; j < w[i].length; j++)
						// Random.nextFloat() return values in the range 0.0 to
						// 1.0
						w[k][i][j] = eps * (rnd.nextFloat() * 2.0f - 1);
		}
		return w;
	}

	public double getValue(GameStateSnapshot s) {
		int[][] index = nTuples.getIndexSet(s);
		LUTSelection ls = nPar.lutSelection;
		int lutSubset = (ls == null ? 0 : ls.selectLUTset(s));
		float[][] w = extW[lutSubset];

		assert (index.length == w.length);

		double linVal = 0.0;
		float[] lut;
		int[] lutIndex;
		for (int i = 0; i < index.length; i++) {
			lut = w[i];
			lutIndex = index[i];
			for (int j = 0; j < index[i].length; j++) {
				linVal += lut[lutIndex[j]];
			}
		}
		return linVal;
	}

	public void updateWeights(UpdateParams u) {
		LUTSelection ls = nPar.lutSelection;
		int lutSubset = (ls == null ? 0 : ls.selectLUTset(u.s));
		float[][] w = extW[lutSubset];
		int[][] activeWeightIndexes = nTuples.getIndexSet(u.s);

		// Prepare learning rates for all weights
		lr.preUpdate(u, lutSubset, activeWeightIndexes);
		if (e != null)
			updateWithEligibilityTraces(u, lutSubset, w);
		else
			updateWithoutEligibilityTraces(u, lutSubset, w, activeWeightIndexes);

		// After all weights were updated, do the post-processing of the
		// learning rates (for all weights)
		lr.postUpdate(u, lutSubset, activeWeightIndexes);
	}

	private void updateWeight(float[] lut, WeightUpdateParams u_i) {
		// Prepare learning-rate i
		lr.preWeightUpdate(u_i);

		// Update weight i
		double dW = lr.getWeightChange(u_i);
		lut[u_i.i] += dW;

		// work on learning rate i after weight-update
		lr.postWeightUpdate(u_i);
	}

	private void updateWithoutEligibilityTraces(UpdateParams u,
			int lutSubsetIndex, float[][] lutSubset, int[][] activeWeightIndexes) {
		// Iterate through all affected weights and adjust them
		// int[][] activeWeightIndexes = nTuples.getIndexSet(u.s);
		float[] lut;
		int[] lutIndex;
		// Create an update-params object for weights
		WeightUpdateParams u_i = new WeightUpdateParams(u);
		for (int i = 0; i < activeWeightIndexes.length; i++) { // Iterate
																// through LUTs
			lut = lutSubset[i];
			lutIndex = activeWeightIndexes[i];
			// Iterate through active weights of the LUT
			for (int j = 0; j < activeWeightIndexes[i].length; j++) {
				u_i.sub = lutSubsetIndex;
				u_i.lut = i;
				u_i.i = lutIndex[j];
				// We only allow every index once, even if mirrored positions
				// lead to duplicate indexes. Therefore x_i is always x_i=1
				u_i.x_i = 1;
				u_i.e_i = u_i.x_i * u_i.derivYWeightLoss;
				u_i.w_i = lut[u_i.i];
				updateWeight(lut, u_i);
			}
		}
	}

	private void updateWithEligibilityTraces(UpdateParams u,
			int lutSubsetIndex, float[][] lutSubset) {
		WeightUpdateParams u_i = new WeightUpdateParams(u);
		float[] lut;
		//
		// Iterate through all LUTs
		//
		for (int l = 0; l < lutSubset.length; l++) {
			EligibilityTraces e_lut = e[lutSubsetIndex][l];
			lut = lutSubset[l];
			//
			// Iterate through all active eligibility traces of the
			// corresponding LUT l
			//
			for (Entry<Integer, Double> e_i : e_lut.getTraces()) {
				u_i.sub = lutSubsetIndex;
				u_i.lut = l;

				u_i.i = e_i.getKey();
				u_i.e_i = e_i.getValue();
				//
				// We only allow every index once, even if mirrored positions
				// lead to duplicate indexes. Therefore x_i is always x_i=1
				//
				u_i.x_i = 1;
				u_i.w_i = lut[u_i.i];
				updateWeight(lut, u_i);
			}
		}

	}

	public void resetEligibilityTraces() {
		for (EligibilityTraces e_sub[] : e)
			for (EligibilityTraces e_lut : e_sub)
				e_lut.reset();
	}

	private void scaleEligibilityTraces(double lambda, double gamma) {
		for (EligibilityTraces e_sub[] : e)
			for (EligibilityTraces e_lut : e_sub)
				e_lut.scaleTraces(lambda, gamma);
	}

	public void updateEligibilityTraces(GameStateSnapshot s, double lambda,
			double gamma, double grad, boolean replace) {
		// First scale the current elig-vector by lambda*gamma
		// e <- e * lambda * gamma
		// The scaling has to be done for all LUTs (for both players). All
		// elements <> 0 in the elig-vector are therefore scaled.
		scaleEligibilityTraces(lambda, gamma);

		//
		// Now get the gradient and add it to the eligibility-trace vector. The
		// gradient has the same value for all activated weights
		//
		addGradientToEligibilityTraces(s, grad, replace);

	}

	public void addGradientToEligibilityTraces(GameStateSnapshot s,
			double grad, boolean replace) {
		LUTSelection ls = nPar.lutSelection;
		int[][] index = nTuples.getIndexSet(s);
		int lutSubset = (ls == null ? 0 : ls.selectLUTset(s));

		EligibilityTraces[] elig = e[lutSubset];
		for (int i = 0; i < index.length; i++)
			for (int j = 0; j < index[i].length; j++) {
				elig[i].addGradient(index[i][j], grad, replace);
			}
	}
}
