import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main (String[] args) {
		Scanner userInputScanner = new Scanner(System.in);

		while(true) {
			System.out.print("Input the path to the project:");
			String directoryPath = userInputScanner.nextLine();

			CodeAnalyzer codeAnalyzer = new CodeAnalyzer(directoryPath, 3);
			try {
				System.out.println(codeAnalyzer.analyzeDirectory());
				return;
			} catch (IOException e) {
				System.out.println("An IOException occured while trying to read the directory, please try again.");
			} catch (SecurityException e) {
				System.err.println("The security manager denied access to the directory, please try again");
			} catch (Exception e) {
				System.err.println("Something went wrong... please try again.");
			}
		}
	}
}
