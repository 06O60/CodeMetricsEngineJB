# Code Metrics Engine

The Code Metrics Engine is a utility tool designed to analyze code complexity and style in a directory of Java or Kotlin files.
It provides comprehensive reports on various metrics such as code complexity and adherence to coding style conventions.

## Usage

### Running the App

1. Navigate to `CodeMetricsEnginge/src/main/java/Main.java`.
2. Execute the `main` method of `Main` class.
3. In the console, you'll be prompted to "Input the path to the project:"—please provide an absolute path to the directory to be assessed.

----

## CodeAnalyzer Class

The `CodeAnalyzer` class is a utility class designed to analyze code complexity and style in a directory of Java or Kotlin files. It serves as the central class of the application, providing methods to perform comprehensive analysis on the codebase.

### Inner Workings

The `CodeAnalyzer` class operates by analyzing each file in the specified directory individually. 
It utilizes the `FileReader` class to extract functions from the files and then performs analysis on these functions to generate code complexity and style reports.

### Usage

To utilize the functionality provided by the `CodeAnalyzer` class, follow these steps:

1. Instantiate a `CodeAnalyzer` object with the directory path and the number of results to display.
2. Call the `analyzeDirectory` method to perform code analysis on the files in the directory.
3. Retrieve the analysis results for each file, including code complexity and style reports.

#### Example usage
```java
   public class CodeAnalysisApp {
    public static void main(String[] args) {
        // Instantiate a CodeAnalyzer object with the directory path and number of results
        CodeAnalyzer codeAnalyzer = new CodeAnalyzer("path/to/directory", 5);
        
        // Perform code analysis on the directory
        String analysisResults;
        try {
            analysisResults = codeAnalyzer.analyzeDirectory(); //a string containing a detailed raport will be returned
            System.out.println(analysisResults);
        } catch (IOException | SecurityException e) {
            System.err.println("An error occurred during code analysis: " + e.getMessage());
        }
    }
}
```



