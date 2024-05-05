import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for analyzing code complexity of methods.
 */
public class CodeComplexityAnalyzer {

	public enum JavaConditionals {
		IF_STATEMENT("\\bif\\s*\\([^)]*\\)"),
		ELSE_STATEMENT("\\belse(?:\\s+|\\{)"),
		ELSE_IF_STATEMENT("\\belse\\s+if\\s*\\([^)]*\\)"),
		SWITCH_CASE("\\bcase\\s+[^:]+:"),
		SWITCH_DEFAULT("default"),
		FOR_LOOP("\\bfor\\s*\\([^)]*\\)"),
		WHILE_LOOP("\\bwhile\\s*\\([^)]*\\)"),
		TERNARY_OPERATOR("[?][^?]+:[^?]+");

		private final Pattern codePattern;
		JavaConditionals (String codeString) {
			this.codePattern = Pattern.compile(codeString);
		}

		/**
		 * Get the pattern associated with the conditional.
		 *
		 * @return The pattern.
		 */
		public Pattern getCodePattern() {
			return codePattern;
		}
	}

	/**
	 * Evaluates the complexity of a list of functions.
	 * NOTE: this function skips functions with complexity 0
	 * e.g. if the resultLength is 3 but there are only 2 functions with complexity > 0, then the resulting list
	 * will have the size of 2.
	 * @param functions    The list of functions to analyze.
	 * @param resultLength The length of the result list.
	 * @return A list of pairs containing method names and their complexity, sorted from highest to lowest complexity.
	 */

	public static List<Pair<String, Integer>> evaluateComplexity(List<Function> functions, int resultLength) {
		return functions.stream()
				.map(CodeComplexityAnalyzer::evaluateComplexityOfAMethod)
				       .filter(pair -> !pair.second().equals(0))
				.sorted((pair1, pair2) -> -Integer.compare(pair1.second(), pair2.second()))
				.limit(resultLength)
				.collect(Collectors.toList());
	}

	//TODO: document why i decided to count if, else, else if separately
	/**
	 * Evaluates the complexity of a single method.
	 *
	 * @param methodToAnalyze The method to analyze.
	 * @return A pair containing the method name and its complexity.
	 */
	protected static Pair<String, Integer> evaluateComplexityOfAMethod (Function methodToAnalyze) {
		String codeToAnalyze = methodToAnalyze.body();
		int complexity = 0;

		for(JavaConditionals conditional: JavaConditionals.values()) {
			Pattern pattern = conditional.getCodePattern();
			Matcher matcher = pattern.matcher(codeToAnalyze);
			int count = 0;
			while(matcher.find()) count++;

			//add 'if' and 'else' occurencies, substract 'else if' to eliminate duplicate counts
			if(conditional == JavaConditionals.ELSE_IF_STATEMENT) complexity-= count;
			//ternary operator creates 2 paths for the program kind of, the same way as switch cases or elses, so I count them as complexity of 2
			else if(conditional == JavaConditionals.TERNARY_OPERATOR) complexity += 2*count;
			else complexity+=count;
		}

		return new Pair<>(methodToAnalyze.name(), complexity);
	}
}