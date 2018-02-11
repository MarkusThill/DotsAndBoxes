package training;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tools.CSVTools;

import nTupleTD.TrainingParams.InfoMeasures;

public class CSVLogger {
	/**
	 * Maintain a list of {@link PrintStream}s, that are used to print
	 * log-events. Every call of {@link CSVLogger#put(Integer, MeasurePoint)}
	 * will also write a line to the print-streams.
	 */
	private List<PrintStream> printStreams;

	/**
	 * Log, that can take an integer (e.g., the current training-game number)
	 * and the values measured at this point.
	 */
	private List<LoggerLine<Integer, MeasurePoint>> log;

	public CSVLogger() {
		printStreams = new ArrayList<PrintStream>();
		log = new ArrayList<LoggerLine<Integer, MeasurePoint>>(100);
	}

	public void logColumnNames(String keyName,
			TrackedInfoMeasures trackInfoMeasures) {
		Iterator<InfoMeasures> i = trackInfoMeasures.iterator();
		String s = new String();
		//
		// The first element is the key (index) of a logger-line (Typically
		// gameNumber or similar).
		//
		s += keyName + CSVTools.SEPARATOR;

		while (i.hasNext()) {
			s += i.next() + CSVTools.SEPARATOR;
		}
		notifyStreamListeners(s);
	}

	public void put(Integer key, MeasurePoint mp) {
		LoggerLine<Integer, MeasurePoint> x = new LoggerLine<Integer, MeasurePoint>(
				key, mp);
		log.add(x);
		notifyStreamListeners(x);
	}

	public void addStreamListener(PrintStream ps) {
		printStreams.add(ps);
	}

	/**
	 * Make sure, that the object implements the {@link Object#toString()}
	 * method as desired.
	 * 
	 * @param o
	 */
	private void notifyStreamListeners(Object o) {
		// Iterator<PrintStream> i = printStreams.iterator();
		// while (i.hasNext()) {
		// PrintStream ps = i.next();
		for (PrintStream ps : printStreams) {
			ps.println(o);
			ps.flush();
		}
	}
}

class LoggerLine<Key, Line> {
	private Key k;
	private Line l;

	public LoggerLine(Key k, Line l) {
		this.k = k;
		this.l = l;
	}

	public Key getKey() {
		return k;
	}

	public Line getLine() {
		return l;
	}

	public void setKey(Key k) {
		this.k = k;
	}

	public void setLine(Line l) {
		this.l = l;
	}

	@Override
	public String toString() {
		return k.toString() + CSVTools.SEPARATOR + l.toString();
	}
}