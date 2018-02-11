package tools;

import java.io.Serializable;

public class Function implements Serializable {
	private static final long serialVersionUID = -6175601705165865561L;
	private double init_y; 
	private double final_y;
	private double final_x;
	private FunctionScheme a;
	private double coeff[];
	
	public Function(double init_y, double final_y, double final_x, FunctionScheme a, double coeff[]) {
		this.init_y = init_y;
		this.final_y = final_y;
		this.final_x = final_x;
		this.a = a;
		this.coeff = coeff;
	}

	/**
	 * 
	 * @author Markus Thill
	 * 
	 */
	public static enum FunctionScheme {
		NONE, LINEAR, EXPONENTIAL, STEP
	};

	public double f(double x) {
		switch (a) {
		case NONE:
			return init_y;
		case EXPONENTIAL:
			return init_y * Math.pow(final_y / init_y, x / final_x);
		case LINEAR:
			return (final_y - init_y) / final_x * x + init_y;
		case STEP:
			if (coeff.length != 1)
				throw new IllegalArgumentException(
						"There should be only one additional parameter in coeff[]!");
			return (x < coeff[0] ? init_y : final_y);
		default:
			throw new UnsupportedOperationException(
					"Function not supported yet");

		}
	}
}
