package multiTraining;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import objectIO.LoadSave;
import nTupleTD.TrainingParams;

@XmlRootElement
public class MultiTrainingParams {
	public static final String EXPERIMENTS_FILE_NAME = "exp";
	public static final String STD_FOLDER_XML = "parXML";
	public static final String STD_FOLDER_OUTPUT_TRAINING = "results";
	public static final String FILE_NAME_EXP_SUMMARY_CSV = "experiments.csv";

	@XmlElement
	public TrainingParams parBasis;

	@XmlRootElement
	public enum ExperimentType {
		SIMPLESEARCH, GRIDSEARCH, UNRELATED_EXPERIMENTS
	};

	@XmlElement
	public ExperimentType experimentType = ExperimentType.GRIDSEARCH;

	@XmlElement
	public int numRepeatsPerExperiment = -1;

	/**
	 * Basis folder of the multi-training. All other folder- and file-paths
	 * specified in this params class are relative to this path.
	 */
	@XmlElement
	public String multiTrainingBaseFolder = null;

	@XmlElement
	public String outputFolderTraining = STD_FOLDER_OUTPUT_TRAINING;
	
	@XmlElement
	public String outputFolderLogFiles = null;

	/**
	 * Set to true, if it should be allowed to save the agents. Since the agents
	 * require a lot of memory, it can make sense to save only the agent of the
	 * first repeat an experiment.
	 */
	@XmlElement
	public boolean saveAgents = false;

	/**
	 * Set to true, if only the first agent of an experiment should be saved.
	 * However, the option {@link MultiTrainingParams#saveAgents} also has to be
	 * activated, otherwise no agents are saved.
	 */
	@XmlElement
	public boolean saveOnlyFirstAgent = true;

	@XmlRootElement
	public enum OverrideFiles {
		ALWAYS, NEVER, ASK
	};

	@XmlElement
	public OverrideFiles overrideFiles = OverrideFiles.ASK;

	/**
	 * The Multi-Training process will generate an XML-file containing the
	 * Training-Params ({@link TrainingParams}) for every experiment. As soon,
	 * as these files are generated, the process will rely on these files and
	 * work one file after the other.
	 */
	@XmlElement
	public String xmlTrainingParamsFolder = STD_FOLDER_XML;

	/**
	 * Multi-Threading can be used to run more than one experiment in parallel.
	 * This is determined by this number.
	 */
	@XmlElement
	public int numParallelRuns = 1;

	// public boolean readParametersFromFile = false;
	// public String parameterFileFolder = null; // Parameters defined in a
	// text-file... Don't know yet...


	@XmlElement
	private ArrayList<ExperimentList> parameters;
	
	public MultiTrainingParams() {
		parameters = new ArrayList<>();

		// TODO: If readParametersFromFile==true, then read the file now!
		// Design file like in SPOT...
		// One line per experiment
	}

	public void resetValueList() {
		parameters.clear();
	}

	private void addSingleColumnExperimentList(List<Parameter> p) {
		List<List<Parameter>> e = MultiTrainingFactory.mergeParamLists(p);
		ExperimentList expList = MultiTrainingFactory.simpleSearch(e);
		parameters.add(expList);
	}

	/**
	 * Adds a list of values for a certain field, that should be used for the
	 * Multi-Training. Depending on the search-type (grid search, creating the
	 * Cartesian product of all lists or a simple search, which simply combines
	 * elements at the same index of the lists), these value-lists will later be
	 * used to generate the individual experiments.
	 * 
	 * @param subObject
	 *            The name of a parameters-object within the
	 *            {@link TrainingParams} instance. This could be "tdPar",
	 *            "nPar", null, or "", where the last two indicate, that no
	 *            sub-object is addressed, but rather an field directly in the
	 *            {@link TrainingParams} instance.
	 * @param fieldName
	 *            The field for which the values are defined.
	 * @param values
	 *            Can be an array from type {@link Boolean}[], {@link Short}[],
	 *            {@link Integer}[], {@link Long}[], {@link Float}[] or
	 *            {@link Double}[].
	 */
	public void addSingleColumnTable(String subObject, String fieldName,
			Object[] values) {
		List<Parameter> p = MultiTrainingFactory.generateParamList(subObject,
				fieldName, values);
		addSingleColumnExperimentList(p);
	}

	public void addSingleColumnTable(String subObject, String fieldName,
			short begin, short step, short end) {
		List<Parameter> p = MultiTrainingFactory.generateParamList(subObject,
				fieldName, begin, step, end);
		addSingleColumnExperimentList(p);
	}

	public void addSingleColumnTable(String subObject, String fieldName,
			int begin, int step, int end) {
		List<Parameter> p = MultiTrainingFactory.generateParamList(subObject,
				fieldName, begin, step, end);
		addSingleColumnExperimentList(p);
	}

	public void addSingleColumnTable(String subObject, String fieldName,
			long begin, long step, long end) {
		List<Parameter> p = MultiTrainingFactory.generateParamList(subObject,
				fieldName, begin, step, end);
		addSingleColumnExperimentList(p);
	}

	public void addSingleColumnTable(String subObject, String fieldName,
			float begin, float step, float end) {
		List<Parameter> p = MultiTrainingFactory.generateParamList(subObject,
				fieldName, begin, step, end);
		addSingleColumnExperimentList(p);
	}

	public void addSingleColumnTable(String subObject, String fieldName,
			double begin, double step, double end) {
		List<Parameter> p = MultiTrainingFactory.generateParamList(subObject,
				fieldName, begin, step, end);
		addSingleColumnExperimentList(p);
	}
	
	// Make logarithmic steps
	public void addSingleColumnTableLog(String subObject, String fieldName,
			double begin, double step, double end) {
		List<Parameter> p = MultiTrainingFactory.generateParamListLog(subObject,
				fieldName, begin, step, end);
		addSingleColumnExperimentList(p);
	}

	public void addSingleColumnTable(List<Parameter> p) {
		addSingleColumnExperimentList(p);
	}

	@SafeVarargs
	public final void addMultiColumnTable(List<Parameter>... columns) {
		List<List<Parameter>> x = MultiTrainingFactory.mergeParamLists(columns);
		//
		// Put the columns together and form a list of experiments. Every index
		// of the following expList represents one experiment. Each experiment
		// is made up of several columns (parameters.)
		//
		ExperimentList expList = MultiTrainingFactory.simpleSearch(x);
		parameters.add(expList);
	}

	public ExperimentList generateExperiments() {
		switch (experimentType) {
		case GRIDSEARCH:
			return MultiTrainingFactory.gridSearch1(parameters);
		case SIMPLESEARCH:
			return MultiTrainingFactory.simpleSearch1(parameters);
		case UNRELATED_EXPERIMENTS:
		default:
			return MultiTrainingFactory.mergeUnrelatedExperiments1(parameters);
		}
	}

	public static void main(String args[]) {
		MultiTrainingParams mtp = new MultiTrainingParams();
//		List<Parameter> eps = MultiTrainingFactory.generateParamList("tdPar",
//				"epsilonInit", 0.0f, 0.1f, 1.0f);
//		List<Parameter> games = MultiTrainingFactory.generateParamList("",
//				"numGames", 0, 1000, 10000);
//		List<List<Parameter>> experiments = MultiTrainingFactory
//				.mergeParamLists(eps, games);
		//mtp.parameters = experiments;
		// mtp.printExperimentList(new OutputStreamWriter(System.out));
		mtp.addSingleColumnTable("tdPar", "alphaInit", 0.0, 0.1, 0.3);

		//ArrayList<Parameter> z = new ArrayList<>();
		//z.add(new Parameter("x", "y", 4));
		//z.add(new Parameter("q", "z", 8));
		//mtp.p = z;
		
		LoadSave.saveObjectToXML("test.xml", mtp, MultiTrainingParams.class);
	}

}
