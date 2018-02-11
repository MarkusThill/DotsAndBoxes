package adaptableLearningRates;

import java.io.Serializable;

import nTupleTD.UpdateParams;
import nTupleTD.WeightUpdateParams;

public class StandardTDL extends LearningRates implements Serializable {
	private static final long serialVersionUID = 7584275387019736072L;

	StandardTDL(float[][][] w) {
		super(w);
	}

	@Override
	public void preWeightUpdate(WeightUpdateParams u_i) {
	}

	@Override
	public double getLearningRate(WeightUpdateParams u_i) {
		return u_i.globalAlpha;
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
