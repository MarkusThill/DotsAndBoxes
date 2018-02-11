package adaptableLearningRates;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class LearningRateParams {
	@XmlRootElement
	public enum LearningRateMethod {
		TDL(0), IDBD(1), TCL(2), RPROP(3), AUTOSTEP(4), ALAP(5);

		private final int value;

		LearningRateMethod(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	};

	@XmlElement
	protected LearningRateMethod learningRateMeth;
}
