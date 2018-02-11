package training;

import java.io.Serializable;

import nTupleTD.TDParams;

/**
 * Defines an interval, which is divided into further sub-intervals of same
 * size. This class can be used for instance, if the strength of an agent should
 * be measured every n training-games or if certain information about the agent
 * should be printed in intervals of certain length.
 * 
 * @author Markus Thill
 * 
 */
public class InfoInterval implements Serializable, Cloneable {
	private static final long serialVersionUID = 5376035203366372856L;
	public int startInterval;
	public int stepBy;
	public int endInterval;
	
	public InfoInterval() {
	}

	/**
	 * Define a new Interval, by giving a start and an end-point. The sub-intervals are defined by a step-size. Every time a counter reaches the value of startInterval the method 
	 * @param startInterval
	 * @param stepBy
	 * @param endInterval
	 */
	public InfoInterval(int startInterval, int stepBy, int endInterval) {
		this.startInterval = startInterval;
		this.stepBy = stepBy;
		this.endInterval = endInterval;
	}

	public String toString() {
		return "[" + startInterval / TDParams.SCALE_GAME_BY_MILLION + " ; "
				+ endInterval / TDParams.SCALE_GAME_BY_MILLION + "]" + " by="
				+ stepBy / TDParams.SCALE_GAME_BY_THOUSEND;
	}

	public boolean infoNecessary(int gameNr) {
		if (gameNr >= startInterval && gameNr <= endInterval)
			return (gameNr - startInterval) % stepBy == 0;
		return false;
	}

	public static boolean infoNecessary(InfoInterval[] iList, int gameNr) {
		for (InfoInterval i : iList) {
			if (i.infoNecessary(gameNr))
				return true;
		}
		return false;
	}
}