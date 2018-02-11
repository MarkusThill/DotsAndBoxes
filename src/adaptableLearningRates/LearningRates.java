package adaptableLearningRates;

import java.io.Serializable;

import nTupleTD.UpdateParams;
import nTupleTD.WeightUpdateParams;
import nTupleTD.NTupleNet;

public abstract class LearningRates implements Serializable {
	private static final long serialVersionUID = -3515324373675817221L;
	/**
	 * Weights of the System. Passed from the class {@link NTupleNet}. Even
	 * though it is possible to change the weights, it should generally not be
	 * done here. The weights are provided for read-only purposes.
	 */
	protected final float[][][] w;

	LearningRates(float[][][] w) {
		this.w = w;
	}

	public abstract void preWeightUpdate(WeightUpdateParams u_i);

	public abstract double getLearningRate(WeightUpdateParams u_i);

	/**
	 * Since this method is typically the same for all algorithms, it is already
	 * implemented in this class.
	 * 
	 * @param u_i
	 * @return
	 */
	public double getWeightChange(WeightUpdateParams u_i) {
		double stepSize = getLearningRate(u_i);
		double deltaW = stepSize * u_i.delta * u_i.e_i;
		return deltaW;
	}

	public abstract void postWeightUpdate(WeightUpdateParams u_i);

	public abstract void preUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes);

	public abstract void postUpdate(UpdateParams u, int lutSubset,
			int[][] activeWeightIndexes);

	/**
	 * @param tdPar
	 *            TD-Parameters Object
	 * @param w
	 *            Weights of the network. Some learning rate algorithms need
	 *            access to the weights. However, learning rate algorithms do
	 *            not adjust weights.
	 * @return A Learning-Rates object, depending on which learning rate
	 *         adaptation technique was selected in the TD-Parameters object
	 */

	public static final LearningRates initLearningRates(
			LearningRateParams lrPar, float w[][][]) {
		switch (lrPar.learningRateMeth) {
		case TDL:
			return new StandardTDL(w);
		case IDBD:
			return new IDBD(w, (IDBDParams) lrPar);
		case TCL:
			return new TCL(w, (TCLParams) lrPar);
		case RPROP:
			return new RProp(w, (RPropParams) lrPar);
		case AUTOSTEP:
			return new AutoStep(w, (AutoStepParams) lrPar);
		case ALAP:
			return new ALAP(w, (ALAPParams) lrPar);
		default:
			throw new UnsupportedOperationException(
					"Learning Rate Adaptation method not supported yet");
		}
	}

}
