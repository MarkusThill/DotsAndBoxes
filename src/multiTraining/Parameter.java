package multiTraining;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nTupleTD.TrainingParams;

@XmlRootElement
public class Parameter {
	/**
	 * Name of the sub-object of a {@link TrainingParams} instance. This could
	 * be "nPar" representing the parameters of the n-tuple system or "tdPar"
	 * representing the name of the TD-Parameters, or "lrPar", representing the
	 * learning rate parameters.
	 */
	@XmlElement
	public String subObject;
	
	@XmlElement
	public String parameterName;
	
	@XmlElement
	public Object value;
	
	public Parameter() {
	}

	public Parameter(String subObject, String parameterName, Object value) {
		this.subObject = subObject;
		this.parameterName = parameterName;
		this.value = value;
	}

	@Override
	public String toString() {
		String s = "(";
		s += parameterName + ", " + value + ")";
		return s;
	}
}
