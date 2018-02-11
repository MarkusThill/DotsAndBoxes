package multiTraining;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Experiment{
	
	@XmlElement
	ArrayList<Parameter> experiment;
	
	public Experiment() {
	}
	
	public Experiment(ArrayList<Parameter> parameterSet) {
		experiment = parameterSet;
	}
	
	public ArrayList<Parameter> get() {
		return experiment;
	}
}