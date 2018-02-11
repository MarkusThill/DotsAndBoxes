package training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nTupleTD.TrainingParams.InfoMeasures;

public class TrackedInfoMeasures implements Serializable {
	private static final long serialVersionUID = -6387020927895291105L;
	/**
	 * List of measures, that we want to track during the training of our agent.
	 */
	public List<InfoMeasures> infoMeasures;
	
	@SuppressWarnings("unused")
	private TrackedInfoMeasures() {
	}

	public TrackedInfoMeasures(InfoMeasures... im) {
		infoMeasures = new ArrayList<>(im.length);
		for (int i = 0; i < im.length; i++) {
			infoMeasures.add(im[i]);
		}
	}

	public Iterator<InfoMeasures> iterator() {
		return infoMeasures.iterator();
	}
}