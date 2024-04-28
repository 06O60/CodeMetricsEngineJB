import java.io.File;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

	public static void main (String[] args) {
		Scanner userInputScanner = new Scanner(System.in);

		System.out.print("Input the path to the project:");
		String directoryPath = userInputScanner.nextLine();

		FileReader fileReader = new FileReader(directoryPath);
	}
}
