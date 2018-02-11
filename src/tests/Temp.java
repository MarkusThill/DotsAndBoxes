package tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import nTupleTD.NTupleParams;
import nTupleTD.TDLearning;
import nTupleTD.TDParams;
import nTupleTD.TrainingParams;
import nTupleTD.NTupleParams.LUTSelection;
import nTupleTD.NTupleParams.TupleGeneration;
import nTupleTD.TrainingParams.EvaluationPlayAs;
import nTupleTD.TrainingParams.InfoMeasures;
import objectIO.LoadSave;
import tools.Function.FunctionScheme;
import training.InfoInterval;
import training.TrackedInfoMeasures;
import training.TrainingEnvironment;
import adaptableLearningRates.AutoStepParams;
import adaptableLearningRates.ActivationFunction.Activation;

public class Temp {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
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
			nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;//Try with Rand- Line Walk.....................................
			nPar.tupleLen = 16;
			nPar.tupleNum = 170;
			nPar.useCornerSymmetry = false; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			nPar.alwaysGenerateNewNtuples = true;
			nPar.useIndexHashTable = false;
			nPar.useSymmetricHashing = false;
			nPar.useSymmetry = true;
		
			AutoStepParams lrPar = new AutoStepParams();//new StandardTDLParams();
			lrPar.alphaInit = Math.exp(-7.0);
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

}
