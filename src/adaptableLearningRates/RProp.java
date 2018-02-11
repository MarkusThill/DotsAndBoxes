package adaptableLearningRates;

import java.io.Serializable;
import nTupleTD.UpdateParams;
import nTupleTD.WeightUpdateParams;

public class RProp extends LearningRates implements Serializable {
	private static final long serialVersionUID = -1807985735165740041L;

	private final RPropParams rPar;

	private float[/* lut-subset */][/* LUT */][/* i */] lastWeightChange;

	/**
	 * The learning step sizes (\Delta_ij) for the weights w_ij from the
	 * previous iteration
	 */
	private float[/* lut-subset */][/* LUT */][/* i */] lastDelta;

	/**
	 * The gradient of the loss function for the weights w_ij from the previous
	 * iteration
	 */
	private float[/* lut-subset */][/* LUT */][/* i */] lastGradient;

	/**
	 * The value error at the beginning of the previous training iteration. This
	 * value is compared with the error at the beginning of the current
	 * iteration to determine if the error has improved.
	 */
	private double lastError = Double.POSITIVE_INFINITY;

	public RProp(float[][][] w, RPropParams rPar) {
		super(w);
		this.rPar = rPar;
		//
		// Initialize the three necessary tables
		//
		initTables(w, rPar);
	}

	private final void initTables(float[][][] w, RPropParams rPar) {
		// The 3-dim arrays do not necessarily have to be cubes..
		lastWeightChange = new float[w.length][][];
		lastDelta = new float[w.length][][];
		lastGradient = new float[w.length][][];

		for (int i = 0; i < w.length; i++) {
			lastWeightChange[i] = new float[w[i].length][];
			lastDelta[i] = new float[w[i].length][];
			lastGradient[i] = new float[w[i].length][];

			for (int j = 0; j < w[i].length; j++) {
				lastWeightChange[i][j] = new float[w[i][j].length];
				lastDelta[i][j] = new float[w[i][j].length];
				lastGradient[i][j] = new float[w[i][j].length];

				//
				// The last delta has to be set to a proper value in the
				// beginning. Zero is not valid
				//
				for (int k = 0; k < lastDelta[i][j].length; k++)
					lastDelta[i][j][k] = (float) rPar.initialDelta;
			}
		}
	}

	private double preWeightUpdateMinus(WeightUpdateParams u_i, double Et) {
		final int s = u_i.sub;
		final int l = u_i.lut;
		final int i = u_i.i;
		final float[] lastGrad = lastGradient[s][l];
		final float[] lastDelta = this.lastDelta[s][l];
		//final float[] lastWeightChange = this.lastWeightChange[s][l];

		double grad_i_Et = -2.0 * u_i.derivYMSELoss * u_i.delta *u_i.x_i;
		final int change = (int) Math.signum(grad_i_Et * lastGrad[i]);
		double weightChange = 0;
		
		double delta = lastDelta[i];		// /WK/ Bug fix: this is for the case change==0
		//grad_i_Et: double gIndex = gradients[index];
		
		if (change > 0) {
			delta = lastDelta[i]* RPropParams.POSITIVE_ETA;
			delta = Math.min(delta, RPropParams.DEFAULT_MAX_STEP);			
		} else if (change < 0) {						// /WK/ Bug fix: exclude here change==0
			// if change<0, then the sign has changed, and the last
			// delta was too big
			delta = lastDelta[i] * RPropParams.NEGATIVE_ETA;
			delta = Math.max(delta, RPropParams.DELTA_MIN);
			if (rPar.improved) grad_i_Et = 0.0;				// /WK/ changed w.r.t. Encog !
			// this has two effects: (1) no weight change in the current iteration AND
			// (2) weight change with this delta in the next iteration (where lastGradient[index]=0)
		}
		//
		// in case change==0 we do only weightChange, no step size change 
		//
		lastGrad[i] = (float) grad_i_Et; 
		weightChange = -Math.signum(grad_i_Et) * delta;
		lastDelta[i] = (float) delta;
		
		return weightChange;
		
	}

	private double preWeightUpdatePlus(WeightUpdateParams u_i, double Et) {
		final int s = u_i.sub;
		final int l = u_i.lut;
		final int i = u_i.i;
		final float[] lastGrad = lastGradient[s][l];
		final float[] lastDelta = this.lastDelta[s][l];
		final float[] lastWeightChange = this.lastWeightChange[s][l];

		double grad_i_Et = -2.0 * u_i.derivYMSELoss * u_i.delta *u_i.x_i;
		final int change = (int) Math.signum(grad_i_Et * lastGrad[i]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = lastDelta[i] * RPropParams.POSITIVE_ETA;
			delta = Math.min(delta, RPropParams.DEFAULT_MAX_STEP);
			// /WK/ added a "-" in front of Math.signum
			weightChange = -Math.signum(grad_i_Et) * delta;
			lastDelta[i] = (float) delta;
			lastGrad[i] = (float) grad_i_Et;
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = lastDelta[i] * RPropParams.NEGATIVE_ETA;
			delta = Math.max(delta, RPropParams.DELTA_MIN);
			lastDelta[i] = (float) delta;

			if (!rPar.improved || Et > this.lastError) {
				weightChange = -lastWeightChange[i];
			}

			// Set the previous gradient to zero so that there will be no
			// step size adjustment in the next iteration
			lastGrad[i] = 0f;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = lastDelta[i];
			weightChange = -Math.signum(grad_i_Et) * delta;
			lastGrad[i] = (float) grad_i_Et;
		}
		return weightChange;
	}

	@Override
	public void preWeightUpdate(WeightUpdateParams u_i) {
		float lastWeightChange[] = this.lastWeightChange[u_i.sub][u_i.lut];
		double Et = u_i.delta * u_i.delta;
		double weightChange = 0;
		if (rPar.plusVariant)
			weightChange = preWeightUpdatePlus(u_i, Et);
		else
			weightChange = preWeightUpdateMinus(u_i, Et);
		lastError = Et;
		lastWeightChange[u_i.i] = (float) weightChange;
	}

	@Override
	public double getLearningRate(WeightUpdateParams u_i) {
		return lastDelta[u_i.sub][u_i.lut][u_i.i];
	}

	@Override
	public double getWeightChange(WeightUpdateParams u_i) {
		return lastWeightChange[u_i.sub][u_i.lut][u_i.i];
	}

	@Override
	public void postWeightUpdate(WeightUpdateParams u_i) {
	}

	@Override
	public void preUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes) {
		// TODO Auto-generated method stub
		
	}
}
