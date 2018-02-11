package training;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import nTupleTD.TDLearning;
import nTupleTD.TrainingParams;
import nTupleTD.TrainingParams.InfoMeasures;
import solvers.WilsonAlphaBetaAgent;
import dotsAndBoxes.GameState;
import dotsAndBoxes.IAgent;
import dotsAndBoxes.Move;
import agentEvaluation.Evaluation;

public class TrainingEnvironment {
	private final Evaluation eval;
	private final CSVLogger infoLog;
	private final TrainingParams par;
	private long trainingStartTime;

	/**
	 * Set up the training for an TD-Learning agent. The environment has to know
	 * a few parameters in beforehand, so that a {@link TrainingParams} object
	 * is required.
	 * 
	 * @param m
	 *            X-dimension of the board (number of boxes in X-direction)
	 * @param n
	 *            Y-Dimension of the board (number of boxes in Y-direction)
	 */
	public TrainingEnvironment(TrainingParams par) {
		//
		// Init the reference agent. Until now, a agent based on Wilsons Solver,
		// that can play perfectly
		//
		IAgent reference = new WilsonAlphaBetaAgent(par.m, par.n);

		//
		// Init evaluation
		//
		eval = new Evaluation(reference, par.evaluationNumMatches);

		//
		// Init logger
		//
		infoLog = new CSVLogger();

		//
		// Set params of the training-environment
		//
		this.par = par;
	}

	private MeasurePoint trackMeasures(TDLearning tdl) {
		TrackedInfoMeasures m = tdl.getTrainingParams().trackInfoMeasures;
		MeasurePoint measures = new MeasurePoint();
		Iterator<InfoMeasures> i = m.iterator();
		while (i.hasNext()) {
			InfoMeasures im = i.next();
			Object value = getMeasure(tdl, im);
			measures.set(im, value);
		}
		return measures;
	}

	private Object getMeasure(TDLearning tdl, InfoMeasures im) {
		TrainingParams p = tdl.getTrainingParams();
		switch (im) {
		case EPSILON:
			return tdl.getEpsilon();
		case GLOBAL_ALPHA:
			return tdl.getGlobalAlpha();
		case SUCESSRATE:
			return eval.evaluate(tdl, p.m, p.n, p.evaluationPlayAs);
		case TIME:
			return getCurrentTrainingTime();
		case SUCESSRATE_DETAIL:
			// TODO:
		default:
			throw new UnsupportedOperationException(
					"This measure is not supported yet!");
		}
	}

	private String getCurrentTrainingTime() {
		long SECOND = 1000;
		long MINUTE = SECOND * 60;
		long HOUR = MINUTE * 60;
		long DAY = HOUR * 24;

		long time = System.currentTimeMillis() - trainingStartTime;
		String result = "";

		if (time > DAY) {
			result += "" + (time / DAY) + "d";
			time = time % DAY;
		}
		if (result.length() != 0 || time > HOUR) {

			result += "" + String.format("%02d", time / HOUR) + ":";
			time = time % HOUR;
		}
		if (result.length() != 0 || time > MINUTE) {
			result += "" + String.format("%02d", time / MINUTE) + ":";
			time = time % MINUTE;
		}
		if (result.length() != 0 || time > SECOND) {
			result += "" + String.format("%02d", time / SECOND);
			time = time % SECOND;
		}
		result += "." + String.format("%03d", time);

		return result;
	}

	public void addLogListener(PrintStream stream) {
		infoLog.addStreamListener(stream);
	}

	public TDLearning train(int verbosity) {
		trainingStartTime = System.currentTimeMillis();

		//
		// Write the Column-names into the logger
		//
		infoLog.logColumnNames("GameNum", par.trackInfoMeasures);
		System.gc();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TDLearning tdl = new TDLearning(par);
		while (tdl.getGameNum() <= par.numGames) {
			//
			// Perform one Training-match
			//
			int gameNum = tdl.getGameNum();
			tdl.trainNet();

			//
			// Get statistics, if required
			//
			if (par.infoInterval.infoNecessary(gameNum)) {
				MeasurePoint measure = trackMeasures(tdl);
				showDebugInfo(tdl, verbosity);
				infoLog.put(gameNum, measure);
			}
		}
		return tdl;
	}

	/**
	 * Show different information during the training-process depending on the
	 * verbosity.
	 * 
	 * @param verbosity
	 */
	private void showDebugInfo(TDLearning tdl, int verbosity) {
		switch(verbosity) {
		case 1: // print the values of a few random boards to the console
			debugPrintBoardValues(tdl);
			break;
		}

	}

	private void debugPrintBoardValues(TDLearning tdl) {
		//
		// First, for an empty board
		//
		TrainingParams par = tdl.getTrainingParams();
		int m = par.m;
		int n = par.n;
		GameState s = new GameState(m,n);
		double v = tdl.getValue(s);
		List<Move>bestMoves = tdl.getBestMoves(s);
		System.out.println("Empty-Board: ");
		System.out.println("TDL-Value: " + v);
		System.out.println("TDL Best move: " + bestMoves);
		System.out.println(s);
		
		for(Move mv : s) {
			s.advance(mv);
			v = tdl.getValue(s);
			bestMoves = tdl.getBestMoves(s);
			System.out.println("Board with one line:");
			System.out.println("TDL-Value: " + v);
			System.out.println("TDL Best moves: " + bestMoves);
			System.out.println(s);
			System.out.println();
			s.undo();
		}
		
		//
		// Some random positions with 2 lines
		//
		for(int i=0;i<10;i++) {
			s.advance(true);
			s.advance(true);
			v = tdl.getValue(s);
			bestMoves = tdl.getBestMoves(s);
			System.out.println("Board with one line:");
			System.out.println("TDL-Value: " + v);
			System.out.println("TDL Best moves: " + bestMoves);
			System.out.println(s);
			System.out.println();
			s.undo();
			s.undo();
		}
		
	}

	// public static void main(String[] args) {
	// // with board inversion...
	// int m = 1, n = 2;
	// TDParams td = new TDParams();
	// td.activation = Activation.NONE;
	// td.epsilonFinal = 0.1;
	// td.epsilonInit = 0.1;
	// td.gamma = 1.0;
	// td.alphaInit = 0.01;
	// td.randValueFunctionInit = false;
	// td.useBoardInversion = true; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// td.useImmediateRewards = true;
	//
	// NTupleParams nPar = new NTupleParams();
	// nPar.randomLength = false;
	// nPar.useGenerationAlgo = true;
	// nPar.tupleGen = TupleGeneration.RAND_WALK;
	// nPar.tupleLen = 7;
	// nPar.tupleNum = 1;
	// nPar.useIndexHashTable = true;
	// nPar.useSymmetricHashing = true;
	// nPar.useSymmetry = true;
	// nPar.useCornerSymmetry = true; // ?? turn off??
	//
	// nPar.numLUTSets = 1;
	// nPar.lutSelection = null;
	//
	// TrainingParams par = new TrainingParams();
	// par.m = m;
	// par.n = n;
	// par.numGames = 2000000;
	// par.infoInterval = new InfoInterval(0, 1000, par.numGames);
	// par.trackInfoMeasures = new TrackedInfoMeasures(
	// InfoMeasures.GLOBAL_ALPHA, InfoMeasures.EPSILON,
	// InfoMeasures.SUCESSRATE);
	// par.nPar = nPar;
	// par.tdPar = td;
	//
	// // Generate n-tuples
	// if (nPar.useGenerationAlgo)
	// nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
	// par.n);
	//
	// //
	// // Start a training-environment
	// //
	// TrainingEnvironment te = new TrainingEnvironment(par);
	// te.addLogListener(System.out);
	// te.train(0);
	// }

}
