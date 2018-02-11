package tests;

import nTupleTD.NTupleFactory;
import nTupleTD.NTupleParams;
import nTupleTD.TDLearning;
import nTupleTD.TDParams;
import nTupleTD.TrainingParams;
import nTupleTD.NTupleParams.LUTSelection;
import nTupleTD.NTupleParams.TupleGeneration;
import nTupleTD.TrainingParams.EvaluationPlayAs;
import nTupleTD.TrainingParams.InfoMeasures;
import org.junit.Test;
import tools.Function.FunctionScheme;
import training.InfoInterval;
import training.TrackedInfoMeasures;
import training.TrainingEnvironment;
import adaptableLearningRates.LearningRateParams;
import adaptableLearningRates.StandardTDLParams;
import adaptableLearningRates.TCLParams;
import adaptableLearningRates.ActivationFunction.Activation;

public class TCLSimpleTest {

	@Test
	public void test() {
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
		td.epsilonInit = 0.1;
		td.epsilonFinal = 0.1;
		td.epsilonAdjust = FunctionScheme.NONE;
		td.gamma = 1.0;
		td.randValueFunctionEps = 0.0f;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;/////////////////////////////////
		td.useImmediateRewards = true;
		

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

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = nPar.nTupleMasks = NTupleFactory
					.createNTupleList(nPar, par.m, par.n);
		
		TCLParams tclPar = new TCLParams();
		tclPar.updateLUTBeforeStepSize = true;
		tclPar.expSchemeFactor = 1.5;
		tclPar.useExpScheme = true;
		tclPar.useRWC = true;
		par.lrPar = (LearningRateParams) tclPar;

		par.tdPar = td;
		par.nPar = nPar;

		//
		// Create Training-environment
		//
		TrainingEnvironment te = new TrainingEnvironment(par);
		te.addLogListener(System.out);
		TDLearning x = te.train(0);
	}

}
