import java.io.IOException;
import java.util.List;

public class CodeAnalyzer {

	private FileReader fileReader;
	//maybe add a possibility to set flags, as to what kind of checks do we want to be performed?
	//so the fields would be flags set

	/*
	 * analyzes the complexity and code style of each file in a given directory
	 * or should I just construct it with directoryPath and make it possible to set it?
	 * should it return string of analysis, or print it ?
	 */
	public String analyze (String directoryPath) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			fileReader = new FileReader(directoryPath);
			String fileName = fileReader.getNextFileName();
			while (fileName != null) {
				stringBuilder.append(String.format("Code analysis of file %s:\n", fileName));
				stringBuilder.append(analyzeFile());
				fileName = fileReader.getNextFileName();
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			//TODO: error handling
		} catch (SecurityException e) {
			//
		}
		return "";
	}

	/*
	 * It would go through all the methods in the file and do the analysis, would be used by analyze
	 * */
	private String analyzeFile () {
		StringBuilder stringBuilder = new StringBuilder();
		Function methodToAnalyze = fileReader.getNextFunction();

		while (methodToAnalyze != null) {
		//	List<Pair<String, Integer>> complexityEvaluationResult = CodeComplexityAnalyzer.evaluateComplexity(methodToAnalyze, 3);
			//Pair<String, Integer> codeStyleEvaluationResult =
		//	stringBuilder.append(complexityEvaluationResult);
			methodToAnalyze = fileReader.getNextFunction();
		}
		return stringBuilder.toString();
	}
}
