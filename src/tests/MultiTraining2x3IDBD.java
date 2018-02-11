package tests;

import gui.OverrideDialogConsole;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import multiTraining.MultiTraining;
import multiTraining.MultiTrainingFactory;
import multiTraining.MultiTrainingParams;
import multiTraining.Parameter;
import multiTraining.MultiTrainingParams.ExperimentType;
import nTupleTD.NTupleFactory;
import nTupleTD.NTupleParams;
import nTupleTD.TDParams;
import nTupleTD.TrainingParams;
import nTupleTD.NTupleParams.LUTSelection;
import nTupleTD.NTupleParams.TupleGeneration;
import nTupleTD.TrainingParams.EvaluationPlayAs;
import nTupleTD.TrainingParams.InfoMeasures;
import tools.Function.FunctionScheme;
import training.InfoInterval;
import training.TrackedInfoMeasures;
import adaptableLearningRates.IDBDParams;
import adaptableLearningRates.LearningRateParams;
import adaptableLearningRates.ActivationFunction.Activation;
import adaptableLearningRates.LearningRateParams.LearningRateMethod;

public class MultiTraining2x3IDBD {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//
		// No board inversion. Use 1 LUT/player
		//
		int NUMGAMES = 200000;
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
		td.alphaInit = 9999;
		td.alphaFinal = 9999;
		td.alphaAdjust = FunctionScheme.EXPONENTIAL;
		td.epsilonInit = 0.1;
		td.epsilonFinal = 0.1;
		td.epsilonAdjust = FunctionScheme.NONE;
		td.gamma = 1.0;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.nTupleMasks = null;
		nPar.numLUTSets = 2;
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		nPar.randomLength = false;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7;
		nPar.tupleNum = 50;
		nPar.useCornerSymmetry = true;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;

		//LearningRateParams lrPar = new LearningRateParams();
		//lrPar.learningRateMeth = LearningRateMethod.TDL;
		//par.lrPar = lrPar;
		
		IDBDParams idbd = new IDBDParams();
		idbd.beta_init = 999;
		idbd.theta = 999;
		par.lrPar = (LearningRateParams) idbd;

		par.tdPar = td;
		par.nPar = nPar;
		
		
		
//		try {
//			JAXBContext jaxbContext = JAXBContext.newInstance(TrainingParams.class, IDBDParams.class, LearningRateParams.class);
//
//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//
//			//
//			// for getting nice formatted output
//			//
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
//					Boolean.TRUE);
//
//			// specify the location and name of xml file to be created
//			File XMLfile = new File("fff.xml");
//
//			//
//			// Writing to XML file
//			//
//			jaxbMarshaller.marshal(par, XMLfile);
//
//			// Writing to console: just for debug-purposes
//			// jaxbMarshaller.marshal(o, System.out);
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
		
		
		

		//
		// Now design the experiments
		//
		// Create n-Tuple new for every run: We want to see how good the other
		// parameters are in general regardless of the n-tuples.
		// Repeats: 20
		// N-Tuple Num: 20, 30, 40, 50, 60, 70
		// Tuple-length: 3, 4, 5, 6, 7, 8
		// Alpha: 0.0001, 0.0002, ..., 0.001, 0.002, ..., 0.01, 0.02, ....
		// Epsilon: Keep constant for now: eps = 0.1

		//
		// Alpha-Table
		//
//		List<Parameter> a1 = new ArrayList<Parameter>();
//		List<Parameter> a2 = new ArrayList<Parameter>();
//		a1.addAll(MultiTrainingFactory.generateParamList("tdPar", "alphaInit",
//				0.0001f, 0.0001f, 0.001f));
//		a2.addAll(MultiTrainingFactory.generateParamList("tdPar", "alphaFinal",
//				0.0001f, 0.0001f, 0.001f));
//		a1.addAll(MultiTrainingFactory.generateParamList("tdPar", "alphaInit",
//				0.001f, 0.001f, 0.01f));
//		a2.addAll(MultiTrainingFactory.generateParamList("tdPar", "alphaFinal",
//				0.001f, 0.001f, 0.01f));

		
		MultiTrainingParams mtp = new MultiTrainingParams();
		
		//
		// N-tuple Length
		//
		//mtp.addSingleColumnTable("nPar", "tupleLen", 4, 1, 8);
		// Take length 7
		
		//
		// N-tuple-Num
		//
		//mtp.addSingleColumnTable("nPar", "tupleNum", 40, 10, 70);
		// Take num 40
		
		//
		// No alpha needed
		//
		//mtp.addMultiColumnTable(a1, a2);
		
		// For a 40x7 tuple system an alpha of 0.0005 was the best before
		List<Parameter> b1 = new ArrayList<Parameter>();
		b1.addAll(MultiTrainingFactory.generateParamListLog("lrPar", "beta_init", 0.0001, 0.0001, 0.001));
		b1.addAll(MultiTrainingFactory.generateParamListLog("lrPar", "beta_init", 0.002, 0.001, 0.01001));
		mtp.addSingleColumnTable(b1);
		
		// Theta
		List<Parameter> t1 = new ArrayList<Parameter>();
		t1.addAll(MultiTrainingFactory.generateParamList("lrPar", "theta", 0.01, 0.01, 0.1));
		t1.addAll(MultiTrainingFactory.generateParamList("lrPar", "theta", 0.2, 0.1, 1.0));
		t1.addAll(MultiTrainingFactory.generateParamList("lrPar", "theta", 2, 1, 5));
		mtp.addSingleColumnTable(t1);
		
		mtp.parBasis = par;
		mtp.experimentType = ExperimentType.GRIDSEARCH;
		mtp.multiTrainingBaseFolder = "C:/Users/Markus Thill/Documents/ConnectFour/Master Thesis Thill/Dots-And-Boxes/Experiments/Initial Tests/02.02.2015/2x3IDBD";
		mtp.numRepeatsPerExperiment = 20;
		mtp.numParallelRuns = 1;

		MultiTraining mt = new MultiTraining(mtp, new OverrideDialogConsole(), System.out);
		mt.init();
	    mt.run();
	}

}
