package multiTraining;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import clone.DeepCopy;
import nTupleTD.TDParams;
import nTupleTD.TrainingParams;
import nTupleTD.TrainingParams.EvaluationPlayAs;

public class MultiTrainingFactory {
	// TODO: Create examples of different experiment setups (grid, simple
	// search) in presentation

	// /**
	// * Create a copy of The parameter {@link TrainingParams} par, but replace
	// * the value of a certain field (given by replace).
	// *
	// * @param par
	// * Trainings-parameters, that will be copied. The value of one
	// * field will be replaced by the next parameter.
	// * @param replace
	// * Replace the value of one field by this given value.
	// * @return A copy of the Trainings-parameters, with one adjusted field.
	// */
	// public static TrainingParams generateTrainingParams(TrainingParams par,
	// Parameter replace) {
	// TrainingParams newPar = (TrainingParams) DeepCopy.copy(par);
	//
	// //
	// // Now set the value of the corresponding field to the new value
	// //
	//
	// Field field;
	// Field subObject = null;
	// try {
	// // Get the subobject (e.g. "tdPar"), if existant
	// if (replace.subObject != null && !replace.subObject.isEmpty()) {
	// subObject = TrainingParams.class
	// .getDeclaredField(replace.subObject);
	// }
	//
	// // Get the class of the subobject. If there is no sub-object, the
	// // class is TrainingParams
	// Class<?> myClass = (subObject != null ? subObject.getType()
	// : TrainingParams.class);
	//
	// // Get the field, that we want to adjust
	// field = myClass.getDeclaredField(replace.parameterName);
	//
	// // Adjust the field in our new TrainingParams object
	// if (subObject != null)
	// field.set(subObject.get(newPar), replace.value);
	// else
	// field.set(newPar, replace.value);
	// } catch (NoSuchFieldException | IllegalArgumentException
	// | IllegalAccessException e) {
	// e.printStackTrace();
	// }
	//
	// return newPar;
	// }

	/**
	 * Copies the given Training-parameters {@link TrainingParams} par, but
	 * replaces a list of fields by new values.
	 * 
	 * @param par
	 *            Training-parameters, that will be copied. The value of several
	 *            fields will be replaced.
	 * @param experiments
	 *            Replacements for some of the Training-Parameters.
	 * @return A copy of the Training-parameters, with several adjusted fields.
	 */
	private static TrainingParams generateSingleTrainingParams(
			TrainingParams par, List<Parameter> parameterSet) {
		TrainingParams newPar = (TrainingParams) DeepCopy.copy(par);

		//
		// Iterate through all fields, where the values have to be replaced by
		// new ones.
		//
		for (Parameter parameter : parameterSet) {
			//
			// Now set the value of the corresponding field to the new value
			//
			Field field;
			Field subObject = null;
			try {
				// Get the subobject (e.g. "tdPar"), if existant
				if (parameter.subObject != null
						&& !parameter.subObject.isEmpty()) {
					subObject = TrainingParams.class
							.getDeclaredField(parameter.subObject);
				}

				// Get the class of the subobject. If there is no sub-object,
				// the
				// class is TrainingParams
				Class<?> myClass = (subObject != null ? subObject.getType()
						: TrainingParams.class);

				//
				// In some cases it can happen, that the variable contains an
				// instance of a subclass of the variable's class. Therefore,
				// determine the real class.
				//
				Class<?> realClass;
				if (subObject != null) {
					Object o = subObject.get(newPar);
					realClass = o.getClass();
				}
				else 
					realClass = TrainingParams.class;

				// Get the field, that we want to adjust
				field = realClass.getDeclaredField(parameter.parameterName);

				// Adjust the field in our new TrainingParams object
				if (subObject != null)
					field.set(subObject.get(newPar), parameter.value);
				else
					field.set(newPar, parameter.value);
			} catch (NoSuchFieldException | IllegalArgumentException
					| IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return newPar;
	}

	public static ExperimentList simpleSearch(List<List<Parameter>> paramLists) {
		//
		// The length of lists has to be the same
		//
		for (int i = 0; i < paramLists.size() - 1; i++)
			if (paramLists.get(i).size() != paramLists.get(i + 1).size())
				throw new UnsupportedOperationException(
						"The length of all parameter-lists have to be equal");

		//
		// Now iterate through all parameter-lists and combine the corresponding
		// parameters
		//
		int numParams = paramLists.get(0).size();
		// List<List<Parameter>> paramSimpleSearch = new ArrayList<>(numParams);
		ExperimentList experiments = new ExperimentList();

		for (int i = 0; i < numParams; i++) {
			//
			// The parameters for the individual fields are combined
			//
			ArrayList<Parameter> paramCombi = new ArrayList<>(paramLists.size());
			for (int j = 0; j < paramLists.size(); j++) {
				Parameter p = paramLists.get(j).get(i);
				paramCombi.add(p);
			}
			experiments.addExperiment(new Experiment(paramCombi));
		}

		return experiments;
	}

	public static ExperimentList simpleSearch1(List<ExperimentList> experiments) {
		//
		// The length of lists has to be the same
		//
		for (int i = 0; i < experiments.size() - 1; i++)
			if (experiments.get(i).size() != experiments.get(i + 1).size())
				throw new UnsupportedOperationException(
						"The length of all parameter-lists have to be equal");

		//
		// Now iterate through all parameter-lists and combine the corresponding
		// parameters
		//
		int numParams = experiments.get(0).size();
		// List<List<Parameter>> paramSimpleSearch = new ArrayList<>(numParams);
		ExperimentList experimentList = new ExperimentList();

		for (int i = 0; i < numParams; i++) {
			//
			// The parameters for the individual fields are combined
			//
			ArrayList<Parameter> paramCombi = new ArrayList<>(
					experiments.size());
			for (int j = 0; j < experiments.size(); j++) {
				List<Parameter> p = experiments.get(j).get(i);
				paramCombi.addAll(p);
			}
			experimentList.addExperiment(new Experiment(paramCombi));
		}

		return experimentList;
	}

	public static List<TrainingParams> generateTrainingParams(
			TrainingParams par, ExperimentList experiments) {

		//
		// Generate all the Training-Params
		//
		List<TrainingParams> tp = new ArrayList<>(experiments.size());
		for (Experiment i : experiments) {
			TrainingParams t = generateSingleTrainingParams(par, i.get());
			tp.add(t);
		}

		return tp;
	}

	public static ExperimentList mergeUnrelatedExperiments1(
			List<ExperimentList> parameters) {
		//
		// We have a list of experiment-lists. Combine all these lists to
		// one list so that we only have one table, where every index
		// represents one experiment.
		//
		ExperimentList expList = new ExperimentList();
		//
		// Iterate through all experiment-tables and merge them to one
		// table.
		//
		for (ExperimentList i : parameters) {
			expList.addAllExperiments(i);
		}
		return expList;
	}

	public static ExperimentList gridSearch1(List<ExperimentList> experiments) {
		//
		// Number of values for each parameter
		//
		int numParameters = experiments.size();
		List<Integer> numValuesParameter = new ArrayList<>(numParameters);
		for (int i = 0; i < numParameters; i++) {
			numValuesParameter.add(i, experiments.get(i).size());
		}

		//
		// Get the Grid
		//
		List<List<Integer>> grid = MultiTrainingFactory
				.createGrid(numValuesParameter);

		//
		// Create the parameter-settings based on the selected grid.
		//
		return getParameterGrid1(experiments, grid);
	}

	private static ExperimentList getParameterGrid1(
			List<ExperimentList> experimentsList, List<List<Integer>> grid) {
		// List<List<Parameter>> parameterGrid = new ArrayList<>(grid.size());
		ExperimentList experiments = new ExperimentList(grid.size());
		//
		// Iterate through the grid and create all the parameter combinations.
		// This is basically the Cartesian product of all parameter-lists
		//
		for (List<Integer> i : grid) {
			//
			// Create a new parameter-combination
			//
			ArrayList<Parameter> paramCombi = new ArrayList<>(i.size());

			//
			// From every parameter-list get the corresponding element according
			// to i
			//
			int k = 0;
			for (Integer j : i) {
				List<Parameter> p = experimentsList.get(k).get(j);
				paramCombi.addAll(p);
				k++;
			}

			//
			// Add this new param-combination to our parameter-grid
			//
			experiments.addExperiment(new Experiment(paramCombi));
		}
		return experiments;
	}

	public static ExperimentList gridSearch(List<List<Parameter>> paramLists) {
		//
		// Number of values for each parameter
		//
		int numParameters = paramLists.size();
		List<Integer> numValuesParameter = new ArrayList<>(numParameters);
		for (int i = 0; i < numParameters; i++) {
			numValuesParameter.add(i, paramLists.get(i).size());
		}

		//
		// Get the Grid
		//
		List<List<Integer>> grid = MultiTrainingFactory
				.createGrid(numValuesParameter);

		//
		// Create the parameter-settings based on the selected grid.
		//
		return getParameterGrid(paramLists, grid);
	}

	private static ExperimentList getParameterGrid(
			List<List<Parameter>> paramLists, List<List<Integer>> grid) {
		// List<List<Parameter>> parameterGrid = new ArrayList<>(grid.size());
		ExperimentList experiments = new ExperimentList(grid.size());
		//
		// Iterate through the grid and create all the parameter combinations.
		// This is basically the Cartesian product of all parameter-lists
		//
		for (List<Integer> i : grid) {
			//
			// Create a new parameter-combination
			//
			ArrayList<Parameter> paramCombi = new ArrayList<>(i.size());

			//
			// From every parameter-list get the corresponding element according
			// to i
			//
			int k = 0;
			for (Integer j : i) {
				Parameter p = paramLists.get(k).get(j);
				paramCombi.add(p);
				k++;
			}

			//
			// Add this new param-combination to our parameter-grid
			//
			experiments.addExperiment(new Experiment(paramCombi));
		}
		return experiments;
	}

	private static List<List<Integer>> createGrid(
			List<Integer> numValuesParameter) {
		return createGrid(numValuesParameter, 0);
	}

	private static List<List<Integer>> createGrid(
			List<Integer> numValuesParameter, int index) {
		List<List<Integer>> myList = new ArrayList<List<Integer>>();
		List<List<Integer>> oldList;
		if (index >= numValuesParameter.size())
			return new ArrayList<List<Integer>>();

		oldList = createGrid(numValuesParameter, index + 1);

		int myNum = numValuesParameter.get(index);
		for (int i = 0; i < myNum; i++) {
			//
			// Add this current value
			//
			for (List<Integer> j : oldList) {
				List<Integer> newRow = new ArrayList<Integer>(j);
				newRow.add(0, i);
				myList.add(newRow);
			}
			if (oldList.isEmpty()) {
				List<Integer> newRow = new ArrayList<>();
				newRow.add(i);
				myList.add(newRow);
			}

		}
		return myList;
	}

	public static List<Parameter> generateParamList(String subObject,
			String field, Object[] values) {
		List<Parameter> paramList = new ArrayList<>(values.length);
		for (Object o : values) {
			Parameter p = new Parameter(subObject, field, o);
			paramList.add(p);
		}
		return paramList;
	}
	
	// Make logarithmic steps
	public static List<Parameter> generateParamListLog(String subObject,
			String field, double startWith, double step, double end) {

		List<Object> values = new ArrayList<>();
		for (double i = startWith; i <= end; i += step) {
			values.add(Math.log(i));
		}
		return generateParamList(subObject, field,
				values.toArray(new Double[values.size()]));
	}

	public static List<Parameter> generateParamList(String subObject,
			String field, double startWith, double step, double end) {

		List<Object> values = new ArrayList<>();
		for (double i = startWith; i <= end; i += step) {
			values.add(i);
		}
		return generateParamList(subObject, field,
				values.toArray(new Double[values.size()]));
	}

	public static List<Parameter> generateParamList(String subObject,
			String field, short startWith, short step, short end) {
		List<Object> values = new ArrayList<>();
		for (int i = startWith; i <= end; i += step) {
			values.add(i);
		}
		return generateParamList(subObject, field,
				values.toArray(new Integer[values.size()]));
	}

	public static List<Parameter> generateParamList(String subObject,
			String field, int startWith, int step, int end) {
		List<Object> values = new ArrayList<>();
		for (int i = startWith; i <= end; i += step) {
			values.add(i);
		}
		return generateParamList(subObject, field,
				values.toArray(new Integer[values.size()]));
	}

	public static List<Parameter> generateParamList(String subObject,
			String field, long startWith, long step, long end) {
		List<Object> values = new ArrayList<>();
		for (long i = startWith; i <= end; i += step) {
			values.add(i);
		}
		return generateParamList(subObject, field,
				values.toArray(new Long[values.size()]));
	}

	public static List<Parameter> generateParamList(String subObject,
			String field, float startWith, float step, float end) {
		List<Object> values = new ArrayList<>();
		for (float i = startWith; i <= end; i += step) {
			values.add(i);
		}
		return generateParamList(subObject, field,
				values.toArray(new Float[values.size()]));
	}

	@SafeVarargs
	public static List<List<Parameter>> mergeParamLists(
			List<Parameter>... params) {
		List<List<Parameter>> paramLists = new ArrayList<>(params.length);
		// Creates a table in the form
		// params[0] | params[1] | params[2] | ...
		//
		for (int i = 0; i < params.length; i++) {
			paramLists.add(params[i]);
		}
		return paramLists;
	}

	/**
	 * Just some Tests.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Field[] fields = TrainingParams.class.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			System.out.println(fields[i]);
		}

		TrainingParams tp = new TrainingParams();
		tp.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;

		// Get evaluation enum
		Field field;

		// Try Enum: Fine!
		System.out.println("Before: " + tp.evaluationPlayAs);
		try {
			field = TrainingParams.class.getDeclaredField("evaluationPlayAs");
			field.set(tp, TrainingParams.EvaluationPlayAs.PLAY_AS_A);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.println("After: " + tp.evaluationPlayAs);

		// Try Primitive: Also works fine. Gut Gut!!
		tp.numGames = -1;
		System.out.println("Before: " + tp.numGames);
		try {
			field = TrainingParams.class.getDeclaredField("numGames");
			field.set(tp, Integer.MAX_VALUE);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.println("After: " + tp.numGames);

		// Try to find a variable in TDParams:
		tp.tdPar = new TDParams();
		tp.tdPar.alphaFinal = Double.POSITIVE_INFINITY;
		System.out.println("Before: " + tp.tdPar.alphaFinal);

		Field subObject;
		try {
			subObject = TrainingParams.class.getDeclaredField("tdPar");
			Class<?> myClass = subObject.getType();
			field = myClass.getDeclaredField("alphaFinal");
			field.set(subObject.get(tp), Double.NaN);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.println("After: " + tp.tdPar.alphaFinal);

		// Test deepCopy
		tp.tdPar.alphaInit = -99999999999.999999;
		System.out.println("alphaInit before clone: " + tp.tdPar.alphaInit);
		TrainingParams newPar = (TrainingParams) DeepCopy.copy(tp);
		newPar.tdPar = new TDParams();
		System.out.println("alphaInit after clone: " + tp.tdPar.alphaInit);

		newPar.tdPar.alphaInit = 0.1234567;
		System.out.println("alphaInit of clone: " + newPar.tdPar.alphaInit);

		//
		// Test Grid-search for several tables with more than one column
		//
		List<Parameter> a1 = generateParamList("tdPar", "alpha_init", 0.01,
				0.01, 0.1);
		List<Parameter> a2 = generateParamList("tdPar", "alpha_final", 0.01,
				0.01, 0.1);
		List<List<Parameter>> a = mergeParamLists(a1, a2);
		ExperimentList expA = simpleSearch(a);
		// epsilon
		List<Parameter> e1 = generateParamList("tdPar", "epsilon_init", 0.1,
				0.1, 0.9);
		List<Parameter> e2 = generateParamList("tdPar", "epsilon_final", 0.1,
				0.1, 0.9);
		List<List<Parameter>> e = mergeParamLists(e1, e2);
		ExperimentList expE = simpleSearch(e);
		// gamma
		List<Parameter> g1 = generateParamList("tdPar", "gamma", 0.5, 0.1, 1.0);
		List<List<Parameter>> g = mergeParamLists(g1);
		ExperimentList expG = simpleSearch(g);

		// TODO: Merge-Experiments method required
		ArrayList<ExperimentList> dd = new ArrayList<>();
		dd.add(expA);
		dd.add(expE);
		dd.add(expG);
		ExperimentList grid = gridSearch1(dd);
		System.out.println(grid);
	}
}
