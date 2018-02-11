package tests;

import gui.OverrideDialogConsole;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.swing.JFileChooser;

import multiTraining.MultiTraining;
import multiTraining.MultiTrainingFactory;
import multiTraining.MultiTrainingParams;
import multiTraining.Parameter;
import multiTraining.MultiTrainingParams.ExperimentType;
import nTupleTD.NTupleFactory;
import nTupleTD.NTupleParams;
import nTupleTD.TDLearning;
import nTupleTD.TDParams;
import nTupleTD.TrainingParams;
import nTupleTD.NTupleParams.LUTSelection;
import nTupleTD.NTupleParams.TupleGeneration;
import nTupleTD.TrainingParams.EvaluationPlayAs;
import nTupleTD.TrainingParams.InfoMeasures;
import objectIO.JFileChooserApprove;
import objectIO.LoadSave;

import org.junit.Test;

import tools.Function.FunctionScheme;
import training.InfoInterval;
import training.TrackedInfoMeasures;
import training.TrainingEnvironment;
import adaptableLearningRates.AutoStepParams;
import adaptableLearningRates.ActivationFunction.Activation;
import adaptableLearningRates.IDBDParams;
import adaptableLearningRates.TCLParams;

public class Tests2605 {
	
	private String getFilePath() {
		JFileChooserApprove fc = new JFileChooserApprove();
		//fc.setFileFilter((agentFilter));
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showSaveDialog(null);
		String path = null;

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			path = fc.getSelectedFile().getPath();

			if (!path.toLowerCase().endsWith("txt"))
				path = path + "." + "txt";

		} 
		return path;
	}
	
	@Test
	public void test3x3RandBoxWalk() throws IOException {

		//
		// Test an 3x3Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 500000;
		int M = 3, N = 3;

		//
		TrainingParams par = new TrainingParams();
		par.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;
		par.evaluationNumMatches = 50;
		par.infoInterval = new InfoInterval(0, 10000, NUMGAMES);
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
		
		// Try even higher exploration rates...
		td.epsilonFinal = 0.2; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		td.epsilonInit = 0.2; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		td.epsilonAdjust = FunctionScheme.NONE;
		td.gamma = 1.0;
		
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true;
		td.useImmediateRewards = true;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		td.lambda = 0.0;
		td.replacingTraces = true;
		td.resettingTraces = false;

		NTupleParams nPar = new NTupleParams();
		nPar.lutSelection = new LUTSelection(0.0f, 0.0f, 0.0f, 0.0f); //new LUTSelection(16.5f, -8.5f, 1.0f, 0.0f);
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 1;
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_BOX_WALK;
		nPar.tupleLen = 12; //12
		nPar.tupleNum = 70; //70 //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nPar.useCornerSymmetry = false; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;

		// Generate n-tuples
		// Should be done by the initialization of n-tuples
		//if (nPar.alwaysGenerateNewNtuples)
		//	nPar.nTupleMasks = nPar.nTupleMasks = NTupleFactory
		//			.createNTupleList(nPar, par.m, par.n);
		System.out.println("Created N-tuples...");
		
		//IDBDParams lrPar = new IDBDParams();//new StandardTDLParams();
		//lrPar.beta_init = -7.5;
		//lrPar.theta = 0.01;
		//par.lrPar = lrPar;
		
		AutoStepParams asPar = new AutoStepParams();
		asPar.alphaInit = Math.exp(-6.0);
		asPar.mu = 1e-3;
		asPar.tau = 1e4;
		par.lrPar = asPar;
		
		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream(getFilePath());
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		TDLearning x = te.train(1); // verbosity 1

		LoadSave zip = new LoadSave(null, ".", "ZIP", "agt.zip");
		zip.saveObjectAsZip(x, 10000000);

		LoadSave xml = new LoadSave(null, ".", "XML", "xml");
		xml.saveObjectToXML(par, TrainingParams.class);
	}
	
	@Test
	public void trainXMLAutostep() throws IOException {
		TrainingParams par = (TrainingParams) LoadSave.loadObjectFromXML("C:/Users/Markus Thill/Desktop/3x3_try12.xml", TrainingParams.INVOLVED_CLASSES);
		int NUMGAMES = 40000;
		par.numGames = NUMGAMES;
		par.evaluationNumMatches = 10;
		par.infoInterval = new InfoInterval(0, 1000, NUMGAMES);
		par.nPar.alwaysGenerateNewNtuples = false;
		
		// Try corner symmetry on
		par.nPar.useCornerSymmetry = true;
		
		//
		// Test elig-traces [rr] with nl-IDBD
		par.tdPar.activation = Activation.TANH;
		
		//par.tdPar.lambda = 0.5;
		//par.tdPar.replacingTraces = true;
		//par.tdPar.resettingTraces = true;
		
		//+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#
		par.tdPar.alphaInit = 0.002; //max. 0.005
		par.tdPar.alphaFinal = 0.002;
		TCLParams lrPar = new TCLParams();
		lrPar.expSchemeFactor = 1.5;
		lrPar.updateLUTBeforeStepSize = true;
		lrPar.useExpScheme = true;
		lrPar.useRWC = true;
		par.lrPar = lrPar;
		//+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#
		
		//IDBDParams lrPar = new IDBDParams();
		//lrPar.beta_init = -7.0;
		//lrPar.theta = 1.0;
		//par.lrPar = lrPar;
		//
		
		// Test board-inversion....
		//par.tdPar.useBoardInversion = true;
		//par.nPar.lutSelection = new LUTSelection(0.0f, 0.0f, 0.0f, 0.0f); //new LUTSelection(16.5f, -8.5f, 1.0f, 0.0f);
		//par.nPar.numLUTSets = 1;
		//
		
		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream(getFilePath());
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		TDLearning q = te.train(1); // verbosity 1
	}
	
	
	
	
	@Test
	public void autoStepMult() throws IOException {
		TrainingParams par = (TrainingParams) LoadSave.loadObjectFromXML("C:/Users/Service/Desktop/3x3_try12.xml", TrainingParams.INVOLVED_CLASSES);
		
		int NUMGAMES = 50000;
		par.evaluationNumMatches = 50;
		par.infoInterval = new InfoInterval(0, 1000, NUMGAMES);
		par.numGames = NUMGAMES;
		par.nPar.alwaysGenerateNewNtuples = false;
		
		// Try corner symmetry on
		par.nPar.useCornerSymmetry = true;
		
		//
		// Test elig-traces [rr] with nl-IDBD
		par.tdPar.activation = Activation.NONE;
		
		//par.tdPar.lambda = 0.5;
		//par.tdPar.replacingTraces = true;
		//par.tdPar.resettingTraces = true;
		
		//+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#
		AutoStepParams asPar = new AutoStepParams();
		asPar.alphaInit = Math.exp(-6.0);
		asPar.mu = 1e-3;
		asPar.tau = 1e4;
		par.lrPar = asPar;
		
		//
		//
		MultiTrainingParams mtp = new MultiTrainingParams();
		List<Parameter> a1 = MultiTrainingFactory.generateParamList("tdPar",
				"alphaInit", 0.001f, 0.1f, 0.001f);
		List<Parameter> a2 = MultiTrainingFactory.generateParamList("tdPar",
				"alphaFinal", 0.001f, 0.1f, 0.001f);
		mtp.addMultiColumnTable(a1, a2);

		mtp.parBasis = par;
		mtp.experimentType = ExperimentType.GRIDSEARCH;
		mtp.multiTrainingBaseFolder = "C:/Users/Service/Desktop/12/autostep";
		mtp.numRepeatsPerExperiment = 22;
		mtp.numParallelRuns = 2;

		MultiTraining mt = new MultiTraining(mtp, new OverrideDialogConsole(),
				System.out);
		//mt.init();
		mt.run();
	}
	
	
	@Test
	public void tclMult() throws IOException {
		TrainingParams par = (TrainingParams) LoadSave.loadObjectFromXML("C:/Users/Service/Desktop/3x3_try12.xml", TrainingParams.INVOLVED_CLASSES);
		
		int NUMGAMES = 50000;
		par.evaluationNumMatches = 50;
		par.infoInterval = new InfoInterval(0, 1000, NUMGAMES);
		par.numGames = NUMGAMES;
		par.nPar.alwaysGenerateNewNtuples = false;
		
		// Try corner symmetry on
		par.nPar.useCornerSymmetry = true;
		
		//
		// Test elig-traces [rr] with nl-IDBD
		par.tdPar.activation = Activation.NONE;
		
		par.tdPar.lambda = 0.8;
		par.tdPar.replacingTraces = true;
		par.tdPar.resettingTraces = true;
		
		//+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#
		par.tdPar.alphaInit = 0.002; //max. 0.005
		par.tdPar.alphaFinal = 0.002;
		TCLParams lrPar = new TCLParams();
		lrPar.expSchemeFactor = 1.5;
		lrPar.updateLUTBeforeStepSize = true;
		lrPar.useExpScheme = true;
		lrPar.useRWC = true;
		par.lrPar = lrPar;
		
		//
		//
		MultiTrainingParams mtp = new MultiTrainingParams();
		List<Parameter> a1 = MultiTrainingFactory.generateParamList("tdPar",
				"alphaInit", 0.002f, 0.1f, 0.002f);
		List<Parameter> a2 = MultiTrainingFactory.generateParamList("tdPar",
				"alphaFinal", 0.002f, 0.1f, 0.002f);
		mtp.addMultiColumnTable(a1, a2);

		mtp.parBasis = par;
		mtp.experimentType = ExperimentType.GRIDSEARCH;
		mtp.multiTrainingBaseFolder = "C:/Users/Service/Desktop/12/tcl-exp1.5rr0.8";
		mtp.numRepeatsPerExperiment = 24;
		mtp.numParallelRuns = 2;

		MultiTraining mt = new MultiTraining(mtp, new OverrideDialogConsole(),
				System.out);
		mt.init();
		mt.run();
	}
}
