package nTupleTD;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import tools.Function;
import tools.Function.FunctionScheme;

import adaptableLearningRates.ActivationFunction.Activation;

@XmlRootElement(name = "TDParams")
public class TDParams implements Serializable {
	private static final long serialVersionUID = 753059275011579365L;
	public static final double SCALE_GAME_BY_MILLION = 1e6;
	public static final double SCALE_GAME_BY_THOUSEND = 1e3;

	/**
	 * Discount-factor gamma of the TD-Learning algorithm.
	 */
	@XmlElement
	public double gamma = 1.0;
	
	@XmlElement
	public double lambda = 0.5;
	
	@XmlElement
	public boolean replacingTraces = false;
	
	@XmlElement
	public boolean resettingTraces = false;

	/**
	 * Randomly initialize weights of the system?
	 */
	@XmlElement
	public boolean randValueFunctionInit = false;

	/**
	 * If {@link TDParams#randValueFunctionInit} is true, then all weights will
	 * be initialized with small random values in the given epsilon-range (which
	 * produces negativ and positiv values with absolute values smaller or equal
	 * epsilon.)
	 */
	@XmlElement
	public float randValueFunctionEps = 0.001f;

	/**
	 * Which activation-function is to be used? See {@link ActivationFunction}
	 * for available functions.
	 */
	@XmlElement
	public Activation activation = Activation.NONE;

	/**
	 * initial exploration-rate for the Temporal Difference Learning (TDL)
	 * algorithm.
	 */
	@XmlElement
	public double epsilonInit = 0.1;

	/**
	 * Final exploration-rate for the Temporal Difference Learning (TDL)
	 * algorithm.
	 */
	@XmlElement
	public double epsilonFinal = 0.1;

	/**
	 * Select, how the exploration-rate epsilon shall be adjusted from the
	 * initial to the final value.
	 */
	@XmlElement
	public FunctionScheme epsilonAdjust;

	/**
	 * Additional coefficients for the adjustment of the exploration-rate
	 * epsilon. Depending on the adjustment methods used, several parameters may
	 * be necessary, in order to adjust epsilon with a certain function. For
	 * details refer to the class {@link Function}.
	 */
	@XmlElement
	public double[] epsilonAdjustCoeff;

	/**
	 * Initial value for the global learning rate Alpha
	 */
	@XmlElement
	public double alphaInit = 0.1;

	/**
	 * Final value for the global learning rate Alpha
	 */
	@XmlElement
	public double alphaFinal = 0.1;

	/**
	 * Select, how alpha shall be adjusted from the initial to the final value.
	 */
	@XmlElement
	public FunctionScheme alphaAdjust;

	/**
	 * Additional coefficients for the adjustment of alpha. Depending on the
	 * adjustment methods used, several parameters may be necessary, in order to
	 * adjust alpha with a certain function.
	 */
	@XmlElement
	public double[] alphaAdjustCoeff;

	/**
	 * If true, learn the value-function only for player A. Values for states,
	 * where player B is to move will be determined in the following way: If we
	 * want the value of state, where player B is to move, we have to invert the
	 * board, get the value for the inverted board (we create the corresponding
	 * situation for player A) and invert this value again.
	 */
	@XmlElement
	public boolean useBoardInversion = true;

	/**
	 * Use immediate rewards, if true. This means, that for every move a reward
	 * of -1, 0 or +1 is given, depending on which player captured a box. If no
	 * box is captured, the reward is zero. This approach has the advantage,
	 * that the value-function can learn to predict for a certain game-state how
	 * many box will be captured in future. Using this approach makes it
	 * impossible to use activation functions such as TANH. Make sure, that the
	 * activation is turned off when using
	 * <code> useImmediateRewards = true</code>.
	 */
	@XmlElement
	public boolean useImmediateRewards = true;

}
