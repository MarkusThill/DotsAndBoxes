package adaptableLearningRates;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ALAPParams  extends LearningRateParams implements Serializable  {

	private static final long serialVersionUID = 4990137594968176397L;
	
	public static final double LOWERBOUND_ALPHA = 1e-6;
	
	@XmlElement
	public double gamma;
	
	@XmlElement
	public double theta;
	
	@XmlElement
	public double alphaInit;
	
	public ALAPParams() {
		learningRateMeth = LearningRateMethod.ALAP;
	}
}
