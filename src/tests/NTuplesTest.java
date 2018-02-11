package tests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import nTupleTD.NTupleFactory;
import nTupleTD.NTupleFactory.SamplePoint;
import nTupleTD.NTupleParams;
import nTupleTD.NTupleParams.LUTSelection;
import nTupleTD.TDLearning;
import nTupleTD.TDParams;
import nTupleTD.TrainingParams;
import nTupleTD.NTupleParams.TupleGeneration;
import nTupleTD.NTuplePoint;
import nTupleTD.NTuples;

import org.junit.Test;

import adaptableLearningRates.ActivationFunction.Activation;
import agentEvaluation.Evaluation;

import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateSnapshot;
import dotsAndBoxes.Move;

public class NTuplesTest {

	@Test
	public void test3x3BoardImmediateRewardsBI_XN_Tuples() {
		// with board inversion...
		int m = 3, n = 3;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.0005;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 8;
		nPar.tupleNum = 150;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;
		nPar.useCornerSymmetry = true; // ?? turn off??

		nPar.numLUTSets = 1;
		nPar.lutSelection = null;

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 2000000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));
	}

	@Test
	public void test2x3BoardImmediateRewardsBI_XN_Tuples() {
		// with board inversion...
		int m = 2, n = 3;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.001;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 8;
		nPar.tupleNum = 100;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;
		nPar.useCornerSymmetry = true; // ?? turn off??

		nPar.numLUTSets = 1;
		nPar.lutSelection = null;

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 2000000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));
	}

	@Test
	public void test2x3BoardImmediateRewardsBI() {
		// with board inversion...
		int m = 2, n = 3;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.01;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 17;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = false;
		nPar.useSymmetry = true;
		nPar.useCornerSymmetry = true; // ?? turn off??

		nPar.numLUTSets = 1;
		nPar.lutSelection = null;

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 200000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));
	}

	@Test
	public void test2x2BoardImmediateRewardsBI() {
		// with board inversion...
		int m = 2, n = 2;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.01;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 12;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = false;
		nPar.useSymmetry = true;

		nPar.numLUTSets = 1;
		nPar.lutSelection = null;

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 20000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));

	}

	@Test
	public void test1x2BoardImmediateRewardsBI() {
		// with board inversion...
		int m = 1, n = 2;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.03;
		td.randValueFunctionInit = false;
		td.useBoardInversion = true; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = false;
		nPar.useSymmetry = true;
		nPar.useCornerSymmetry = false; // TODO: THERE IS A PROBLEM, WHEN USING
										// CORNER-SYMMETRIES. SOMETHING GOES
										// WRONG. THE VALUES ARE NOT ACCURATE
										// ENOUGH!!!!!

		nPar.numLUTSets = 1;
		nPar.lutSelection = null;

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 100000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));

	}

	@Test
	public void test2x3BoardImmediateRewardN_Tuple() {
		int m = 2, n = 3;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.001;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 8;
		nPar.tupleNum = 100;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;
		nPar.useCornerSymmetry = true;

		nPar.numLUTSets = 2;
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 1000000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		System.out.println("Start Evaluation\n\n");
		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));
	}

	@Test
	public void test2x3BoardImmediateReward() {
		//
		// For every box-difference one LUT
		//
		int m = 2, n = 3;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.2;
		td.epsilonInit = 0.2;
		td.gamma = 1.0;
		td.alphaInit = 0.01;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 17;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = false;
		nPar.useSymmetry = true;

		nPar.numLUTSets = 2;
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 1000000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		System.out.println("Start Evaluation\n\n");
		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));
	}

	@Test
	public void test1x5BoardImmediateReward() {
		//
		// For every box-difference one LUT
		//
		int m = 1, n = 5;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.2;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.01;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 16;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = false;
		nPar.useSymmetry = true;

		nPar.numLUTSets = 2;
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 200000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		System.out.println("Start Evaluation\n\n");
		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));
	}

	@Test
	public void test2x2BoardImmediateReward() {
		//
		// For every box-difference one LUT
		//
		int m = 2, n = 2;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.01;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 12;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = true;
		nPar.useSymmetry = true;

		nPar.numLUTSets = 2;
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 20000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		s.advance(new int[] { 0, 0, 0 });
		s.advance(new int[] { 1, 0, 0 });
		s.advance(new int[] { 1, 1, 0 });
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));
		System.out.println("\n\n");

		System.out.println("Start Evaluation\n\n");
		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));
	}

	@Test
	public void test1x2BoardImmediateRewards() {
		int m = 1, n = 2;
		TDParams td = new TDParams();
		td.activation = Activation.NONE;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.01;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;
		td.useImmediateRewards = true;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = false;
		nPar.useSymmetry = true;

		nPar.numLUTSets = 2;
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 100000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));

	}

	@Test
	public void test1x2BoardNoBoxDiff() {
		//
		// For every box-difference one LUT
		//
		int m = 1, n = 2;
		TDParams td = new TDParams();
		td.activation = Activation.TANH;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.01;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = false;
		nPar.useSymmetry = true;

		nPar.numLUTSets = 2;
		nPar.lutSelection = new LUTSelection(0.5f, -0.5f, 0.0f, 0.0f);
		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 100000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 1000 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));

	}

	@Test
	public void test2x2Board() {
		//
		// For every box-difference one LUT
		//
		int m = 2, n = 2;
		TDParams td = new TDParams();
		td.activation = Activation.TANH;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.1;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 12;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = false;
		nPar.useSymmetry = true;

		nPar.numLUTSets = 14;
		nPar.lutSelection = new LUTSelection(6.5f, -3.5f, 1.0f, 0.0f);

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 200000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 100 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));

	}

	@Test
	public void test1x2Board() {
		//
		// For every box-difference one LUT
		//
		int m = 1, n = 2;
		TDParams td = new TDParams();
		td.activation = Activation.TANH;
		td.epsilonFinal = 0.1;
		td.epsilonInit = 0.1;
		td.gamma = 1.0;
		td.alphaInit = 0.1;
		td.randValueFunctionInit = false;
		td.useBoardInversion = false;

		NTupleParams nPar = new NTupleParams();
		nPar.randomLength = false;
		nPar.alwaysGenerateNewNtuples = true;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.tupleLen = 7;
		nPar.tupleNum = 1;
		nPar.useIndexHashTable = true;
		nPar.useSymmetricHashing = false;
		nPar.useSymmetry = true;

		nPar.numLUTSets = 6;
		nPar.lutSelection = new LUTSelection(2.5f, -1.5f, 1.0f, 0.0f);

		TrainingParams par = new TrainingParams();
		par.m = m;
		par.n = n;
		par.nPar = nPar;
		par.numGames = 20000;
		par.tdPar = td;

		// Generate n-tuples
		if (nPar.alwaysGenerateNewNtuples)
			nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
					par.n);

		TDLearning tdl = new TDLearning(par);
		for (int i = 0; i < par.numGames; i++) {
			tdl.trainNet();
			if (i % 100 == 0)
				System.out.println("percent: "
						+ (int) ((float) i / par.numGames * 100.0));
		}

		//
		// print all values of all afterstates..
		//
		GameState s = new GameState(m, n);
		System.out.print(s);
		System.out.println("Value: " + tdl.getValue(s));

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			s.advance(i.next());
			System.out.print(s);
			System.out.println("Value: " + tdl.getValue(s));
			System.out.println("\n\n");
			s.undo();
		}

		Evaluation ev = new Evaluation(m, n, 100);
		System.out.println("Evaluation-Result: " + ev.evaluate(tdl, m, n));

	}

	@Test
	public void testSample() {
		GameState s = new GameState(4, 3);
		NTuplePoint[] tupmasks = NTupleFactory.createNTuple(new int[] { 0, 0,
				0, 0 }, new int[] { 3, 11, 24, 16 });
		NTupleParams nPar = new NTupleParams();
		nPar.nTupleMasks = new NTuplePoint[][] { tupmasks };
		NTuples n = new NTuples(nPar, 4, 3);
		s.advance(0, (byte) 3);
		int x = n.sample(0, s);
		assertEquals(1, x);

		s.advance(0, (byte) 11);
		x = n.sample(0, s);
		assertEquals(1 + 2, x);

		s.advance(0, (byte) 16);
		x = n.sample(0, s);
		assertEquals(1 + 2 + 8, x);

		s.advance(0, (byte) 24);
		x = n.sample(0, s);
		assertEquals(1 + 2 + 4 + 8, x);

	}

	@Test
	public void testHashLookupIndexSet() {
		// reset
		GameState s;
		NTuplePoint[] tupmasks;
		NTuples n;

		s = new GameState(4, 3);
		tupmasks = NTupleFactory.createNTuple(new int[] { 0, 0, 0, 0 },
				new int[] { 3, 11, 24, 16 });
		NTupleParams nPar = new NTupleParams();
		nPar.useCornerSymmetry = true;
		nPar.useSymmetricHashing = true;
		nPar.useIndexHashTable = true;
		nPar.useSymmetry = true;
		nPar.nTupleMasks = new NTuplePoint[][] { tupmasks };
		n = new NTuples(nPar, 4, 3);

		// A hashlookup should fail
		s.advance(0, (byte) 3);
		s.advance(0, (byte) 24);
		int[][] indexSet = n
				.hashLookupIndexSet(s.getMirrorSymmetricSnapshots());
		assertTrue(indexSet == null);

		// Generate the elements now
		indexSet = n.getIndexSet(s.getGameStateSnapshot());
		assertArrayEquals(new int[][] { new int[] { 2, 0, 4, 5, 10 } },
				indexSet);

		// Hash-Lookup should now also find this index-set
		indexSet = n.hashLookupIndexSet(s.getAllSymmetricSnapshots());
		assertArrayEquals(new int[][] { new int[] { 2, 0, 4, 5, 10 } },
				indexSet);

		// Hash-Lookup should also find the Set for symmetric positions
		s = new GameState(4, 3);
		s.advance(0, (byte) 0);
		s.advance(0, (byte) 27);
		indexSet = n.hashLookupIndexSet(s.getAllSymmetricSnapshots());
		assertArrayEquals(new int[][] { new int[] { 2, 0, 4, 5, 10 } },
				indexSet);

		s = new GameState(4, 3);
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 11);
		indexSet = n.hashLookupIndexSet(s.getAllSymmetricSnapshots());
		assertArrayEquals(new int[][] { new int[] { 2, 0, 4, 5, 10 } },
				indexSet);

		s = new GameState(4, 3);
		s.advance(0, (byte) 8);
		s.advance(0, (byte) 19);
		indexSet = n.hashLookupIndexSet(s.getAllSymmetricSnapshots());
		assertArrayEquals(new int[][] { new int[] { 2, 0, 4, 5, 10 } },
				indexSet);
	}

	@Test
	public void testHashPerformance() {
		int numIterations = (int) 10000;
		GameState s;
		NTuplePoint[][] tupmasks;
		NTuples nTup;
		int m = 3, n = 3;
		NTupleParams nPar = new NTupleParams();
		tupmasks = NTupleFactory.createNTupleList(nPar, m, n);
		nPar.nTupleMasks = tupmasks;

		// Warmup
		nTup = new NTuples(nPar, m, n);
		for (int i = 0; i < numIterations / 10; i++) {
			s = new GameState(m, n, false);
			int[][] indexSet = nTup.getIndexSet(s);
			indexSet = nTup.getIndexSet(s);
			while (s.advance(true)) {
				indexSet = nTup.getIndexSet(s);
			}
			if (i % 1000 == 0)
				System.gc();
		}
		// Warmup end

		// Lookups up with symmetries
		nPar.useSymmetricHashing = true;
		nPar.useIndexHashTable = true;
		nTup = new NTuples(nPar, m, n);
		long start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			s = new GameState(m, n, false);
			int[][] indexSet = nTup.getIndexSet(s);
			indexSet = nTup.getIndexSet(s);
			while (s.advance(true)) {
				indexSet = nTup.getIndexSet(s);
			}
			if (i % 1000 == 0)
				System.gc();
		}
		long end = System.nanoTime();
		long time = end - start;

		System.out.println("Time for " + numIterations
				+ " iterations (WITH hashing): " + time / 1e9 + "s");
		System.out.println(nTup.hashStatistics());

		// Now do exactly the same, but without hash-table
		nPar.useSymmetricHashing = false;
		nPar.useIndexHashTable = false;
		nTup = new NTuples(nPar, m, n);
		start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			s = new GameState(m, n, false);
			int[][] indexSet = nTup.getIndexSet(s);
			indexSet = nTup.getIndexSet(s);
			while (s.advance(true)) {
				indexSet = nTup.getIndexSet(s);
			}
			if (i % 1000 == 0)
				System.gc();
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("Time for " + numIterations
				+ " iterations (WITHOUT hashing): " + time / 1e9 + "s");

		// Now do exactly the same, but with do not lookup the symmetric
		// positions
		nPar.useSymmetricHashing = false;
		nPar.useIndexHashTable = true;
		nTup = new NTuples(nPar, m, n);
		start = System.nanoTime();
		for (int i = 0; i < numIterations; i++) {
			s = new GameState(m, n, false);
			int[][] indexSet = nTup.getIndexSet(s);
			indexSet = nTup.getIndexSet(s);
			while (s.advance(true)) {
				indexSet = nTup.getIndexSet(s);
			}
			if (i % 1000 == 0)
				System.gc();
		}
		end = System.nanoTime();
		time = end - start;
		System.out.println("Time for " + numIterations
				+ " iterations (Hashing, WITHOUT Symmetries): " + time / 1e9
				+ "s");

	}

	@Test
	public void testCreateRandomPointsNTuple() throws TimeoutException {
		NTupleParams nPar = new NTupleParams();
		nPar.tupleLen = 10;
		NTuplePoint[] x = NTupleFactory.createNTuple(nPar, 3, 3);
		assertEquals(10, x.length);
		for (int i = 0; i < x.length - 1; i++) {
			// Since n-tuple is sorted
			// make sure, that neighbors are not equal
			assertTrue(x[i].compareTo(x[i + 1]) != 0);
		}

		NTuplePoint[] y = Arrays.copyOf(x, x.length);

		assertTrue(NTuplePoint.equals(x, y));

	}

	@Test
	public void testEquals() throws TimeoutException {
		NTupleParams nPar = new NTupleParams();
		nPar.tupleLen = 10;
		nPar.tupleGen = TupleGeneration.RAND_LINES;
		NTuplePoint[] x = NTupleFactory.createNTuple(nPar, 3, 3);
		assertEquals(10, x.length);
		NTuplePoint[] y = Arrays.copyOf(x, x.length);
		assertTrue(NTuplePoint.equals(x, y));
		assertTrue(NTuplePoint.equals(y, x));

		y[0] = new NTuplePoint(x[0].direction, x[0].mask);
		assertTrue(NTuplePoint.equals(x, y));
		assertTrue(NTuplePoint.equals(y, x));

		// Swap two elements
		NTuplePoint tmp = y[4];
		y[4] = y[8];
		y[8] = tmp;
		assertTrue(NTuplePoint.equals(x, y));
		assertTrue(NTuplePoint.equals(y, x));

		// Shorter n-tuple is not equal
		nPar.tupleLen = 9;
		NTuplePoint[] z = NTupleFactory.createNTuple(nPar, 3, 3);
		assertFalse(NTuplePoint.equals(x, z));
		assertFalse(NTuplePoint.equals(z, x));

		// In 100 runs, most created n-tuples should be unequal with x
		nPar.tupleLen = 10;
		int runs = 1000;
		int counterNEQ = 0;
		for (int i = 0; i < runs; i++) {
			z = NTupleFactory.createNTuple(nPar, 3, 3);
			if (!NTuplePoint.equals(x, z))
				counterNEQ++;
		}
		assertTrue((double) counterNEQ / runs > 0.90);

	}

	@Test
	public void testContains() throws TimeoutException {
		NTupleParams nPar = new NTupleParams();
		nPar.tupleLen = 6;
		NTuplePoint[] x = NTupleFactory.createNTuple(nPar, 3, 3);
		NTuplePoint[] y;
		do {
			y = NTupleFactory.createNTuple(nPar, 3, 3);
		} while (NTuplePoint.equals(x, y));

		ArrayList<NTuplePoint[]> al = new ArrayList<NTuplePoint[]>(10);
		al.add(x);
		assertTrue(NTupleFactory.contains(al, x));

		assertFalse(NTupleFactory.contains(al, y));

		// Find the same point again, but newly generated
		NTuplePoint[] z;
		do {
			z = NTupleFactory.createNTuple(nPar, 3, 3);
		} while (!NTuplePoint.equals(x, z));
		assertTrue(NTupleFactory.contains(al, z));
	}

	@Test
	public void testCreateNTupleListRandomPoints() {
		NTupleParams nPar = new NTupleParams();
		nPar.tupleLen = 10;
		nPar.tupleNum = 100;
		NTuplePoint[][] x = NTupleFactory.createNTupleList(nPar, 3, 3);
		assertEquals(x.length, 100);
		// Dont know what else to do
	}

	@Test
	public void testCreateNTupleRandomWalk() throws TimeoutException {
		int m = 4, n = 3;
		NTupleParams nPar = new NTupleParams();
		nPar.tupleLen = 6;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		NTuplePoint[] x = NTupleFactory.createNTuple(nPar, m, n);
	}

	@Test
	public void createNTupleListRandWalk() {
		int m = 3, n = 3;
		NTupleParams nPar = new NTupleParams();
		nPar.tupleLen = 6;
		nPar.tupleNum = 100;
		nPar.tupleGen = TupleGeneration.RAND_LINE_WALK;
		nPar.randomLength = false;
		NTuplePoint[][] x = NTupleFactory.createNTupleList(nPar, m, n);
		// assertEquals(x.length, 100);

		for (int i = 0; i < x.length; i++) {
			System.out.println(NTupleFactory.toString(x[i], m, n));
		}
	}

}
