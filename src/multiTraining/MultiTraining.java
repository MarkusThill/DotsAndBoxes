package multiTraining;

import gui.OverrideDialog;
import gui.OverrideDialogConsole;
import gui.OverrideDialog.OverrideOption;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import multiTraining.MultiTrainingParams.ExperimentType;
import multiTraining.MultiTrainingParams.OverrideFiles;
import nTupleTD.NTupleParams;
import nTupleTD.TDLearning;
import nTupleTD.TDParams;
import nTupleTD.TrainingParams;
import nTupleTD.NTupleParams.LUTSelection;
import nTupleTD.NTupleParams.TupleGeneration;
import nTupleTD.TrainingParams.EvaluationPlayAs;
import nTupleTD.TrainingParams.InfoMeasures;
import objectIO.LoadSave;
import org.apache.commons.io.FileUtils;
import tools.CSVTools;
import tools.Function.FunctionScheme;
import training.InfoInterval;
import training.TrackedInfoMeasures;
import training.TrainingEnvironment;
import adaptableLearningRates.ActivationFunction.Activation;
import adaptableLearningRates.IDBD;
import adaptableLearningRates.IDBDParams;
import adaptableLearningRates.LearningRateParams;
import adaptableLearningRates.LearningRateParams.LearningRateMethod;
import adaptableLearningRates.StandardTDLParams;

import com.opencsv.CSVWriter;

//TODO: Allow the user to change the number of CPUs used for this...
public class MultiTraining {
	// Generate a csv-file that summarizes the params of the grid-search (One
	// number for each XML-file)
	// Read in MultiTrainingParams from an XML-File...
	// Define values of the parameter-Lists in XML-File or normal TextFile?

	// String path =
	// Test.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	// String decodedPath = URLDecoder.decode(path, "UTF-8");

	private static final int VERBOSITY_TRAINING = 0;
	private MultiTrainingParams mtp;
	private OverrideDialog overrideDialog;
	private ExperimentList experiments;
	private PrintStream logStream;

	public MultiTraining(MultiTrainingParams mtp, OverrideDialog overrideDialog,
			PrintStream logStream) {
		this.mtp = mtp;
		this.overrideDialog = overrideDialog;
		this.logStream = logStream;
	}

	// init
	public void init() {
		logStream.println("Start Multi Training Initialization!");
		logStream.println("====================================");
		
		//
		// Create the root-folder of this multi-training
		//
		logStream.println("------------------------------------");
		logStream.println("Create Root-Folder of Experiment!");
		createRootFolder();
		logStream.println("Done!");
		logStream.println("------------------------------------");
		
		//
		// Save the mtp-object, to be sure
		// Maybe TODO
//		logStream.println("------------------------------------");
//		logStream
//		.println("Save the MultiTrainingParams-object!");
//		
//		LoadSave.saveObjectToXML(filePath, mtp, MultiTrainingParams.class);
//		logStream.println("Done!");
//		logStream.println("------------------------------------");
		
		//
		// We have to design the experiments first
		//
		logStream.println("------------------------------------");
		logStream
				.println("Generate the Experiments based on the Mult-Training Parameters");
		experiments = mtp.generateExperiments();
		logStream.println("Done!");
		logStream.println("------------------------------------");

		

		//
		// Based on the experiments, we have create a summary in form of a
		// CSV-file, that lists all the experiments in tabular form
		//
		logStream.println("------------------------------------");
		logStream.println("Save a Summary of all Experiments to a CSV File!");
		saveExperimentSummary();
		logStream.println("Done!");
		logStream.println("------------------------------------");

		//
		// Generate the XML-files for the experiments
		//
		logStream.println("------------------------------------");
		logStream
				.println("Generate XML files containing the Training Parameters of the individual Experiments!");
		generateXMLParamFiles();
		logStream.println("Done!");
		logStream.println("====================================");
		logStream.println("\n");

		//
		// Ask, whether old result-folders shall be removed, if
		// existent...
		//
		askToRemoveResultFolder();

	}

	private void askToRemoveResultFolder() {
		String resultsDirPath = mtp.multiTrainingBaseFolder + "/"
				+ mtp.outputFolderTraining;
		File resultsDir = new File(resultsDirPath);
		if (resultsDir.isDirectory()) {
			//
			// ask, if old results shall be removed!
			//
			OverrideOption op = null;
			if (mtp.overrideFiles == OverrideFiles.ASK) {
				logStream.print("A results folder exists already. Remove?");
				OverrideOption showOptions[] = new OverrideOption[] {
						OverrideOption.YES, OverrideOption.NO };
				op = overrideDialog.getUserResponse(resultsDir, showOptions);
			}
			if (op == OverrideOption.YES
					|| mtp.overrideFiles == OverrideFiles.ALWAYS) {
				LoadSave.deleteFolder(resultsDir, logStream);
			}
			logStream.print(op + "\n");
		}
	}

	private void validateTrainingParams() {
		//
		// Check, if the TD-Params are available and n-tuple params as well
		//
		TrainingParams par = mtp.parBasis;
		if (par.tdPar == null)
			logStream.println("WARNING: TD-Params are null!");
		if (par.nPar == null)
			logStream.println("WARNING: N-Tuples-Params are null!");
		//
		// Warn the user, if n-tuples are not supposed to be created by the
		// generation algorithm, but the n-tuple list is not initialized yet.
		//
		if (!par.nPar.alwaysGenerateNewNtuples && par.nPar.nTupleMasks == null) {
			//
			// We do not want to tolerate this
			//
			String msg= 
					"N-Tuples are not supposed to be generated by the algorithm,\n "
					+ "but they are not initialized. Check the parameters\n"
					+ "\"alwaysGenerateNewNtuples\" and \"nTupleMasks\" \n "
					+ "of the NTupleParams object.";
			logStream.println(msg);
			throw new UnsupportedOperationException(msg);
		}
	}

	private void generateXMLParamFiles() {
		validateTrainingParams();
		
		saveBaseParamsToXML();

		//
		// Get all Training-Params
		//
		List<TrainingParams> tp = MultiTrainingFactory.generateTrainingParams(
				mtp.parBasis, experiments);

		//
		// First attempt to create the folder
		//
		File dir = new File(mtp.multiTrainingBaseFolder + "/"
				+ mtp.xmlTrainingParamsFolder);
		boolean created = createFolder(dir);
		if (!created) {
			logStream
					.println("Could not create XML Parameter Folder. Exiting now!");
			return;
		}

		//
		// Now iterate through the list and save all training-param objects as
		// XML-files
		//
		int k = 0;
		for (TrainingParams t : tp) {
			String path = mtp.multiTrainingBaseFolder + "/"
					+ mtp.xmlTrainingParamsFolder + "/"
					+ MultiTrainingParams.EXPERIMENTS_FILE_NAME + k++ + ".xml";
			LoadSave.saveObjectToXML(path, t, TrainingParams.INVOLVED_CLASSES);
		}
		logStream.println("In total " + k + " XML Param files generated in "
				+ dir + "!");
	}

	private void saveBaseParamsToXML() {
		//
		// First save the base Training Params
		//
		String baseParamsPath = mtp.multiTrainingBaseFolder + "/" + "baseParams.xml";
		File f = new File(baseParamsPath);
		if (f.exists()) {
			//
			// File exists already.
			//
			OverrideOption op = null;
			if (mtp.overrideFiles == OverrideFiles.ASK) {
				OverrideOption[] options = new OverrideOption[] {
						OverrideOption.YES, OverrideOption.NO };
				op = overrideDialog.getUserResponse(f, options);

			}
			if (op == OverrideOption.NO
					|| mtp.overrideFiles == OverrideFiles.NEVER)
				return;
		}
		LoadSave.saveObjectToXML(baseParamsPath, mtp.parBasis, TrainingParams.class);
	}

	private void saveExperimentSummary() {
		//
		// Check, if file exists already
		//
		File f = new File(mtp.multiTrainingBaseFolder + "/"
				+ MultiTrainingParams.FILE_NAME_EXP_SUMMARY_CSV);
		if (f.exists()) {
			//
			// File exists already.
			//
			OverrideOption op = null;
			if (mtp.overrideFiles == OverrideFiles.ASK) {
				OverrideOption[] options = new OverrideOption[] {
						OverrideOption.YES, OverrideOption.NO };
				op = overrideDialog.getUserResponse(f, options);

			}
			if (op == OverrideOption.NO
					|| mtp.overrideFiles == OverrideFiles.NEVER)
				return;
		}
		//
		// Create Experiment Summary File
		//
		try {
			printExperimentList(new FileWriter(f));
			logStream.println("File \"" + f + "\" saved!");
		} catch (IOException e) {
			System.err.println("Could not write the Summary file!!!");
			e.printStackTrace();
			System.exit(1);
		}

	}

	private boolean createRootFolder() {
		//
		// Check, if this folder exists already
		//
		File dir = new File(mtp.multiTrainingBaseFolder);
		return createFolder(dir);
	}

	private boolean createFolder(File dir) {
		if (dir.isDirectory()) {
			//
			// Directory exists already
			//
			OverrideOption op = null;
			if (mtp.overrideFiles == OverrideFiles.ASK) {
				OverrideOption[] options = new OverrideOption[] {
						OverrideOption.YES, OverrideOption.NO };
				op = overrideDialog.getUserResponse(dir, options);
			}
			if (op == OverrideOption.NO
					|| mtp.overrideFiles == OverrideFiles.NEVER)
				return false;

			if (!LoadSave.deleteFolder(dir, logStream)) {
				logStream.println("Could not replace folder \"" + dir + "\".");
				return false;
			}
		}
		return LoadSave.createFolder(dir, logStream);
	}

	public void run() {
		//
		// Iterate through all XML-files and perform the desired experiments.
		// Note, that the experiments can run in parallel threads, depending
		// on how many parallel threads are allowed. Each experiment is
		// typically repeated several times.
		//

		//
		// First get all XML-files containing the experiment descriptions.
		//
		File[] xmlFiles = getXMLFiles();

		//
		// Try to create the results folder! We cannot use the createFolder
		// method here, because the folder shall only be created, if not
		// existent. Otherwise, we want to continue with the next experiments
		// (e.g., if the multi-training was aborted for some reason).
		//
		String resultsDirPath = createResultsFolder();
		if (resultsDirPath == null)
			return; // abort, if we could not create the folder...

		final ThreadCounter tc = new ThreadCounter();
		boolean neverOverride = false;
		boolean alwaysOverride = false;
		//
		// Iterate through all XML-Files
		//
		for (File f : xmlFiles) {
			//
			// Load XML File to a TrainingsParams object
			//
			//
			final TrainingParams tp = (TrainingParams) LoadSave
					.loadObjectFromXML(f.getPath(), TrainingParams.INVOLVED_CLASSES);

			//
			// Check, if the folder for the corresponding experiment exists
			// already. If not, then create it now!
			//
			String expName = f.getName().toLowerCase().replaceAll(".xml", "");
			String expPath = resultsDirPath + "/" + expName;
			createExperimentFolder(expPath);

			//
			// Now repeat this experiment several times
			//
			for (int i = 0; i < mtp.numRepeatsPerExperiment; i++) {
				//
				// Create a .csv file, in which the training-results are
				// written. If the file exists already the user may be asked to
				// override the file. If the file shall not be overwritten, then
				// continue with the next repeat of the current experiment.
				//
				final File csvFile = getCSVFile(expPath, i);

				//
				// Get the fileName of the agt.zip file that may be created
				// (only if the option is activated in the parameters.)
				//
				final File agtZipFile = getAgtZipFile(expPath, i);
				//
				// What to do, if file exists already
				//
				if (csvFile.exists()) {
					//
					// File exists already.
					//
					if (neverOverride
							|| mtp.overrideFiles == OverrideFiles.NEVER)
						continue;

					if (!alwaysOverride
							&& mtp.overrideFiles == OverrideFiles.ASK) {
						OverrideOption[] options = new OverrideOption[] {
								OverrideOption.YES, OverrideOption.YES_TO_ALL,
								OverrideOption.NO, OverrideOption.NO_TO_ALL };
						OverrideOption op = overrideDialog.getUserResponse(
								csvFile, options);
						switch (op) {
						case NO_TO_ALL:
							neverOverride = true;
						case NO:
							continue;
						case YES_TO_ALL:
							alwaysOverride = true;
						case YES:
							break;
						default:
							throw new UnsupportedOperationException();
						}
					}
					FileUtils.deleteQuietly(csvFile);
				}

				//
				// Run this experiment once and store the training-progress in
				// the CSV file. The names of the individual repeats are
				// identified by the name of the experiment + the repeat-number.
				//
				runExperimentOnce(tc, tp, expName, i, csvFile, agtZipFile);

			}
		}
	}

	private File getCSVFile(String expPath, int i) {
		String csvPath = expPath + "/"
				+ MultiTrainingParams.EXPERIMENTS_FILE_NAME + i + ".csv";
		final File csvFile = new File(csvPath);
		return csvFile;
	}

	private File getAgtZipFile(String expPath, int i) {
		String agtZipPath = expPath + "/"
				+ MultiTrainingParams.EXPERIMENTS_FILE_NAME + i + ".agt.zip";
		final File agtZipFile = new File(agtZipPath);
		return agtZipFile;
	}

	private void runExperimentOnce(final ThreadCounter tc,
			final TrainingParams tp, final String expName, final int i,
			final File csvFile, final File agtZipFile) {
		while (tc.getThreadCounter() >= mtp.numParallelRuns) {
			//
			// Sleep a little bit and then check again, if we can start
			// a new run.
			//
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//
		// Reserve new Thread
		//
		tc.incrementThreadCounter();

		logStream.println("Starting with Experiment: " + expName + ", repeat: "
				+ (i + 1) + ". Now!");

		//
		// Run a new Thread
		//
		new Thread(new Runnable() {
			@Override
			public void run() {
				TDLearning td = null;
				try {
					td = startSingleTraining(tp, csvFile);
				} catch (Exception e) {
					logStream
							.println("ERROR: Could not train the TD-Agent!. Continue with the next..."
									+ "Experiment: "
									+ expName
									+ "."
									+ "Repeat-No.:" + i);
					e.printStackTrace(logStream);
				}
				//
				// Experiment is finished, therefore this Thread will
				// also be terminated.
				//
				tc.decrementThreadCounter();
				System.gc();
				//
				// Save agent, if required
				//
				if (mtp.saveAgents) {
					if (!mtp.saveOnlyFirstAgent || i == 0) {
						//
						// Save the agent in the same folder as the experiment
						// result CSV-files.
						//
						long estimatedObjectByteSize = td.estimateSizeInBytes();
						String agtZipPath = agtZipFile.getPath();
						try {
							LoadSave.saveObjectAsZip(td,
									estimatedObjectByteSize, agtZipPath);
						} catch (IOException e) {
							logStream
									.println("ERROR: Could not save the TD-Agent!."
											+ "Experiment: "
											+ expName
											+ "."
											+ "Repeat-No.:" + i);
							e.printStackTrace(logStream);
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	private void createExperimentFolder(String expPath) {
		File expDir = new File(expPath);
		if (!expDir.isDirectory()) {
			//
			// This experiment has to be created
			//
			boolean success = expDir.mkdir();
			if (!success) {
				logStream.println("Could not create the folder \"" + expDir
						+ "\"!");
			}
		}
	}

	private String createResultsFolder() {
		String resultsDirPath = mtp.multiTrainingBaseFolder + "/"
				+ mtp.outputFolderTraining;
		File resultsDir = new File(resultsDirPath);
		if (!resultsDir.isDirectory()) {
			// Create only, if not existent
			boolean success = createFolder(resultsDir);
			if (!success) {
				logStream.println("Could not create the folder \"" + resultsDir
						+ "\"! Aborting!");
				return null;
			}
		}
		return resultsDirPath;
	}

	private File[] getXMLFiles() {
		String xmlPath = mtp.multiTrainingBaseFolder + "/"
				+ mtp.xmlTrainingParamsFolder;
		File[] xmlFiles = LoadSave.getDirectoryFiles(xmlPath, "xml");
		return xmlFiles;
	}

	private TDLearning startSingleTraining(TrainingParams par,
			File csvResultFile) {
		TrainingEnvironment te = new TrainingEnvironment(par);
		PrintStream ps = null;
		try {
			ps = new PrintStream(csvResultFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		te.addLogListener(ps);
		// Just for test-purposes
		// te.addLogListener(System.out);
		TDLearning td = te.train(VERBOSITY_TRAINING);
		ps.close();
		return td;
	}

	public void printExperimentList(Writer writer) {
		CSVWriter csv = new CSVWriter(writer, CSVTools.SEPARATOR_AS_CHAR);
		//
		// If the experiments are not related, then we cannot have a header,
		// every line has to be written seperatly incldung the field names in
		// every line.
		//
		if (mtp.experimentType != ExperimentType.UNRELATED_EXPERIMENTS) {
			//
			// Write the Header. Take the field names for the first experiment
			//
			List<Parameter> l = experiments.get(0);
			List<String> header = new ArrayList<>();
			header.add("Experiment");
			for (Parameter p : l) {
				header.add(p.parameterName);
			}
			csv.writeNext(header.toArray(new String[header.size()]), false);

			//
			// Now iterate through the experiments and list the values
			//
			int k = 0; // Row-counter
			for (Experiment i : experiments) {
				//
				// Determine one row
				//
				List<String> row = new ArrayList<>();
				row.add(k++ + "");
				for (Parameter j : i.get()) {
					//NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
					//DecimalFormat df = (DecimalFormat)nf;
					//df.setParseBigDecimal(true);
					//BigDecimal bd = (BigDecimal) df.parse(j.value.toString(), new ParsePosition(0));
					BigDecimal bd = new BigDecimal(j.value.toString())
							.round(new MathContext(8, RoundingMode.HALF_DOWN));
					//bd = bd.round(new MathContext(8, RoundingMode.HALF_DOWN));
					String val = bd.stripTrailingZeros().toPlainString();
					//
					// For german settings: replace the point by a comma
					//
					val = val.replace('.', ',');
					row.add(val);
				}
				csv.writeNext(row.toArray(new String[row.size()]), false);
			}

		} else {
			//
			// Since all experiments are unrelated, we have to print all the
			// variables with their values in each row
			//
			csv.writeNext(new String[] { "Experiment" }, false);
			int k = 0; // Row-counter
			for (Experiment i : experiments) {
				//
				// Determine one row
				//
				List<String> row = new ArrayList<>();
				row.add(k++ + "");
				for (Parameter j : i.get()) {
					row.add(j.parameterName);
					BigDecimal bd = new BigDecimal(j.value.toString())
							.round(new MathContext(6, RoundingMode.HALF_DOWN));
					String val = bd.stripTrailingZeros().toPlainString();
					row.add(val);
				}
				csv.writeNext(row.toArray(new String[row.size()]), false);
			}
		}

		try {
			csv.close();
		} catch (IOException e) {
			e.printStackTrace(logStream);
			e.printStackTrace();
		}
	}

	private static class ThreadCounter {
		int threadCounter;

		public ThreadCounter() {
			threadCounter = 0;
		}

		public synchronized void incrementThreadCounter() {
			threadCounter++;
		}

		public synchronized void decrementThreadCounter() {
			threadCounter--;
		}

		public synchronized int getThreadCounter() {
			return threadCounter;
		}
	}

	public static void main(String[] args) {
		test();

		// String xmlPath = mtp.multiTrainingBaseFolder + "/"
		// + mtp.xmlTrainingParamsFolder;
		// File[] xmlFiles = getDirectoryFiles(xmlPath, "xml");
		// File f = xmlFiles[0];
		// String expPath = f.getName().toLowerCase().replaceAll(".xml", "");

	}

	public static void test() {
		//
		// Test an 2x2Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 1000;
		int M = 2, N = 2;

		//
		TrainingParams par = new TrainingParams();
		par.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;
		par.infoInterval = new InfoInterval(0, 100, NUMGAMES);
		par.lrPar = null;
		par.m = M;
		par.n = N;
		par.numGames = NUMGAMES;
		par.trackInfoMeasures = new TrackedInfoMeasures(
				InfoMeasures.GLOBAL_ALPHA, InfoMeasures.EPSILON,
				InfoMeasures.SUCESSRATE, InfoMeasures.TIME);

		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.alphaInit = 0.001;
		td.alphaFinal = 0.001;
		td.alphaAdjust = FunctionScheme.EXPONENTIAL;
		td.epsilonInit = 0.1;
		td.epsilonFinal = 0.1;
		td.epsilonAdjust = FunctionScheme.NONE;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;// ///////////////////////////////
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 2;// //////////////////////////////////////////////////
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 6;
		nPar.tupleNum = 40;
		nPar.useCornerSymmetry = true;// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;

		LearningRateParams lrPar = new StandardTDLParams();
		//lrPar.learningRateMeth = LearningRateMethod.TDL;
		par.lrPar = lrPar;
		
		par.tdPar = td;
		par.nPar = nPar;

		// MultiTraining
		// =====================================================================================
		MultiTrainingParams mtp = new MultiTrainingParams();
		List<Parameter> eps1 = MultiTrainingFactory.generateParamList("tdPar",
				"epsilonInit", 0.1f, 0.1f, 1.0f);
		List<Parameter> eps2 = MultiTrainingFactory.generateParamList("tdPar",
				"epsilonFinal", 0.1f, 0.1f, 1.0f);
		mtp.addMultiColumnTable(eps1, eps2);

		List<Parameter> a1 = MultiTrainingFactory.generateParamList("tdPar",
				"alphaInit", 0.001f, 0.001f, 0.01f);
		List<Parameter> a2 = MultiTrainingFactory.generateParamList("tdPar",
				"alphaFinal", 0.001f, 0.001f, 0.01f);
		mtp.addMultiColumnTable(a1, a2);

		List<Parameter> g = MultiTrainingFactory.generateParamList("tdPar",
				"gamma", 0.1f, 0.1f, 1.0f);
		mtp.addMultiColumnTable(g);

		mtp.parBasis = par;
		mtp.experimentType = ExperimentType.GRIDSEARCH;
		mtp.multiTrainingBaseFolder = "C:/Users/Markus Thill/Documents/ConnectFour/Master Thesis Thill/Dots-And-Boxes/Experiments/Initial Tests/02.02.2015/Test";
		mtp.numRepeatsPerExperiment = 10;
		mtp.numParallelRuns = 1;

		MultiTraining mt = new MultiTraining(mtp, new OverrideDialogConsole(),
				System.out);
		mt.init();
		mt.run();

	}

}
