package training;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.EnumMap;
import tools.CSVTools;
import nTupleTD.TrainingParams.InfoMeasures;

public class MeasurePoint {
	private static final String FORMATSTRING_DOUBLE = "#0.00000";
	private final EnumMap<InfoMeasures, Object> measurePoint;

	public MeasurePoint() {
		measurePoint = new EnumMap<InfoMeasures, Object>(InfoMeasures.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		//
		// Convert the measured values into a String, using the CSV-format.
		// Values are separated using a predifined separator
		//
		Collection<Object> v = measurePoint.values();
		// Iterator<Object> i = v.iterator();
		String measurePoint = new String();
		NumberFormat formatter;
		// while (i.hasNext()) {
		// Object o = i.next();
		for (Object o : v) {
			if (o instanceof Double) {
				double d = (Double) o;
				formatter = new DecimalFormat(FORMATSTRING_DOUBLE);
				measurePoint += formatter.format(d);
			} else {
				measurePoint += o.toString();
			}
			measurePoint += CSVTools.SEPARATOR;
		}
		return measurePoint;
	}

	public void set(InfoMeasures im, Object value) {
		measurePoint.put(im, value);
	}
}
