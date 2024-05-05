import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeAnalyzer {

	private FileReader fileReader;
	private int numOfResults;

	public CodeAnalyzer (String directoryPath,int numOfResults) {
		this.fileReader = new FileReader(directoryPath);
		this.numOfResults = numOfResults;
	}

	//maybe add a possibility to set flags, as to what kind of checks do we want to be performed?
	//so the fields would be flags set

	/*
	 * analyzes the complexity and code style of each file in a given directory
	 * or should I just construct it with directoryPath and make it possible to set it?
	 * should it return string of analysis, or print it ?
	 */
	public String analyze () {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			String fileName = fileReader.getNextFileName();
			while (fileName != null) {
				stringBuilder.append(String.format("Code analysis of file %s:\n", fileName));
				stringBuilder.append(analyzeFile());
				stringBuilder.append("\n");
				fileName = fileReader.getNextFileName();
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			//TODO: error handling
		} catch (SecurityException e) {
			//
		}
		return stringBuilder.toString();
	}

	/*
	 * It would go through all the methods in the file and do the analysis, would be used by analyze
	 * */
	private String analyzeFile () {
		List<Function> functionsToAnalyze = new ArrayList<>();
		Function functionToAnalyze = fileReader.getNextFunction();

		while (functionToAnalyze != null) {
			functionsToAnalyze.add(functionToAnalyze);
			functionToAnalyze = fileReader.getNextFunction();
		}

		StringBuilder stringBuilder = new StringBuilder();
		List<Pair<String, Integer>> codeComplexityResult = CodeComplexityAnalyzer.evaluateComplexity(
				functionsToAnalyze,
				numOfResults
		);

		stringBuilder.append(
				String.format("========== Code complexity (showing top %d results) ==========\n", numOfResults));
		if(codeComplexityResult.isEmpty())
			stringBuilder.append("No methods to perform code complexity check found.");
		int resCount = 1;
		for (Pair<String, Integer> p : codeComplexityResult) {
			stringBuilder.append(String.format("%d. Function name: %s, Code complexity value: %d\n",
			                                   resCount++,
			                                   p.first(),
			                                   p.second())
			);
		}
		stringBuilder.append("\n");

		stringBuilder.append("========== Code style ==========\n");
		try {
			stringBuilder.append(
					String.format("Percentage of methods that do not comply with the naming conventions: %.2f%%",
					              CodeStyleAnalyzer.evaluateCodeStyle(functionsToAnalyze)));
		} catch(IllegalArgumentException e) {
			stringBuilder.append("No methods to perform code style check found.");
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
}
