import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for analyzing code complexity and style in a directory of Java or Kotlin files.
 */
public class CodeAnalyzer {
	//maybe add a possibility to set flags, as to what kind of checks do we want to be performed?
	//so the fields would be flags set
	private final FileReader fileReader;
	private final int numOfResults;

	/**
	 * Constructs a CodeAnalyzer object with the specified directory path and number of results to display.
	 *
	 * @param directoryPath The path to the directory containing Java or Kotlin files.
	 * @param numOfResults  The number of results to display for code complexity analysis.
	 */
	public CodeAnalyzer (String directoryPath, int numOfResults) {
		this.fileReader = new FileReader(directoryPath);
		this.numOfResults = numOfResults;
	}


	/**
	 * Analyzes the code complexity and style of each file in the specified directory.
	 *
	 * @return A string containing the analysis results.
	 */
	public String analyzeDirectory () throws IOException, SecurityException {
		StringBuilder stringBuilder = new StringBuilder();
		String fileName = fileReader.getNextFileName();
		if(fileName == null)
			stringBuilder.append("No files to analyze in the provided directory.");
		while (fileName != null) {
			stringBuilder.append(String.format("Code analysis of file %s:\n", fileName));
			stringBuilder.append(analyzeFile());
			stringBuilder.append("\n");
			fileName = fileReader.getNextFileName();
		}
		return stringBuilder.toString();
	}

	/**
	 * Analyzes the code complexity and style of each (analyzable) file in the specified directory.
	 *
	 * @return A string containing the analysis results.
	 */
	private String analyzeFile () {
		List<Function> functionsToAnalyze = new ArrayList<>();
		Function functionToAnalyze = fileReader.getNextFunction();

		while (functionToAnalyze != null) {
			functionsToAnalyze.add(functionToAnalyze);
			functionToAnalyze = fileReader.getNextFunction();
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(generateCodeComplexityReport(functionsToAnalyze, numOfResults));
		stringBuilder.append(generateCodeStyleReport(functionsToAnalyze));

		return stringBuilder.toString();
	}

	/**
	 * Generates a report on code style analysis based on the provided list of functions.
	 *
	 * @param functionsToAnalyze The list of functions to analyze for code style.
	 * @return A string containing the code style analysis report.
	 */
	private static String generateCodeStyleReport (List<Function> functionsToAnalyze) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("========== Code style ==========\n");
		try {
			stringBuilder.append(
					String.format("Percentage of methods not complying with naming conventions: %.2f%%\n",
					              CodeStyleAnalyzer.evaluateCodeStyle(functionsToAnalyze)));
		} catch (IllegalArgumentException e) {
			stringBuilder.append("No methods to perform code style analysis found.\n");
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

	/**
	 * Generates a report on code complexity analysis based on the provided list of functions and the specified number of results to display.
	 *
	 * @param functionsToAnalyze The list of functions to analyze for code complexity.
	 * @param numOfResults      The number of results to display for code complexity analysis.
	 * @return A string containing the code complexity analysis report.
	 */
	private String generateCodeComplexityReport (List<Function> functionsToAnalyze, int numOfResults) {
		StringBuilder stringBuilder = new StringBuilder();
		List<Pair<String, Integer>> codeComplexityResult = CodeComplexityAnalyzer.evaluateComplexity(
				functionsToAnalyze,
				numOfResults
		);

		stringBuilder.append(
				String.format("========== Code complexity (showing top %d results) ==========\n", numOfResults));
		if (codeComplexityResult.isEmpty())
			stringBuilder.append("No methods to perform code complexity analysis found.\n");
		int resCount = 1;
		for (Pair<String, Integer> p : codeComplexityResult) {
			stringBuilder.append(String.format("%d. Function: %s, Complexity: %d\n",
			                                   resCount++,
			                                   p.first(),
			                                   p.second())
			);
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
}
