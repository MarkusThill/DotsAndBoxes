package adaptableLearningRates;

import java.io.Serializable;

import nTupleTD.UpdateParams;
import nTupleTD.WeightUpdateParams;

public class TCL extends LearningRates implements Serializable {
	private static final long serialVersionUID = -6330524732777950185L;
	private final TCLParams tclPar;

	private float tcN[/* lut-subset */][/* LUT */][/* i */] = null;
	private float tcA[/* lut-subset */][/* LUT */][/* i */] = null;

	public TCL(float[][][] w, TCLParams tclPar) {
		super(w);
		this.tclPar = tclPar;
		initTables(w, tclPar);
	}

	private void initTables(float[][][] w, TCLParams tclPar) {
		// The 3-dim arrays do not necessarily have to be cubes..
		tcN = new float[w.length][][];
		tcA = new float[w.length][][];

		for (int i = 0; i < w.length; i++) {
			tcN[i] = new float[w[i].length][];
			tcA[i] = new float[w[i].length][];
			for (int j = 0; j < w[i].length; j++) {
				tcN[i][j] = new float[w[i][j].length];
				tcA[i][j] = new float[w[i][j].length];
			}
		}
		// Both lists don't have to be initialized. They are initialized by zero
		// already.
	}

	private void updateTables(WeightUpdateParams u_i) {
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		float A[] = tcA[s][l];
		float N[] = tcN[s][l];

		//
		// Recommended Weight Change or delta?
		//
		final boolean useRWC = tclPar.useRWC;
		final double updateVal = (useRWC ? u_i.delta * u_i.e_i : u_i.delta);

		// update tables
		N[i] = (float) (N[i] + updateVal);
		A[i] = (float) (A[i] + Math.abs(updateVal));
	}

	@Override
	public void preWeightUpdate(WeightUpdateParams u_i) {
		boolean up = tclPar.updateLUTBeforeStepSize;
		if (!up)
			updateTables(u_i);
	}

	@Override
	public double getLearningRate(WeightUpdateParams u_i) {
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		float A[] = tcA[s][l];
		float N[] = tcN[s][l];
		double tc = (A[i] == 0 ? 1.0 : Math.abs(N[i]) / A[i]);

		// When choosing exp. scheme of tcFactor
		if (tclPar.useExpScheme) {
			double fac = tclPar.expSchemeFactor;
			tc = Math.exp(fac * (tc - 1.0));
		}
		return tc * u_i.globalAlpha;
	}

	@Override
	public void postWeightUpdate(WeightUpdateParams u_i) {
		boolean up = tclPar.updateLUTBeforeStepSize;
		if (up)
			updateTables(u_i);
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
