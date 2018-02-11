package tests;

import gui.OverrideDialogConsole;
import java.util.ArrayList;
import java.util.List;
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
import adaptableLearningRates.LearningRateParams;
import adaptableLearningRates.StandardTDLParams;
import adaptableLearningRates.ActivationFunction.Activation;
import adaptableLearningRates.LearningRateParams.LearningRateMethod;

public class MultiTraining2x3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//
		// No board inversion. Use 1 LUT/player
		//
		int NUMGAMES = 100000;
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
		td.alphaInit = 999;
		td.alphaFinal = 999;
		td.alphaAdjust = FunctionScheme.EXPONENTIAL;
		td.epsilonInit = 0.1;
		td.epsilonFinal = 0.1;
		td.epsilonAdjust = FunctionScheme.NONE;
		td.gamma = 1.0;
		//td.learningRateMeth = LearningRateMethod.TDL;
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
		nPar.tupleLen = -999;
		nPar.tupleNum = -999;
		nPar.useCornerSymmetry = true;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;
		
		LearningRateParams lrPar = new StandardTDLParams();
		par.lrPar = lrPar;

		par.tdPar = td;
		par.nPar = nPar;

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
		List<Parameter> a1 = new ArrayList<Parameter>();
		List<Parameter> a2 = new ArrayList<Parameter>();
		a1.addAll(MultiTrainingFactory.generateParamList("tdPar", "alphaInit",
				0.0001f, 0.0001f, 0.001f));
		a2.addAll(MultiTrainingFactory.generateParamList("tdPar", "alphaFinal",
				0.0001f, 0.0001f, 0.001f));
		a1.addAll(MultiTrainingFactory.generateParamList("tdPar", "alphaInit",
				0.002f, 0.001f, 0.005f));
		a2.addAll(MultiTrainingFactory.generateParamList("tdPar", "alphaFinal",
				0.002f, 0.001f, 0.005f));

		
		MultiTrainingParams mtp = new MultiTrainingParams();
		
		//
		// N-tuple Length
		//
		mtp.addSingleColumnTable("nPar", "tupleLen", new Integer[] {5,6,7});
		
		//
		// N-tuple-Num
		//
		mtp.addSingleColumnTable("nPar", "tupleNum", new Integer[] {50, 100, 200});
		
		//
		// Alpha
		//
		mtp.addMultiColumnTable(a1, a2);
		
		mtp.parBasis = par;
		mtp.experimentType = ExperimentType.GRIDSEARCH;
		mtp.multiTrainingBaseFolder = "C:/Users/Markus Thill/Documents/ConnectFour/Master Thesis Thill/Dots-And-Boxes/Experiments/Initial Tests/02.02.2015/2x3";
		mtp.numRepeatsPerExperiment = 10;
		mtp.numParallelRuns = 1;

		MultiTraining mt = new MultiTraining(mtp, new OverrideDialogConsole(), System.out);
		mt.init();
	    mt.run();
	}

}
