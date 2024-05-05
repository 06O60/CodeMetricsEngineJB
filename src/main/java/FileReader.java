import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO implement checking whether the whole directory is compilable, if not do not proceed with checks
/**
 * Utility class for reading Java/Kotlin files and extracting methods.
 */
public class FileReader {
	private final String directoryPath;
	private File[] files;
	private Function[] methods;
	private int filesIterator;
	private int methodIterator;

	/**
	 * Constructs a FileReader object with the specified directory path.
	 *
	 * @param directoryPath The path to the directory containing Java/Kotlin files to extract functions from.
	 */
	public FileReader (String directoryPath) {
		this.directoryPath = directoryPath;
		filesIterator = 0;
		methodIterator = 0;
	}

	/**
	 * Retrieves all the Java/Kotlin files in the specified directory.
	 *
	 * @return An array of File objects representing the files.
	 * @throws SecurityException If a security manager exists and denies access to the directory.
	 * @throws IOException      If an I/O error occurs while reading the directory.
	 */
	private File[] getFiles () throws SecurityException, IOException {
		if (files == null) {
			files = getAllAnalysableFiles();
		}
		return files;
	}

	/**
	 * Retrieves all the analyzable files in the specified directory.
	 * By analyzable I mean, only the files that contain compilable Java/Kotlin code
	 * @return An array of File objects representing the analyzable files.
	 * @throws SecurityException If a security manager exists and denies access to the directory.
	 * @throws IOException      If an I/O error occurs while reading the directory.
	 */
	private File[] getAllAnalysableFiles () throws SecurityException, IOException {
		Path dir = Paths.get(directoryPath);

		return Files.walk(dir)
				       .filter(path -> Files.isRegularFile(path) && isAnalysableFile(path))
				       .map(Path::toFile)
				       .toArray(File[]::new);
		//TODO: at the end altogether check if the project compiles
	}

	/**
	 * Checks if the file type is analyzable.
	 *
	 * @param filePath The path to the file.
	 * @return True if the file is a compilable Java or Kotlin code, false otherwise.
	 */
	private static boolean isAnalysableFile (Path filePath) {
		String pathString = filePath.toString();
		return (pathString.endsWith(".java") || pathString.endsWith(".kt") || pathString.endsWith(".kts"));
	}

	/**
	 * Retrieves the next Java or Kotlin file name in the directory and prepares
	 * the methods array to contain the methods of the retrieved file.
	 *
	 * @return The name of the next file, or {@code null} if there are no more files.
	 * @throws IOException    If an I/O error occurs while accessing the directory.
	 * @throws SecurityException If a security manager denies access to the directory.
	 */
	public String getNextFileName () throws IOException, SecurityException {
		if(files == null)
			files = getFiles();
		if(filesIterator < files.length) {
			methods = getFunctionStringsFromFile(files[filesIterator]);
			methodIterator = 0;
			return files[filesIterator++].getName();
		}
		else return null;
	}

	/**
	 * Retrieves the next function from the current file.
	 *
	 * @return The next function, or null if there are no more methods in the current file.
	 */
	public Function getNextFunction () {
		return (methodIterator < methods.length) ? methods[methodIterator++] : null;
	}

	/**
	 * Retrieves the functions strings from the specified file.
	 *
	 * @param file The file from which to extract methods.
	 * @return An array of method strings.
	 * @throws FileNotFoundException If the file could not be found.
	 */
	protected static Function[] getFunctionStringsFromFile (File file) throws FileNotFoundException {
		List<Function> functions = new ArrayList<>();

		// Read the file contents into a string
		String fileContents = readFileContents(file);

		// Regex pattern to match method definition
		String methodPattern = "\\b(?!record)\\w+\\b(?:<[^<>]+(?:<[^<>]+>)*>)?\\s+\\w+\\s*\\([^()]*\\)\\s*(?:throws\\s+\\w+(?:,\\s*\\w+)*)?\\s*\\{";

		Pattern pattern = Pattern.compile(methodPattern);
		Matcher matcher = pattern.matcher(fileContents);

		while (matcher.find()) {
			Function function = extractFunction(matcher.group(), fileContents);
			functions.add(function);
		}

		return functions.toArray(new Function[0]);
	}


	/**
	 * Reads the contents of the specified file into a String.
	 *
	 * @param file The file to read.
	 * @return The contents of the file as a string.
	 * @throws FileNotFoundException If the file could not be found.
	 */
	private static String readFileContents(File file) throws FileNotFoundException {
		StringBuilder stringBuilder = new StringBuilder();
		try (Scanner fileScanner = new Scanner(file)) {
			while (fileScanner.hasNextLine()) {
				stringBuilder.append(fileScanner.nextLine()).append("\n");
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Extracts a function from the file contents based on its signature.
	 *
	 * @param functionSignature The signature of the method to extract.
	 * @param fileContents    The contents of the file to extract the method from.
	 * @return The extracted method.
	 */
	private static Function extractFunction (String functionSignature, String fileContents) {
		String functionName = extractFunctionName(functionSignature);
		int functionStartIndex = fileContents.indexOf(functionSignature);
		int functionEndIndex = findFunctionEndIndex(functionStartIndex, fileContents);
		String functionBody =fileContents.substring(functionStartIndex, functionEndIndex);
		return new Function(functionName, cleanUpRedundantPiecesOfCode(functionBody));
	}

	/**
	 * Extracts the name of a function from its signature.
	 *
	 * @param functionSignature The signature of the function.
	 * @return The name of the function.
	 */
	private static String extractFunctionName (String functionSignature) {
		String methodNamePattern = "([a-zA-Z0-9_$]+) *\\(";
		Pattern pattern = Pattern.compile(methodNamePattern);
		Matcher matcher = pattern.matcher(functionSignature);
		matcher.find();
		String functionName = matcher.group(1);
		return functionName;
	}

	/**
	 * Finds the end index of a method in the file contents string, given its start index.
	 *
	 * @param functionStartIndex The start index of the method in the file contents.
	 * @param fileContents     The contents of the file.
	 * @return The end index of the method.
	 */
	private static int findFunctionEndIndex (int functionStartIndex, String fileContents) {
		int curlyBraceCount = 0;
		int index = functionStartIndex;
		while (index < fileContents.length()) {
			char c = fileContents.charAt(index);
			if (c == '{') {
				curlyBraceCount++;
			} else if (c == '}') {
				curlyBraceCount--;
				if (curlyBraceCount == 0) {
					return index + 1;
				}
			}
			index++;
		}
		return index; // Function end not found, return end of file
	}

	/**
	 * Cleans up redundant pieces of code, including string literals and commented-out sections.
	 * Comments and contents of string literals are considered reduncant, since they
	 * should not be accounted for during code analysis, since they are not really part of the code
	 * @param code The code to clean up.
	 * @return The cleaned-up code.
	 */
	protected static String cleanUpRedundantPiecesOfCode(String code) {
		code = emptyTheStringLiterals(code);
		return deleteCommentedOutCode(code);
	}

	/**
	 * Removes string literals from the provided code.
	 * Changes each string like "string" into "" in code.
	 * @param code The code containing string literals.
	 * @return The code with string literals replaced by empty strings.
	 */
	protected static String emptyTheStringLiterals(String code) {
		Pattern pattern = Pattern.compile("\"[^\"]*\"");
		Matcher matcher = pattern.matcher(code);

		//so any "sdfgd" is in the end ""
		return matcher.replaceAll("\"\"");
	}

	/**
	 * Deletes commented-out code from the provided code.
	 *
	 * @param code The code containing commented-out sections.
	 * @return The code with commented-out sections removed.
	 */
	protected static String deleteCommentedOutCode(String code) {
		Pattern pattern = Pattern.compile("//.*|/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/");
		Matcher matcher = pattern.matcher(code);

		return matcher.replaceAll("");
	}



}
