package nTupleTD;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import adaptableLearningRates.ALAPParams;
import adaptableLearningRates.AutoStep;
import adaptableLearningRates.AutoStepParams;
import adaptableLearningRates.IDBD;
import adaptableLearningRates.IDBDParams;
import adaptableLearningRates.LearningRateParams;
import adaptableLearningRates.RProp;
import adaptableLearningRates.RPropParams;
import adaptableLearningRates.StandardTDLParams;
import adaptableLearningRates.TCL;
import adaptableLearningRates.TCLParams;

import training.InfoInterval;
import training.TrackedInfoMeasures;

@XmlRootElement(name = "TrainingParams")
@XmlSeeAlso({ IDBD.class, RProp.class, AutoStep.class, TCL.class })
public class TrainingParams implements Serializable {
	private static final long serialVersionUID = -7566641642457887337L;

	// TODO: Especially important for all learning-rate classes such as IDBD,
	// TDL and
	// so on. Since e.g. IDBDParams is a sub-class of LearningRateParams, the
	// XML marshaller has to save the IDBD-params. This can only be done, if the
	// involved classes are known.
	// For details check: http://stackoverflow.com/questions/3155230/
	public static final Class<?>[] INVOLVED_CLASSES = new Class[] {
			TrainingParams.class, IDBDParams.class, StandardTDLParams.class,
			LearningRateParams.class, AutoStepParams.class, TCLParams.class, RPropParams.class, ALAPParams.class };

	/**
	 * The total number of training-games to be performed.
	 */
	@XmlElement
	public int numGames;

	/**
	 * Number of boxes in dimension X for the board.
	 */
	@XmlElement
	public int m;

	/**
	 * Number of boxes in dimension Y for the board.
	 */
	@XmlElement
	public int n;

	/**
	 * Define, which players the TD-agent should play during the evaluation.
	 * There are three possibilities: A, B or both. If both is selected, then
	 * the number of evaluation matches is autimatically doubled. If the
	 * reference-Agent is a perfect player, then it can make sense in certain
	 * cases to play only one side, since the other side may always lead to a
	 * loss.
	 * 
	 * @author Markus Thill
	 * 
	 */
	@XmlRootElement
	public static enum EvaluationPlayAs {
		PLAY_AS_A, // Agent plays as A during the evaluation
		PLAY_AS_B, // Agent plays as B during the evaluation
		PLAY_BOTH // Agent plays both, A and B, during the evaluation. The
					// number of evaluation matches is then twice as much as in
					// the other two cases.
	};

	/**
	 * For the evaluation of the agent, which assesses the strength, select
	 * which side (A, B or Both) the TD-agent should play during the
	 * evaluation-matches.If the reference-Agent is a perfect player, then it
	 * can make sense in certain cases to play only one side, since the other
	 * side may always lead to a loss. If the option <code>PLAY_BOTH</code> is
	 * selected, then the number of evaluation matches is twice as much as in
	 * the other two cases.
	 */
	@XmlElement
	public EvaluationPlayAs evaluationPlayAs;
	
	@XmlElement
	public int evaluationNumMatches = 200;

	/**
	 * Defines the intervals in which certain information should be retrieved
	 * from the agent. This could be the evaluation of the agent-strength or the
	 * current exploration-rate and more.
	 */
	@XmlElement
	public InfoInterval infoInterval;

	/**
	 * A List of measures that can be retrieved from the agent. It can be
	 * defined later, which measures should actually be tracked during the
	 * training.
	 * 
	 * @author Markus Thill
	 * 
	 */
	@XmlRootElement
	public static enum InfoMeasures {
		GLOBAL_ALPHA, // The global exploration rate
		EPSILON, // The exploration-rate
		SUCESSRATE, // The success-rate as one value
		SUCESSRATE_DETAIL, // Detailed information about the evaluation:
							// Wins/Draws/Losses
		TIME // Time-stamp of the current measure
	}

	@XmlElement
	public TrackedInfoMeasures trackInfoMeasures;

	/**
	 * All parameters related to the Temporal Difference Learning (TDL)
	 * algorithm.
	 */
	@XmlElement
	public TDParams tdPar;

	/**
	 * All parameters, that are needed to generate an n-tuple system as value
	 * function approximation.
	 */
	@XmlElement
	public NTupleParams nPar;

	/**
	 * Object, containing the params of the learning-rate adaption-algorithm.
	 * Normally is casted in the corresponding Learning-Rate Class.
	 */
	@XmlElement
	public LearningRateParams lrPar;
}
