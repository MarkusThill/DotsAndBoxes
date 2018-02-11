package nTupleTD;

import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

public class EligibilityTraces {
	private TreeMap<Integer, Double> eligTraces;

	public EligibilityTraces() {
		eligTraces = new TreeMap<>();
	}

	public void reset() {
		eligTraces.clear();
	}

	public void scaleTraces(double lambda, double gamma) {
		// Multiply all elements of the elig-vector with lambda * gamma
		Set<Entry<Integer, Double>> c = eligTraces.entrySet();
		// for (Iterator<Entry<Integer, Double>> i = c.iterator(); i.hasNext();)
		// {
		// Entry<Integer, Double> entry = i.next();
		// double e_i = entry.getValue() * lambda * gamma;
		// entry.setValue(e_i);
		// }

		for (Entry<Integer, Double> entry_i : c) {
			double e_i = entry_i.getValue() * lambda * gamma;
			entry_i.setValue(e_i);
		}
	}

	public void addGradient(int i, double grad, boolean replace) {
		Object entry = eligTraces.get(i);
		double value = (Double) (entry == null || replace ? 0.0 : entry);
		eligTraces.put(i, value + grad);
	}

	public double getTrace(int i) {
		Double value = eligTraces.get(i);
		return (value == null ? 0.0 : value);
	}
	
	public Set<Entry<Integer, Double>> getTraces() {
		return eligTraces.entrySet();
	}
}
