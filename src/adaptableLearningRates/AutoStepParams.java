package adaptableLearningRates;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AutoStepParams  extends LearningRateParams implements Serializable{
	private static final long serialVersionUID = 2352461847876498123L;
	
	@XmlElement
	public double alphaInit = 0.001;
	
	@XmlElement
	public double mu = 1e-3;
	
	@XmlElement
	public double tau = 1e4;
	
	public AutoStepParams() {
		learningRateMeth = LearningRateMethod.AUTOSTEP;
	}
}
