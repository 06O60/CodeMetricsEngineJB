import java.io.IOException;
import java.util.Scanner;

public class Main {
	private static String ANSI_RESET = "\u001B[0m";
	private static String ANSI_RED = "\u001B[31m";
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
				System.out.println(ANSI_RED + "An IOException occured while trying to read the directory, please try again." + ANSI_RESET);
			} catch (SecurityException e) {
				System.err.println(ANSI_RED + "The security manager denied access to the directory, please try again" + ANSI_RESET);
			} catch (Exception e) {
				System.err.println(ANSI_RED + "Something went wrong... please try again." + ANSI_RESET);
			}
		}
	}
}
