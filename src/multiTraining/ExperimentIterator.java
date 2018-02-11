package multiTraining;

import java.util.Iterator;
import java.util.ArrayList;


public class ExperimentIterator implements Iterator<Experiment> {
	private int i;
	private final ArrayList<Experiment> experiments;
	private final int size;

	ExperimentIterator(ArrayList<Experiment> experiments) {
		this.experiments = experiments;
		size = experiments.size();
		i = 0;
	}

	@Override
	public boolean hasNext() {
		if (experiments.size() != experiments.size())
			throw new UnsupportedOperationException(
					"The list was changed inbetween. Cannot iterate through the list further");
		return i < size;
	}

	@Override
	public Experiment next() {
		return experiments.get(i++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove not supported!");
	}
}
