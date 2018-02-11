package adaptableLearningRates;

import java.io.Serializable;

public class ActivationFunction implements Serializable {
	private static final long serialVersionUID = -6778887424836180606L;

	public enum Activation {
		NONE(0), TANH(1), LOGSIG(2);
		private final int value;
		Activation(int value) {
			this.value = value;
		}
		
		public int getIndex() {
			return value;
		}
	};
	
	public static final double derivateMSELoss(double y, Activation activation) {
		switch (activation) {
		case LOGSIG:
			return y * (1 - y);
		case TANH:
			return (1.0 - y * y);
		case NONE:
			return 1.0;
		default:
			throw new UnsupportedOperationException("Activation-Function not supported");
		}
	}

	public static final double derivateWeightLoss(double y, Activation activation) {
		switch (activation) {
		case TANH:
			return (1.0 - y * y);
		case LOGSIG:
		case NONE:
			// Same, for both linear and log. sigmoid function
			return 1.0;
		default:
			throw new UnsupportedOperationException("Activation-Function not supported");
		}
	}

	/**
	 * 
	 * @param r Assume reward r in the range of [-1,+1]
	 * @param activation Activation-function 
	 * @return
	 */
	public static final double scaleReward(double r, Activation activation) {
		switch (activation) {
		case LOGSIG:
			return (r + 1.0) / 2.0;
		case NONE:
		case TANH:
			return r;
		default:
			throw new UnsupportedOperationException("Activation-Function not supported");
		
		}
	}
	
	public static final double activation(double y_lin, Activation activation) {
		switch (activation) {
		case LOGSIG:
			return 1.0 / (1.0 + Math.exp(-y_lin));
		case TANH:
			return Math.tanh(y_lin);
		case NONE:
			return y_lin;
		default:
			throw new UnsupportedOperationException("Activation-Function not supported");
		}
	}
}
