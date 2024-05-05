import java.util.List;

public class CodeStyleAnalyzer {

	public static double evaluateCodeStyle(List<Function> functions) {
		int nonCompliantMethods = 0;
		for(Function f: functions)
			if(!evaluateCodeStyleOfAMethod(f).second())
				nonCompliantMethods++;
		if(functions.isEmpty())
			throw new IllegalArgumentException("No methods to evaluate");
		return (100.0*nonCompliantMethods)/functions.size();
	}

	//1. it has to be in camelcase
	//2. no spaces
	//TODO: 3. only constructor starts with upper case
	public static Pair<String, Boolean> evaluateCodeStyleOfAMethod(Function methodToAnalyze) {
		return new Pair(methodToAnalyze.name(), methodToAnalyze.name().matches("[a-zA-Z][a-zA-Z0-9]*"));
	}
}
