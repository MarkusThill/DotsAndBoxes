package multiTraining;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExperimentList implements Iterable<Experiment>{
	/**
	 * A list of experiments. Every experiment is represented as a list of
	 * parameters (such as allpha_init, epsilon_final, ...), that will be used
	 * for this experiment.
	 */
	@XmlElement
	private ArrayList<Experiment> experiments;
	
	
	public ExperimentList(ArrayList<Experiment> experiments) {
		this.experiments = experiments;
	}
	
	public ExperimentList() {
		experiments = new ArrayList<>();
	}
	
	public ExperimentList(int initialSize) {
		experiments = new ArrayList<>(initialSize);
	}
	
	public List<Parameter> get(int index) {
		return experiments.get(index).experiment;
	}
	
	public void addExperiment(Experiment experiment)  {
		experiments.add(new Experiment(experiment.get()));
	}
	
	public void addAllExperiments(ExperimentList expList)  {
		experiments.addAll(expList.experiments);
	}
	
	public int size() {
		return experiments.size();
	}
	
	@Override
	public Iterator<Experiment> iterator() {
		return new ExperimentIterator(experiments);
	}
	
	@Override
	public String toString() {
		return experiments.toString();
	}
}
