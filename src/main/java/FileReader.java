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

/**
 * Utility class for reading Java/Kotlin files and extracting methods.
 */
public class FileReader {
	private String directoryPath;
	File[] files;
	String[] methods;
	int filesIterator = 0;
	int methodIterator = 0;

	/**
	 * Constructs a FileReader object with the specified directory path.
	 *
	 * @param directoryPath The path to the directory containing Java/Kotlin files to extract functions from.
	 */
	public FileReader (String directoryPath) {
		this.directoryPath = directoryPath;
		filesIterator = 0;
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

	//TODO: the following method should skip files that are not compilable
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

	}

	/**
	 * Checks if the file type is analyzable.
	 *
	 * @param filePath The path to the file.
	 * @return True if the file is a compilable Java or Kotlin code, false otherwise.
	 */
	private static boolean isAnalysableFile (Path filePath) {
		return (filePath.endsWith(".java") || filePath.endsWith(".kt") || filePath.endsWith(".kts"));
	}

	/**
	 * Retrieves the next Java/Kotlin file name in the directory.
	 * Sets up the methods array to contain the methods of the retrieved file.
	 *
	 * @return The name of the next file, or null if there are no more files.
	 * @throws FileNotFoundException If a file with the specified path could not be found.
	 */
	public String getNextFileName () throws FileNotFoundException {
		if(files.length < filesIterator) {
			methods = getMethodStringsFromFile(files[filesIterator]);
			methodIterator = 0;
			return files[filesIterator++].getName();
		}
		else return null;
	}

	/**
	 * Retrieves the next method from the current file.
	 *
	 * @return The next method, or null if there are no more methods in the current file.
	 */
	public String getNextMethod () {
		return (methodIterator < methods.length) ? methods[methodIterator++] : null;
	}

	/**
	 * Retrieves the method strings from the specified file.
	 *
	 * @param file The file from which to extract methods.
	 * @return An array of method strings.
	 * @throws FileNotFoundException If the file could not be found.
	 */
	protected static String[] getMethodStringsFromFile(File file) throws FileNotFoundException {
		List<String> methods = new ArrayList<>();

		// Read the file contents into a string
		String fileContents = readFileContents(file);

		// Regex pattern to match method definition
		String methodPattern = "\\b(?!record)\\w+\\b(?:<[^<>]+(?:<[^<>]+>)*>)?\\s+\\w+\\s*\\([^()]*\\)\\s*(?:throws\\s+\\w+(?:,\\s*\\w+)*)?\\s*\\{";

		Pattern pattern = Pattern.compile(methodPattern);
		Matcher matcher = pattern.matcher(fileContents);

		while (matcher.find()) {
			String method = extractMethod(matcher.group(), fileContents);
			methods.add(method);
		}

		return methods.toArray(new String[0]);
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
	 * Extracts a method from the file contents based on its signature.
	 *
	 * @param methodSignature The signature of the method to extract.
	 * @param fileContents    The contents of the file to extract the method from.
	 * @return The extracted method.
	 */
	private static String extractMethod(String methodSignature, String fileContents) {
		int methodStartIndex = fileContents.indexOf(methodSignature);
		int methodEndIndex = findMethodEndIndex(methodStartIndex, fileContents);
		return fileContents.substring(methodStartIndex, methodEndIndex);
	}

	/**
	 * Finds the end index of a method in the file contents string, given its start index.
	 *
	 * @param methodStartIndex The start index of the method in the file contents.
	 * @param fileContents     The contents of the file.
	 * @return The end index of the method.
	 */
	private static int findMethodEndIndex(int methodStartIndex, String fileContents) {
		int curlyBraceCount = 0;
		int index = methodStartIndex;
		while (index < fileContents.length()) {
			char c = fileContents.charAt(index);
			if (c == '{') {
				curlyBraceCount++;
			} else if (c == '}') {
				curlyBraceCount--;
				if (curlyBraceCount == 0) {
					return index + 1; // Return index of closing brace
				}
			}
			index++;
		}
		return index; // Method end not found, return end of file
	}


}
