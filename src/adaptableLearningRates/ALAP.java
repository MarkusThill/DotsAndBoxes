package adaptableLearningRates;

import java.io.Serializable;

import nTupleTD.UpdateParams;
import nTupleTD.WeightUpdateParams;

public class ALAP extends LearningRates implements Serializable {
	private static final long serialVersionUID = -2979682460004097012L;

	private final ALAPParams alapPar;

	/**
	 * The gradient of the loss function for the weights w_ij from the previous
	 * iteration
	 */
	private float[/* lut-subset */][/* LUT */][/* i */] lastGradient;

	/**
	 * Vector-elements for the element-wise normalization of the meta-descent
	 * update
	 */
	private float[/* lut-subset */][/* LUT */][/* i */] v;

	private float[/* lut-subset */][/* LUT */][/* i */] alpha;

	ALAP(float[][][] w, ALAPParams alapPar) {
		super(w);
		this.alapPar = alapPar;
		initTables(w, alapPar);
	}

	private final void initTables(float[][][] w, ALAPParams alapPar) {
		// The 3-dim arrays do not necessarily have to be cubes..
		v = new float[w.length][][];
		alpha = new float[w.length][][];
		lastGradient = new float[w.length][][];

		for (int i = 0; i < w.length; i++) {
			v[i] = new float[w[i].length][];
			alpha[i] = new float[w[i].length][];
			lastGradient[i] = new float[w[i].length][];

			for (int j = 0; j < w[i].length; j++) {
				v[i][j] = new float[w[i][j].length];
				alpha[i][j] = new float[w[i][j].length];
				lastGradient[i][j] = new float[w[i][j].length];

				//
				// alpha has to be initialized
				//
				for (int k = 0; k < alpha[i][j].length; k++)
					alpha[i][j][k] = (float) alapPar.alphaInit;
			}
		}
	}

	@Override
	public void preWeightUpdate(WeightUpdateParams u_i) {
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		float[] v = this.v[s][l];
		float[] lastGrad = this.lastGradient[s][l];
		float[] alpha = this.alpha[s][l];
		double gamma = alapPar.gamma;
		double theta = alapPar.theta;
		double x_i = u_i.x_i;
		//
		// Calculate new v_i
		//
		double grad = u_i.derivYWeightLoss * u_i.delta * x_i;
		double vNew = (1 - gamma) * v[i] + gamma * (grad * grad);
		v[i] = (float) vNew;

		//
		// Calculate new alpha_i
		//
		if (v[i] != 0.0) {
			double a = alpha[i] * (1.0 + theta * grad * lastGrad[i] / v[i]);
			alpha[i] = (float) a;
		}

		//
		// Check, if alpha_i is smaller than the lower-bound. Set new value in
		// case.
		//
		if (alpha[i] < ALAPParams.LOWERBOUND_ALPHA)
			alpha[i] = (float) ALAPParams.LOWERBOUND_ALPHA;

		//
		// Save last gradient
		//
		lastGrad[i] = (float) grad;
	}

	@Override
	public double getLearningRate(WeightUpdateParams u_i) {
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		return alpha[s][l][i];
	}

	@Override
	public void postWeightUpdate(WeightUpdateParams u_i) {
		// Nothing to do
	}

	@Override
	public void preUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes) {
		// Nothing to do
	}

	@Override
	public void postUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes) {
		// Nothing to do
	}

}
