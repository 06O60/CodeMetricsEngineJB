import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {
	private String directoryPath;
	File[] files;
	String[] methods;
	int filesIterator = 0;
	int methodIterator = 0;

	public FileReader (String directoryPath) {
		this.directoryPath = directoryPath;
		filesIterator = 0;
	}

	private File[] getFiles () throws SecurityException, IOException {
		if (files == null) {
			files = getAllAnalysableFiles();
		}
		return files;
	}

	private File[] getAllAnalysableFiles () throws SecurityException, IOException {
		Path dir = Paths.get(directoryPath);

		return Files.walk(dir)
				       .filter(path -> Files.isRegularFile(path) && isAnalysableFileType(path))
				       .map(Path::toFile)
				       .toArray(File[]::new);

	}
	private static boolean isAnalysableFileType (Path filePath) {
		return (filePath.endsWith(".java") || filePath.endsWith(".kt") || filePath.endsWith(".kts"));
	}
	/*
	 * gets the next Java/Kotlin fileName in the directory, now getNextMethod, will return methods from that file
	 * */
	public String getNextFileName () {
		if(files.length < filesIterator) {
			methods = getMethodStringsFromFile(files[filesIterator]);
			methodIterator = 0;
			return files[filesIterator++].getName();
		}
		else return null;
	}

	private String[] getMethodStringsFromFile (File file) {
		return new String[0];
	}

	public String getNextMethod () {
		return (methodIterator < methods.length) ? methods[methodIterator++] : null;
	}
}
