package adaptableLearningRates;

import java.io.Serializable;

public class TCLParams extends LearningRateParams implements Serializable  {
	private static final long serialVersionUID = -6173321663611369953L;
	public boolean updateLUTBeforeStepSize = true;
	
	
	/**
	 * Use recommended weight change to update the tables A and N? If not, the
	 * error signal delta will be used.
	 */
	public boolean useRWC = true;
	public boolean useExpScheme = true;
	public double expSchemeFactor = 2.7;
	
	public  TCLParams() {
		learningRateMeth = LearningRateMethod.TCL;
	}
}
