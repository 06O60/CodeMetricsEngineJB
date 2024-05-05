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
----

## FileReader Class

The `FileReader` class is a utility class responsible for reading Java/Kotlin files, extracting methods from them, and preparing them for analysis within the application.

### Supported Languages

The `FileReader` class supports Java and Kotlin files for method extraction and analysis.

### Usage

To utilize the functionality provided by the `FileReader` class, follow these steps:

1. Instantiate a `FileReader` object with the directory path containing the Java/Kotlin files.
2. Use the `getNextFileName` method to retrieve the next file name in the directory and prepare the methods array for analysis.
3. Use the `getNextFunction` method to iterate through the methods extracted from the current file.

### Inner Workings

Before extracting the methods, the `FileReader` class performs the following steps:

1. **Cleaning Up Code**: The class cleans up the code out of string literals and comments. This process ensures that the extracted methods only contain the actual code to be analyzed, excluding comments and string literals that are not part of the code logic.

2. **Method Extraction**: Once the code is cleaned up, the class extracts the methods using regular expressions. For Java files, it utilizes the `javaMethodPattern` pattern, while for Kotlin files, it uses the `kotlinMethodPattern` pattern. These patterns are designed to match method definitions accurately, allowing for precise extraction of methods from the code.

----

## Code Complexity 

### Displaying top N most complex methods in each analysed file

To display more than the top 3 most complex methods in a file, utilize the CodeAnalyzer object with the desired number of results:

```java
CodeAnalyzer codeAnalyzer = new CodeAnalyzer(directoryPath, 5);
System.out.println(codeAnalyzer.analyzeDirectory());
```
This will print the raport with top 5 results.

**Note:** If fewer methods have a complexity greater than 0 than the requested number of results, only those methods with complexity greater than 0 will be returned.

### Methodology

Code complexity is measured by counting occurrences of 'if', 'else', 'else if', 'switch', 'for', 'while' statements, as well as the ternary operator. Here's how they contribute to the complexity:

- Each 'if', 'else', 'else if', 'for', 'while', and 'switch' statement increments the complexity by 1.
- The ternary operator (?:) increments the complexity by 2, as it is equivalent to an 'if' statement with both 'if' and 'else' branches.
Please note that in the current implementation, each case of a switch statement is counted as 1. A potential improvement could be to count each case separately.

### Implementation Details
All utilities required for measuring code complexity are contained within the CodeComplexityAnalyzer class. Here are some key points:

- Each method in CodeComplexityAnalyzer is static, as it's intended to be a utility class with methods that don't require any private context.
- The evaluateComplexity method evaluates the complexity of methods provided as input and returns a list of pairs containing method names and their complexities, sorted from highest to lowest.
- The method `evaluateComplexity` evaluates the complexity of methods given as a parameter and parses the results into a list of pairs <method_name, complexity_value>. It doesn't directly return a string with complexity report to promote extensibility of the application—this way different styles of reports can be generated using the same method.
- The evaluateComplexityOfAMethod method evaluates the complexity of a single method.

----

 ## Code Style

The CodeStyleAnalyzer class provides functionality for evaluating the overall code style compliance of a list of functions within the application.

### Usage
To assess the code style compliance of functions, you can utilize the evaluateCodeStyle method, passing a list of functions as input. This method returns the percentage of non-compliant methods in the list.

```java
List<Function> functions = getFunctionsFromCodebase();
double compliancePercentage = CodeStyleAnalyzer.evaluateCodeStyle(functions);
System.out.println("Code style compliance: " + compliancePercentage + "%");
```

### Methodology
The CodeStyleAnalyzer evaluates the code style compliance of individual functions using the evaluateCodeStyleOfAFunction method. Currently, this method checks if function names adhere to camelCase conventions and do not contain illegal characters such as "_".

### Implementation Details
The CodeStyleAnalyzer class contains static methods for code style evaluation, ensuring ease of use and independence from class instances.
Error handling is implemented to handle empty function lists, throwing an IllegalArgumentException when encountered.



