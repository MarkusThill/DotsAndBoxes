package tests;

import java.util.ArrayList;
import java.util.List;

import multiTraining.ExperimentList;
import multiTraining.MultiTrainingFactory;
import multiTraining.Parameter;

import org.junit.Test;


public class MultiTrainingFactoryTest {

	// @Test
	// public void testGenerateTrainingParams() {
	// TrainingParams tp = new TrainingParams();
	// tp.tdPar = new TDParams();
	// tp.nPar = new NTupleParams();
	//
	// Parameter p = new Parameter("tdPar", "alphaInit", 0.12345);
	//
	// //
	// TrainingParams newtp = MultiTrainingFactory.generateTrainingParams(tp,
	// p);
	// assertEquals(0.12345, newtp.tdPar.alphaInit, 0.000001);
	//
	// //
	// p = new Parameter(null, "evaluationPlayAs",
	// TrainingParams.EvaluationPlayAs.PLAY_AS_B);
	// newtp = MultiTrainingFactory.generateTrainingParams(tp, p);
	// assertEquals(newtp.evaluationPlayAs,
	// TrainingParams.EvaluationPlayAs.PLAY_AS_B);
	//
	// //
	// p = new Parameter("nPar", "useSymmetricHashing", true);
	// newtp = MultiTrainingFactory.generateTrainingParams(tp, p);
	// assertEquals(newtp.nPar.useSymmetricHashing, true);
	// }

//	@Test
//	public void testGenerateTrainingParams2() {
//		TrainingParams tp = new TrainingParams();
//		tp.tdPar = new TDParams();
//		tp.nPar = new NTupleParams();
//
//		ArrayList<Parameter> al = new ArrayList<>();
//		Parameter p = new Parameter("tdPar", "alphaInit", 0.12345);
//		al.add(p);
//		p = new Parameter(null, "evaluationPlayAs",
//				TrainingParams.EvaluationPlayAs.PLAY_AS_B);
//		al.add(p);
//		p = new Parameter("nPar", "useSymmetricHashing", true);
//		al.add(p);
//		TrainingParams newtp = MultiTrainingFactory.generateTrainingParams(tp,
//				al);
//
//		assertEquals(0.12345, newtp.tdPar.alphaInit, 0.000001);
//		assertEquals(newtp.evaluationPlayAs,
//				TrainingParams.EvaluationPlayAs.PLAY_AS_B);
//		assertEquals(newtp.nPar.useSymmetricHashing, true);
//
//	}

//	@Test
//	public void testCreateGrid() {
//		ArrayList<Integer> numValuesParameter = new ArrayList<>();
//		numValuesParameter.add(3);
//		numValuesParameter.add(4);
//		numValuesParameter.add(5);
//
//		List<List<Integer>> l = MultiTrainingFactory
//				.createGrid(numValuesParameter);
//		System.out.println(l);
//	}

//	@Test
//	public void testGetParameterGrid() {
//		ArrayList<Parameter> alpha = new ArrayList<>();
//		Parameter p = new Parameter("tdPar", "alphaInit", 0.1);
//		alpha.add(p);
//		p = new Parameter("tdPar", "alphaInit", 0.2);
//		alpha.add(p);
//		p = new Parameter("tdPar", "alphaInit", 0.3);
//		alpha.add(p);
//		p = new Parameter("tdPar", "alphaInit", 0.4);
//		alpha.add(p);
//
//		ArrayList<Parameter> epsilon = new ArrayList<>();
//		p = new Parameter("tdPar", "epsilonInit", 0.5);
//		epsilon.add(p);
//		p = new Parameter("tdPar", "epsilonInit", 0.6);
//		epsilon.add(p);
//		p = new Parameter("tdPar", "epsilonInit", 0.7);
//		epsilon.add(p);
//		p = new Parameter("tdPar", "epsilonInit", 0.8);
//		epsilon.add(p);
//
//		List<List<Parameter>> parameters = new ArrayList<>();
//		parameters.add(alpha);
//		parameters.add(epsilon);
//
//		List<Integer> numValuesParameter = new ArrayList<>(2);
//		numValuesParameter.add(4);
//		numValuesParameter.add(4);
//		List<List<Integer>> grid = MultiTrainingFactory
//				.createGrid(numValuesParameter);
//		System.out.println("Grid: ");
//		System.out.println(grid);
//
//		ExperimentList parameterGrid = MultiTrainingFactory
//				.getParameterGrid(parameters, grid);
//
//		System.out.println("Parameter-Grid: ");
//		System.out.println(parameterGrid);
//
//	}

//	@Test
//	public void testGridSearch() {
//		ArrayList<Parameter> alpha = new ArrayList<>();
//		Parameter p = new Parameter("tdPar", "alphaInit", 0.1);
//		alpha.add(p);
//		p = new Parameter("tdPar", "alphaInit", 0.2);
//		alpha.add(p);
//		p = new Parameter("tdPar", "alphaInit", 0.3);
//		alpha.add(p);
//		p = new Parameter("tdPar", "alphaInit", 0.4);
//		alpha.add(p);
//
//		ArrayList<Parameter> epsilon = new ArrayList<>();
//		p = new Parameter("tdPar", "epsilonInit", 0.5);
//		epsilon.add(p);
//		p = new Parameter("tdPar", "epsilonInit", 0.6);
//		epsilon.add(p);
//		p = new Parameter("tdPar", "epsilonInit", 0.7);
//		epsilon.add(p);
//		p = new Parameter("tdPar", "epsilonInit", 0.8);
//		epsilon.add(p);
//
//		List<List<Parameter>> parameters = new ArrayList<>();
//		parameters.add(alpha);
//		parameters.add(epsilon);
//
//		TrainingParams par = new TrainingParams();
//		par.tdPar = new TDParams();
//		par.nPar = new NTupleParams();
//		List<TrainingParams> tp = MultiTrainingFactory.gridSearch(par,
//				parameters);
//
//		assertEquals(tp.size(), alpha.size() * epsilon.size());
//
//		// First entry
//		assertEquals(0.1, tp.get(0).tdPar.alphaInit, 0.00001);
//		assertEquals(0.1, tp.get(1).tdPar.alphaInit, 0.00001);
//		assertEquals(0.1, tp.get(2).tdPar.alphaInit, 0.00001);
//		assertEquals(0.1, tp.get(3).tdPar.alphaInit, 0.00001);
//		assertEquals(0.2, tp.get(4).tdPar.alphaInit, 0.00001);
//		assertEquals(0.2, tp.get(5).tdPar.alphaInit, 0.00001);
//		assertEquals(0.2, tp.get(6).tdPar.alphaInit, 0.00001);
//		assertEquals(0.2, tp.get(7).tdPar.alphaInit, 0.00001);
//		assertEquals(0.3, tp.get(8).tdPar.alphaInit, 0.00001);
//		assertEquals(0.3, tp.get(9).tdPar.alphaInit, 0.00001);
//		assertEquals(0.3, tp.get(10).tdPar.alphaInit, 0.00001);
//		assertEquals(0.3, tp.get(11).tdPar.alphaInit, 0.00001);
//		assertEquals(0.4, tp.get(12).tdPar.alphaInit, 0.00001);
//		assertEquals(0.4, tp.get(13).tdPar.alphaInit, 0.00001);
//		assertEquals(0.4, tp.get(14).tdPar.alphaInit, 0.00001);
//		assertEquals(0.4, tp.get(15).tdPar.alphaInit, 0.00001);
//
//		assertEquals(0.5, tp.get(0).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.6, tp.get(1).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.7, tp.get(2).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.8, tp.get(3).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.5, tp.get(4).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.6, tp.get(5).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.7, tp.get(6).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.8, tp.get(7).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.5, tp.get(8).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.6, tp.get(9).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.7, tp.get(10).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.8, tp.get(11).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.5, tp.get(12).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.6, tp.get(13).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.7, tp.get(14).tdPar.epsilonInit, 0.00001);
//		assertEquals(0.8, tp.get(15).tdPar.epsilonInit, 0.00001);
//
//	}

	@Test
	public void generateParamList() {
		List<Parameter> x = MultiTrainingFactory.generateParamList("tdPar",
				"epsilonInit", 0.1, 0.1, 1.0);
		
		System.out.println(x);
	}
	
	@Test
	public void generateParamList2() {
		List<Parameter> x = MultiTrainingFactory.generateParamList("",
				"numGames", 0, 1000, 10000);
		
		System.out.println(x);
	}
	
	@Test
	public void generateParamList3() {
		Double[] values = new Double[] {0.1, 0.2, 0.3, 0.4};
		List<Parameter> x = MultiTrainingFactory.generateParamList("tdPar",
				"epsilonInit", values);
		
		System.out.println(x);
	}
	
	@Test
	public void testGetParametersCombined() {
		List<Parameter> eps = MultiTrainingFactory.generateParamList("tdPar",
				"epsilonInit", 0.0, 0.1, 1.0);
		List<Parameter> games = MultiTrainingFactory.generateParamList("",
				"numGames", 0, 1000, 10000);
		List<List<Parameter>> paramLists = new ArrayList<>();
		System.out.println("Size Eps: " + eps.size());
		System.out.println("Size Games: " + games.size());
		paramLists.add(eps);
		paramLists.add(games);
		ExperimentList x = MultiTrainingFactory.simpleSearch(paramLists);
		System.out.println(x);
	}
	
	@Test
	public void testMergeParamLists() {
		List<Parameter> eps = MultiTrainingFactory.generateParamList("tdPar",
				"epsilonInit", 0.0, 0.1, 1.0);
		List<Parameter> games = MultiTrainingFactory.generateParamList("",
				"numGames", 0, 1000, 10000);
		List<List<Parameter>> paramLists = MultiTrainingFactory.mergeParamLists(eps, games);
		ExperimentList x = MultiTrainingFactory.simpleSearch(paramLists);
		System.out.println(x);
	}

}
