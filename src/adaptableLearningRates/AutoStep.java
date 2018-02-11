package adaptableLearningRates;

import java.io.Serializable;
import tools.CompareDouble;
import nTupleTD.UpdateParams;
import nTupleTD.WeightUpdateParams;

public class AutoStep extends LearningRates implements Serializable {
	private static final long serialVersionUID = 2688579852814215918L;

	private final AutoStepParams asPar;
	private float[/* lut-subset */][/* LUT */][/* i */] alpha;
	private float[/* lut-subset */][/* LUT */][/* i */] h;
	private float[/* lut-subset */][/* LUT */][/* i */] v;
	// private double mu;
	// private double tau = (float) 1e4;
	private double M = Double.NaN;

	public AutoStep(float[][][] w, AutoStepParams asPar) {
		super(w);
		this.asPar = asPar;
		createTables(w);
	}

	private void createTables(float[][][] w) {
		alpha = new float[w.length][][];
		h = new float[w.length][][];
		v = new float[w.length][][];

		for (int i = 0; i < w.length; i++) {
			alpha[i] = new float[w[i].length][];
			h[i] = new float[w[i].length][];
			v[i] = new float[w[i].length][];
			for (int j = 0; j < w[i].length; j++) {
				alpha[i][j] = new float[w[i][j].length];
				h[i][j] = new float[w[i][j].length];
				v[i][j] = new float[w[i][j].length];

				//
				// alpha has to be initialized
				//
				for (int k = 0; k < alpha[i][j].length; k++)
					alpha[i][j][k] = (float) asPar.alphaInit;
			}
		}

	}

	@Override
	public void preWeightUpdate(WeightUpdateParams u_i) {
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		double alpha_i = alpha[s][l][i];
		alpha[s][l][i] = (float) (alpha_i / M);
	}

	@Override
	public double getLearningRate(WeightUpdateParams u_i) {
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		double alpha_i = alpha[s][l][i];
		return alpha_i;
	}

	@Override
	public void postWeightUpdate(WeightUpdateParams u_i) {
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		float[] h = this.h[s][l];
		float[] alpha = this.alpha[s][l];
		double x_i = u_i.x_i;
		double rwc = getWeightChange(u_i);
		double xPlus = 1.0 - alpha[i] * x_i * x_i;
		if (xPlus < 0.0)
			xPlus = 0.0;
		double h_i = h[i] * xPlus + rwc;
		h[i] = (float) h_i;
	}

	@Override
	public void preUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes) {
		// We have to prepare the learning rates alpha for each active weight
		// and the corresponding values v_i.
		double x_i = 1; // TODO: Will this stay like this? Probably...
		double tau = asPar.tau;
		double mu = asPar.mu;
		//
		// Reset M first...
		//
		M = 0.0;
		for (int k = 0; k < activeWeightIndexes.length; k++) {
			float[] v = this.v[lutSubset][k];
			float[] h = this.h[lutSubset][k];
			float[] alpha = this.alpha[lutSubset][k];
			double v_i1, v_i2;
			for (int j = 0; j < activeWeightIndexes[k].length; j++) {
				int i = activeWeightIndexes[k][j];
				v_i1 = Math.abs(u.delta * x_i * h[i]);
				v_i2 = v[i] + 1.0 / tau * alpha[i] * x_i * x_i * (v_i1 - v[i]);
				v[i] = (float) Math.max(v_i1, v_i2);
				if (!CompareDouble.equals(v[i], 0.0, 0.00001)) {
					double expo = mu * u.delta * x_i * h[i] / v[i];
					alpha[i] = (float) (alpha[i] * Math.exp(expo));
				}
				M += (alpha[i] * x_i * x_i);
			}
		}

		M = Math.max(M, 1.0);
	}

	@Override
	public void postUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes) {
	}
}
