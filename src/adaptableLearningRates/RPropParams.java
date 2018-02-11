package adaptableLearningRates;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RPropParams extends LearningRateParams implements Serializable {
	private static final long serialVersionUID = -9212839518298760581L;

	public RPropParams(){
		learningRateMeth = LearningRateMethod.RPROP;
	}
	
	/**
	 * The default zero tolerance.
	 */
	public static final double DEFAULT_ZERO_TOLERANCE = 0.00000000000000001;
	
	/**
	 * The POSITIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is greater than zero.
	 */
	public static final double POSITIVE_ETA = 1.2;//1.2;
	
	/**
	 * The NEGATIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is less than zero.
	 */
	public static final double NEGATIVE_ETA = 0.8;//0.5;
	
	/**
	 * The minimum delta value for a weight matrix value.
	 */
	public static final double DELTA_MIN = 1e-5;
	
	/**
	 * The starting update for a delta.
	 */
	public static final double DEFAULT_INITIAL_UPDATE = 0.001;
	
	/**
	 * The maximum amount a delta can reach.
	 */
	public static final double DEFAULT_MAX_STEP = 50;

	@XmlElement
	public boolean improved = true;

	/**
	 * RProp-Plus (true) or RProp-Minus(false)
	 */
	@XmlElement
	public boolean plusVariant = true;
	
	@XmlElement
	public double initialDelta = DEFAULT_INITIAL_UPDATE;
}
