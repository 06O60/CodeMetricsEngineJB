import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {
	private String directoryPath;
	File[] files;

	public FileReader (String directoryPath) {
		this.directoryPath = directoryPath;
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

	/*
	 * gets the next Java/Kotlin fileName in the directory, now getNextMethod, will return methods from that file
	 * */
	public String getNextFileName () {
		return null;
	}

	public String getNextMethod () {
		return null;
	}

	private static boolean isAnalysableFileType (Path filePath) {
		return (filePath.endsWith(".java") || filePath.endsWith(".kt") || filePath.endsWith(".kts"));
	}

}
