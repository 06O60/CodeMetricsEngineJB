import java.util.List;

public class CodeStyleAnalyzer {

	/**
	 * Evaluates the overall code style compliance of a list of functions.
	 *
	 * @param functions The list of functions to evaluate.
	 * @return The percentage of non-compliant methods in the list.
	 * @throws IllegalArgumentException If the list of functions is empty.
	 */
	public static double evaluateCodeStyle(List<Function> functions) {
		if(functions.isEmpty())
			throw new IllegalArgumentException("No methods to evaluate");

		int nonCompliantMethods = 0;
		for(Function f: functions)
			if(! evaluateCodeStyleOfAFunction(f).second())
				nonCompliantMethods++;

		return (100.0 * nonCompliantMethods) / functions.size();
	}

	//TODO: 3. only constructor starts with upper case
	/**
	 * Evaluates the code style compliance of a single function.
	 * Currently it checks if the function name is in camelCase and doesn't contain illegal characters such as "_"
	 * @param functionToAnalyze The function to analyze.
	 * @return A Pair containing the method name and a boolean indicating compliance.
	 */
	public static Pair<String, Boolean> evaluateCodeStyleOfAFunction (Function functionToAnalyze) {
		return new Pair(functionToAnalyze.name(), functionToAnalyze.name().matches("[a-zA-Z][a-zA-Z0-9]*"));
	}
}
