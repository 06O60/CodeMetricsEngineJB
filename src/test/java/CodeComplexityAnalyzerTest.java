import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeComplexityAnalyzerTest {


	@ParameterizedTest
	@CsvSource ({
			"simpleMethod, int x = 10; return x + 5;, 0",  // no conditionals
			"ifMethod, if (x > 0) { return 'positive'; }, 1", //only one if statement
			"ifElseMethod, if (x > 0) { return 'positive'; } else { return 'negative'; }, 2",  // If-else statement
			"elseIfMethod, if (x > 0) { return 'positive'; } else if(x == 0) { return 'zero'; } + " +
					"else { return negative; }, 3", //else if statement
			"forLoopMethod, for (int i = 0; i < n; i++) { sum += i; }, 1",  // For loop
			"whileLoopMethod, while (x > 0) { x--; }, 1",  // While loop
			"nestedLoopMethod, for (int i = 0; i < n; i++) { for (int j = 0; j < m; j++) { count++; } }, 2",  //Nested loops
			"oldSwitchCaseMethod, switch (x) { case 1: return 'One'; case 2: return 'Two'; default: return 'Other'; }, 1",  // Old switch-case method
			"newSwitchCaseMethod, switch (x) { case true -> 'One'; case false -> 'Zero'; }, 1",
			"ternaryOperatorMethod, x > 0 ? 'Positive' : 'Negative', 2"  // Ternary operator method
	})
	public void testEvaluateComplexityOfAMethod(String methodName, String methodBody, int expectedComplexity) {
		Function methodToAnalyze = new Function(methodName, methodBody);
		Pair<String, Integer> result = CodeComplexityAnalyzer.evaluateComplexityOfAMethod(methodToAnalyze, "java");
		assertEquals(expectedComplexity, result.second());
	}
}
