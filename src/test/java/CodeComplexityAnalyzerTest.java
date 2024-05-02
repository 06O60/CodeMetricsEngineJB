import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeComplexityAnalyzerTest {

	@Test
	public void testEmptyTheStringLiterals_noStringLiterals_returnsTheSameCodeAsInputted() {
		String code = """
				public void addEdge(Node to, int capacity) {
					Edge e = new Edge(capacity, this, to);
					edges.add(e);
					to.getEdges().add(e.getBackwards());
				}""";
		String expectedCode = code;

		String returnedCode = CodeComplexityAnalyzer.emptyTheStringLiterals(code);

		assertEquals(expectedCode, returnedCode);
	}

	@Test
	public void testEmptyTheStringLiterals_oneStringLiteral_correctlyEmptiesTheString() {
		String code = """
				public void testCode(Code someParam) {
					String firstStringLiteral = "some text, maybe and if, or switch to throw the analyzer off :)";
					//comment
					if( 1 == 2 )
						throw new Exception();		
				}""";
		String expectedCode = """
				public void testCode(Code someParam) {
					String firstStringLiteral = "";
					//comment
					if( 1 == 2 )
						throw new Exception();		
				}""";


		String returnedCode = CodeComplexityAnalyzer.emptyTheStringLiterals(code);

		assertEquals(expectedCode, returnedCode);
	}

	@Test
	public void testEmptyTheStringLiterals_multipleStringLiterals_correctlyEmptiesTheString() {
		String code = """
				public void testCode(Code someParam) {
					String firstStringLiteral = "some text, maybe and if, or switch to throw the analyzer off :)";
					//comment
					if( "one".equals(2) )
						throw new Exception("Math is not mathin");		
				}""";
		String expectedCode = """
				public void testCode(Code someParam) {
					String firstStringLiteral = "";
					//comment
					if( "".equals(2) )
						throw new Exception("");	
				}""";

		String returnedCode = CodeComplexityAnalyzer.emptyTheStringLiterals(code);

		assertEquals(expectedCode, returnedCode);
	}
	@Test
	public void testEmptyTheStringLiterals_multilineString_correctlyEmptiesTheString() {
		String code = """
					public void testEmptyTheStringLiterals_noStringLiterals_returnsTheSameCodeAsInputted() {
						String code = \"\"\"
								public void addEdge(Node to, int capacity) {
									Edge e = new Edge(capacity, this, to);
									edges.add(e);
									to.getEdges().add(e.getBackwards());
								}\"\"\";
				   
						String returnedCode = CodeComplexityAnalyzer.emptyTheStringLiterals(code);
				   
						assertEquals(code, returnedCode);
					}
			""";
		String expectedCode = """
					public void testEmptyTheStringLiterals_noStringLiterals_returnsTheSameCodeAsInputted() {
						String code = \"\"\"\"\"\";
				   
						String returnedCode = CodeComplexityAnalyzer.emptyTheStringLiterals(code);
				   
						assertEquals(code, returnedCode);
					}
			""";


		String returnedCode = CodeComplexityAnalyzer.emptyTheStringLiterals(code);

		assertEquals(expectedCode, returnedCode);
	}

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
			"oldSwitchCaseMethod, switch (x) { case 1: return 'One'; case 2: return 'Two'; default: return 'Other'; }, 3",  // Old switch-case method
			"newSwitchCaseMethod, switch (x) { case true -> 'One'; case false -> 'Zero'; }, 2",
			"ternaryOperatorMethod, x > 0 ? 'Positive' : 'Negative', 2"  // Ternary operator method
	})
	public void testEvaluateComplexity(String methodName, String methodBody, int expectedComplexity) {
		Function methodToAnalyze = new Function(methodName, methodBody);
		Pair<String, Integer> result = CodeComplexityAnalyzer.evaluateComplexity(methodToAnalyze);
		assertEquals(expectedComplexity, result.second());
	}
}
