package adaptableLearningRates;

import java.io.Serializable;

import tools.CompareDouble;

import nTupleTD.UpdateParams;
import nTupleTD.WeightUpdateParams;

/**
 * Implementation of Suttons linear IDBD and Koop's non-linear IDBD for a
 * logistic activation function.
 * 
 * @author Markus Thill
 * 
 */
public class IDBD extends LearningRates implements Serializable {
	private static final long serialVersionUID = 6125755547786557357L;
	// Incremental Delta-Bar-Delta Variables
	// TODO: comment
	private final IDBDParams idbdPar;
	private float[/* lut-subset */][/* LUT */][/* i */] idbd_h;
	private float[/* lut-subset */][/* LUT */][/* i */] idbd_beta;
	
	private double sumActiveH = 0.0;

	public IDBD(float[][][] w, IDBDParams idbdPar) {
		super(w);
		this.idbdPar = idbdPar;
		//
		// Initialize learning rates
		//
		initTables(w, idbdPar);
	}

	private void initTables(float[][][] w, IDBDParams idbdPar) {
		// The 3-dim arrays do not necessarily have to be cubes..
		idbd_h = new float[w.length][][];
		idbd_beta = new float[w.length][][];
		
		for(int i = 0;i<w.length;i++) {
			idbd_h[i] = new float[w[i].length][];
			idbd_beta[i] = new float[w[i].length][];
			for(int j=0;j<w[i].length;j++) {
				idbd_h[i][j] = new float[w[i][j].length];
				idbd_beta[i][j] = new float[w[i][j].length];
				//
				// idbd_beta has to be initialized
				//
				for (int k = 0; k < idbd_beta[i][j].length; k++)
					idbd_beta[i][j][k] = (float) idbdPar.beta_init;
			}
		}		
	}

	@Override
	public void preWeightUpdate(WeightUpdateParams u_i) {
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		double theta = idbdPar.theta;
		float[] beta = idbd_beta[s][l];
		float[] h = idbd_h[s][l];

		double deltaBeta = theta * u_i.delta * u_i.e_i * h[i];

		if (Math.abs(deltaBeta) > IDBDParams.IDBD_BETA_CHANGE_MAX)
			deltaBeta = (deltaBeta < 0 ? -1 : +1)
					* IDBDParams.IDBD_BETA_CHANGE_MAX;
		beta[i] += deltaBeta;

		// bound beta
		if (beta[i] < IDBDParams.BETA_LOWER_BOUND)
			beta[i] = (float) IDBDParams.BETA_LOWER_BOUND;
	}

	@Override
	public double getLearningRate(WeightUpdateParams u_i) {
		float beta[] = idbd_beta[u_i.sub][u_i.lut];
		return Math.exp(beta[u_i.i]);
	}

	@Override
	public void postWeightUpdate(WeightUpdateParams u_i) {
		if(IDBDParams.USE_INSTANIOUS_HESSIAN) {
			updateHinstantaniousHessian(u_i);
			return;
		}
		
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		double x_i = u_i.x_i;
		double e_i = u_i.e_i;
		double alpha = getLearningRate(u_i);
		float[] h = idbd_h[s][l];
		
		// Koops non-linear version needs *y*(1-y) in grad
		double x_plus = 1.0 - alpha * x_i * e_i * u_i.derivYMSELoss;
		if (x_plus < 0)
			x_plus = 0.0;
		h[i] = (float) (h[i] * x_plus + alpha * u_i.delta * e_i);
	}
	
	public void updateHinstantaniousHessian(WeightUpdateParams u_i) {
		int s = u_i.sub;
		int l = u_i.lut;
		int i = u_i.i;
		double x_i = u_i.x_i;
		double alpha = getLearningRate(u_i);
		float[] h = idbd_h[s][l];
		
		
		h[i] = (float) (h[i] + alpha * (u_i.delta - sumActiveH));
	}

	@Override
	public void preUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes) {
		
		if(IDBDParams.USE_INSTANIOUS_HESSIAN) {
			double H=0;
			for (int k = 0; k < activeWeightIndexes.length; k++) {
				float[] h = this.idbd_h[lutSubset][k];
				for (int j = 0; j < activeWeightIndexes[k].length; j++) {
					int i = activeWeightIndexes[k][j];
					H += h[i];
				}
			}
			sumActiveH = H;
		}
	}

	@Override
	public void postUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes) {
		// TODO Auto-generated method stub
		
	}

}
