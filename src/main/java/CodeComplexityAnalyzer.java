import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeComplexityAnalyzer {

	public enum JavaConditionals {
		IF_STATEMENT("if"),
		ELSE_STATEMENT("else"),
		ELSE_IF_STATEMENT("else if"),
		SWITCH_CASE("case"),
		SWITCH_DEFAULT("default"),
		FOR_LOOP("for"),
		WHILE_LOOP("while"),
		TERNARY_OPERATOR("[?][^?]+:[^?]+");

		private final Pattern codePattern;
		private JavaConditionals (String codeString) {
			this.codePattern = Pattern.compile(codeString);
		}
		public Pattern getCodePattern() {
			return codePattern;
		}
	}

	//TODO: document why i decided to count if, else, else if separately
	//TODO: implement KMP-algo to count for substrings
	//TODO: Implement search for ternary operator for java
	//TODO: do not count any conditional operators that are in strings in the code.
	public static Pair<String, Integer> evaluateComplexity (Function methodToAnalyze) {
		String codeToAnalyze = emptyTheStringLiterals(methodToAnalyze.body());
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

	//TODO: check if it works for the multiline strings as well
	protected static String emptyTheStringLiterals(String code) {
		Pattern pattern = Pattern.compile("\"[^\"]*\"");
		Matcher matcher = pattern.matcher(code);

		//so any "sdfgd" is in the end ""
		return matcher.replaceAll("\"\"");
	}
}