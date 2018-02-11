package adaptableLearningRates;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IDBDParams extends LearningRateParams implements Serializable {
	private static final long serialVersionUID = 8953718548000867468L;
	public static final double BETA_LOWER_BOUND = -10.0;
	public static final double BETA_UPPER_BOUND = -1;
	public static final double IDBD_BETA_CHANGE_MAX = 2;
	
	public static final boolean USE_INSTANIOUS_HESSIAN = false; //TODO: Turn off.....
	
	public IDBDParams() {
		learningRateMeth = LearningRateMethod.IDBD;
	}
	
	@XmlElement
	public double beta_init = -5.8;
	
	@XmlElement
	public double theta = 1.0;

}
