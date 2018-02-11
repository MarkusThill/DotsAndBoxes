package tests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import nTupleTD.NTupleFactory;
import nTupleTD.NTupleParams;
import nTupleTD.NTupleParams.LUTSelection;
import nTupleTD.TDLearning;
import nTupleTD.TDParams;
import nTupleTD.TrainingParams;
import nTupleTD.NTupleParams.TupleGeneration;
import nTupleTD.TrainingParams.EvaluationPlayAs;
import nTupleTD.TrainingParams.InfoMeasures;
import objectIO.LoadSave;
import org.junit.Test;
import tools.Function.FunctionScheme;
import training.InfoInterval;
import training.TrackedInfoMeasures;
import training.TrainingEnvironment;
import adaptableLearningRates.ALAPParams;
import adaptableLearningRates.AutoStepParams;
import adaptableLearningRates.IDBDParams;
import adaptableLearningRates.LearningRateParams;
import adaptableLearningRates.RPropParams;
import adaptableLearningRates.StandardTDLParams;
import adaptableLearningRates.ActivationFunction.Activation;
import adaptableLearningRates.TCLParams;

public class NTuplesInitialRuns {
	
	
	
	@Test
	public void test2x2ALAP() throws IOException {

		//
		// Test an 2x2Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 10000;
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
		td.epsilonInit = 0.2;
		td.epsilonFinal = 0.2;
		td.epsilonAdjust = FunctionScheme.LINEAR;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;/////////////////////////////////
		td.useImmediateRewards = true;
		
		td.lambda = 0.0;
		td.resettingTraces = false;
		td.replacingTraces = false;
		

		NTupleParams nPar = new NTupleParams();
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 2;  ////////////////////////////////////////////////////
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7;
		nPar.tupleNum = 40;
		nPar.useCornerSymmetry = true;// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;
		
		
		ALAPParams lrPar = new ALAPParams();
		lrPar.alphaInit = 0.001;
		lrPar.gamma = 0.01;
		lrPar.theta = 0.01;
		par.lrPar = lrPar;

		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream("2x2.txt");
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		TDLearning x = te.train(0);

		LoadSave zip = new LoadSave(null, ".", "ZIP", "agt.zip");
		zip.saveObjectAsZip(x, 10000000);

		LoadSave xml = new LoadSave(null, ".", "XML", "xml");
		xml.saveObjectToXML(par, TrainingParams.class);
	}
	
	
	
	@Test
	public void test2x2RPROP() throws IOException {

		//
		// Test an 2x2Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 100000;
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
		td.epsilonInit = 0.2; //
		td.epsilonFinal = 0.2;
		td.epsilonAdjust = FunctionScheme.LINEAR;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;/////////////////////////////////
		td.useImmediateRewards = true;
		
		td.lambda = 0.0;
		td.resettingTraces = false;
		td.replacingTraces = false;
		

		NTupleParams nPar = new NTupleParams();
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 2;  ////////////////////////////////////////////////////
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7;
		nPar.tupleNum = 40;
		nPar.useCornerSymmetry = true;// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;
		
		
		RPropParams lrPar = new RPropParams();
		lrPar.improved = true;
		lrPar.plusVariant = false;
		lrPar.initialDelta = 0.001;
		par.lrPar = lrPar;

		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream("2x2.txt");
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		TDLearning x = te.train(0);

		LoadSave zip = new LoadSave(null, ".", "ZIP", "agt.zip");
		zip.saveObjectAsZip(x, 10000000);

		LoadSave xml = new LoadSave(null, ".", "XML", "xml");
		xml.saveObjectToXML(par, TrainingParams.class);
	}
	
	@Test
	public void test4x4RandBoxWalk() throws IOException {
		//
		// Test an 3x3Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 4000000;
		int M = 4, N = 4;

		//
		TrainingParams par = new TrainingParams();
		par.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;
		par.evaluationNumMatches = 10;
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
		td.epsilonFinal = 0.2;
		td.epsilonInit = 0.2;
		td.epsilonAdjust = FunctionScheme.NONE;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;
		td.useImmediateRewards = true;
		
		td.lambda = 0.0;
		td.replacingTraces = true;
		td.resettingTraces = false;

		NTupleParams nPar = new NTupleParams();
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 2;
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_BOX_WALK;
		nPar.tupleLen = 16;
		nPar.tupleNum = 170;
		nPar.useCornerSymmetry = true; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;
	
		AutoStepParams lrPar = new AutoStepParams();//new StandardTDLParams();
		lrPar.alphaInit = Math.exp(-6.0);
		lrPar.mu = 0.001;
		lrPar.tau = 1e4;
		par.lrPar = lrPar;
		
		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream(M + "x" + N + ".txt");
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
	public void test3x3RandBoxWalk() throws IOException {

		//
		// Test an 3x3Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 4000000;
		int M = 3, N = 3;

		//
		TrainingParams par = new TrainingParams();
		par.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;
		par.evaluationNumMatches = 20;
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
		td.epsilonFinal = 0.2;
		td.epsilonInit = 0.2;
		td.epsilonAdjust = FunctionScheme.NONE;
		td.gamma = 1.0;
		
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true;
		td.useImmediateRewards = true;
		
		td.lambda = 0.0;
		td.replacingTraces = true;
		td.resettingTraces = false;

		NTupleParams nPar = new NTupleParams();
		nPar.lutSelection = new LUTSelection(0.0f, 0.0f, 0.0f, 0.0f);//new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 1;
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_BOX_WALK;
		nPar.tupleLen = 70;
		nPar.tupleNum = 16;
		nPar.useCornerSymmetry = false; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = nPar.nTupleMasks = NTupleFactory
					.createNTupleList(nPar, par.m, par.n);
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
		FileOutputStream fos = new FileOutputStream(M + "x" + N + ".txt");
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
	public void test2x3RandBoxWalk() throws IOException {

		//
		// Test an 2x3Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 500000;
		int M = 2, N = 3;

		//
		TrainingParams par = new TrainingParams();
		par.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;
		par.infoInterval = new InfoInterval(0, 1000, NUMGAMES);
		par.lrPar = null;
		par.m = M;
		par.n = N;
		par.numGames = NUMGAMES;
		par.trackInfoMeasures = new TrackedInfoMeasures(
				InfoMeasures.GLOBAL_ALPHA, InfoMeasures.EPSILON,
				InfoMeasures.SUCESSRATE, InfoMeasures.TIME);

		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.alphaFinal = 0.0001; 
		td.alphaInit = 0.0001;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true;
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.lutSelection = null;
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 1;
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_BOX_WALK;
		nPar.tupleLen = 14; // !!!!!!!!!!!!!!!!! Use 100 8-tuple
		nPar.tupleNum = 10;
		nPar.useCornerSymmetry = true;// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
										// Turn off??
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = nPar.nTupleMasks = NTupleFactory
					.createNTupleList(nPar, par.m, par.n);
		
		LearningRateParams lrPar = new StandardTDLParams();
		par.lrPar = lrPar;

		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream(M + "x" + N + ".txt");
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		TDLearning x = te.train(0);

		LoadSave zip = new LoadSave(null, ".", "ZIP", "agt.zip");
		zip.saveObjectAsZip(x, 10000000);

		LoadSave xml = new LoadSave(null, ".", "XML", "xml");
		xml.saveObjectToXML(par, TrainingParams.class);
	}
	

	@Test
	public void test2x3() throws IOException {

		//
		// Test an 2x3Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 2000000;
		int M = 2, N = 3;

		//
		TrainingParams par = new TrainingParams();
		par.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;
		par.infoInterval = new InfoInterval(0, 1000, NUMGAMES);
		par.lrPar = null;
		par.m = M;
		par.n = N;
		par.numGames = NUMGAMES;
		par.trackInfoMeasures = new TrackedInfoMeasures(
				InfoMeasures.GLOBAL_ALPHA, InfoMeasures.EPSILON,
				InfoMeasures.SUCESSRATE, InfoMeasures.TIME);

		TDParams td = new TDParams();
		td.activation = Activation.TANH;
		td.alphaInit = 0.0006;
		td.alphaFinal = 0.0006;
		td.alphaAdjust = FunctionScheme.EXPONENTIAL;
		td.epsilonInit = 0.2;
		td.epsilonFinal = 0.1;
		td.epsilonAdjust = FunctionScheme.LINEAR;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;
		td.useImmediateRewards = true;
		
		td.lambda = 0.8;
		td.replacingTraces = true;

		NTupleParams nPar = new NTupleParams();
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 2;
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_BOX_WALK;
		nPar.tupleLen = 8; 
		nPar.tupleNum = 20;
		nPar.useCornerSymmetry = true;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;
		
		//LearningRateParams lrPar = new StandardTDLParams();
		//par.lrPar = lrPar;
		
		AutoStepParams asPar = new AutoStepParams();
		asPar.alphaInit = Math.exp(-7.0);
		asPar.mu = 1e-3;
		asPar.tau = 1e4;
		par.lrPar = asPar;

		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream(M + "x" + N + ".txt");
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		TDLearning x = te.train(0);

		LoadSave zip = new LoadSave(null, ".", "ZIP", "agt.zip");
		zip.saveObjectAsZip(x, 10000000);

		LoadSave xml = new LoadSave(null, ".", "XML", "xml");
		xml.saveObjectToXML(par, TrainingParams.class);
	}

	@Test
	public void test2x2() throws IOException {

		//
		// Test an 2x2Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 10000;
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
		td.epsilonInit = 0.2;
		td.epsilonFinal = 0.2;
		td.epsilonAdjust = FunctionScheme.LINEAR;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true;/////////////////////////////////
		td.useImmediateRewards = true;
		
		td.lambda = 0.0;
		td.resettingTraces = true;
		td.replacingTraces = true;
		

		NTupleParams nPar = new NTupleParams();
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 1;////////////////////////////////////////////////////
		nPar.lutSelection =new LUTSelection(0.0f, 0.0f, 0.0f, 0.0f); // new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 12;
		nPar.tupleNum = 1;
		nPar.useCornerSymmetry = true;// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;

		
		AutoStepParams asPar = new AutoStepParams();
		asPar.alphaInit = Math.exp(-4.0);
		asPar.mu = 1e-3;
		asPar.tau = 1e4;
		par.lrPar = asPar;

		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream("2x2.txt");
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		TDLearning x = te.train(0);

		LoadSave zip = new LoadSave(null, ".", "ZIP", "agt.zip");
		zip.saveObjectAsZip(x, 10000000);

		LoadSave xml = new LoadSave(null, ".", "XML", "xml");
		xml.saveObjectToXML(par, TrainingParams.class);
	}
	
	@Test
	public void test2x2TCL() throws IOException {

		//
		// Test an 2x2Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 10000;
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
		td.alphaInit = 0.002; 
		td.alphaFinal = 0.002; 
		td.alphaAdjust = FunctionScheme.EXPONENTIAL;
		td.epsilonInit = 0.2;
		td.epsilonFinal = 0.2;
		td.epsilonAdjust = FunctionScheme.LINEAR;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;/////////////////////////////////
		td.useImmediateRewards = true;
		
		td.lambda = 0.5;
		td.resettingTraces = false;
		td.replacingTraces = false;
		

		NTupleParams nPar = new NTupleParams();
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 2;////////////////////////////////////////////////////
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7;
		nPar.tupleNum = 40;
		nPar.useCornerSymmetry = true;// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;

		
		TCLParams lrPar = new TCLParams();
		lrPar.expSchemeFactor = 2.7;
		lrPar.updateLUTBeforeStepSize = true;
		lrPar.useExpScheme = true;
		lrPar.useRWC = true;
		par.lrPar = lrPar;

		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream("2x2.txt");
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		TDLearning x = te.train(0);

		LoadSave zip = new LoadSave(null, ".", "ZIP", "agt.zip");
		zip.saveObjectAsZip(x, 10000000);

		LoadSave xml = new LoadSave(null, ".", "XML", "xml");
		xml.saveObjectToXML(par, TrainingParams.class);
	}
	
	
	@Test
	public void test2x3IDBDHessian() throws IOException {

		//
		// Test an 2x3Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 2000000;
		int M = 2, N = 3;

		//
		TrainingParams par = new TrainingParams();
		par.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;
		par.evaluationNumMatches = 200;
		par.infoInterval = new InfoInterval(0, 1000, NUMGAMES);
		par.lrPar = null;
		par.m = M;
		par.n = N;
		par.numGames = NUMGAMES;
		par.trackInfoMeasures = new TrackedInfoMeasures(
				InfoMeasures.GLOBAL_ALPHA, InfoMeasures.EPSILON,
				InfoMeasures.SUCESSRATE, InfoMeasures.TIME);

		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.alphaInit = 0.0006;
		td.alphaFinal = 0.0006;
		td.alphaAdjust = FunctionScheme.EXPONENTIAL;
		td.epsilonInit = 0.2;
		td.epsilonFinal = 0.2;
		td.epsilonAdjust = FunctionScheme.LINEAR;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;
		td.useImmediateRewards = true;
		
		td.lambda = 0.0;
		td.replacingTraces = true;

		NTupleParams nPar = new NTupleParams();
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 2;
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7; 
		nPar.tupleNum = 200;
		nPar.useCornerSymmetry = true;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;
		
		//LearningRateParams lrPar = new StandardTDLParams();
		//par.lrPar = lrPar;
		
		IDBDParams asPar = new IDBDParams();
		asPar.beta_init = -7.4;
		asPar.theta = 0.01; //0.01
		par.lrPar = asPar;

		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream(M + "x" + N + ".txt");
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		TDLearning x = te.train(0);

		LoadSave zip = new LoadSave(null, ".", "ZIP", "agt.zip");
		zip.saveObjectAsZip(x, 10000000);

		LoadSave xml = new LoadSave(null, ".", "XML", "xml");
		xml.saveObjectToXML(par, TrainingParams.class);
	}

	@Test
	public void test1x2() throws FileNotFoundException {

		//
		// Test an 1x2Board
		// Markus Thill, 15.12.2014
		//
		int NUMGAMES = 10000;
		int M = 1, N = 2;

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
		td.alphaFinal = 0.1;
		td.alphaInit = 0.1;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true;
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.lutSelection = null;
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 1;
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7;
		nPar.tupleNum = 1;
		nPar.useCornerSymmetry = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = nPar.nTupleMasks = NTupleFactory
					.createNTupleList(nPar, par.m, par.n);
		
		LearningRateParams lrPar = new StandardTDLParams();
		par.lrPar = lrPar;

		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		FileOutputStream fos = new FileOutputStream("1x2.txt");
		PrintStream ps = new PrintStream(fos);
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(ps);
		te.addLogListener(System.out);
		te.train(0);

		LoadSave y = new LoadSave(null, ".", "XML", "xml");

		y.saveObjectToXML(par, TrainingParams.class);
	}
}
