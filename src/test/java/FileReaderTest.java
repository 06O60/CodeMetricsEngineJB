import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileReaderTest {
	private final File bigJavaCodeFile = new File("src/test/resources/MaxFlow.java");
	private File tmpFile;

	private File createTempFile (String content) throws IOException {
		tmpFile = File.createTempFile("TestClass", ".java", new File("src/test/resources/tmp"));
		try (FileWriter writer = new FileWriter(tmpFile)) {
			writer.write("public class TestClass {\n");
			writer.write(content);
			writer.write("}\n");
		}
		return tmpFile;
	}

	@AfterEach
	public void deleteTemporaryFiles () {
		if (tmpFile != null)
			tmpFile.delete();
	}

	@Test
	public void testGetMethodStringsFromFile_validFileProvided_returnsExpectedNumberOfMethods () throws IOException {
		Function[] methods =
				new FileReader("src/test/resources/MaxFlow.java").getFunctionStringsFromFile(bigJavaCodeFile);
		assertEquals(29, methods.length);
	}

	// The following tests check whether given a method in a java file, the getMethodStringsFromFile correctly detects
	// the method name, and correctly identifies the method body
	@Test
	public void testGetMethodStringsFromFile_methodWithAccessorProvided_returnsCorrectMethodNameAndBody() throws IOException {
		File file = createTempFile("public void methodWithAccessor    () {\n        // Method body\n    }\n");
		FileReader fileReader = new FileReader("src/test/resources/tmp");
		fileReader.getNextFileName();

		Function[] methods = FileReader.getFunctionStringsFromFile(file);

		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertEquals("methodWithAccessor", methods[0].name());
		assertTrue(methods[0].body().contains("void methodWithAccessor    () {\n        \n    }"));
	}
	@Test
	public void testGetMethodStringsFromFile_methodWithoutAccessorProvided_returnsCorrectMethodNameAndBody() throws IOException {
		File file = createTempFile("void methodWithoutAccessor() {\n        // Method body\n    }\n");
		FileReader fileReader = new FileReader("src/test/resources/tmp");
		fileReader.getNextFileName();

		Function[] methods = fileReader.getFunctionStringsFromFile(file);

		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertEquals("methodWithoutAccessor", methods[0].name());
		assertTrue(methods[0].body().contains("void methodWithoutAccessor() {\n        \n    }"));
	}

	@Test
	public void testGetMethodStringsFromFile_constructorMethodProvided_returnsCorrectClassNameAndBody () throws IOException {
		File file = createTempFile("public TestClass() {\n        // Constructor body\n    }\n");
		FileReader fileReader = new FileReader("src/test/resources/tmp");

		Function[] methods = fileReader.getFunctionStringsFromFile(file);

		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertEquals("TestClass", methods[0].name());
		assertTrue(methods[0].body().contains("TestClass()"));
	}

	@Test
	public void testGetMethodStringsFromFile_methodWithThrowsProvided_returnsCorrectMethodNameAndBody () throws IOException {
		File file = createTempFile(
				"public void methodWithThrows() throws Exception, IOException {\n        // Method body\n    }\n");
		FileReader fileReader = new FileReader("src/test/resources/tmp");

		Function[] methods = fileReader.getFunctionStringsFromFile(file);

		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertEquals("methodWithThrows", methods[0].name());
		assertTrue(methods[0].body().contains("methodWithThrows()"));
	}

	@Test
	public void testGetMethodStringsFromFile_methodWithGenericsProvided_returnsCorrectMethodNameAndBody () throws IOException {
		File file = createTempFile("public <T> void methodWithGenerics(T param) {\n        // Method body\n    }\n");
		FileReader fileReader = new FileReader("src/test/resources/tmp");

		Function[] methods = fileReader.getFunctionStringsFromFile(file);

		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertEquals("methodWithGenerics", methods[0].name());
		assertTrue(methods[0].body().contains("{\n        \n    }"));

	}

	@Test
	public void testGetMethodStringsFromFile_methodInNestedClassProvided_returnsCorrectMethodNameAndBody () throws IOException {
		File file = createTempFile(
				"static class NestedClass {\n        public void methodInNestedClass() {\n            // Method " +
						"body\n" +
						"        }\n    }\n");
		FileReader fileReader = new FileReader("src/test/resources/tmp");

		Function[] methods = fileReader.getFunctionStringsFromFile(file);

		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertEquals("methodInNestedClass", methods[0].name());
		assertTrue(methods[0].body().contains("methodInNestedClass()"));
	}


	@Test
	public void testEmptyTheStringLiterals_noStringLiterals_returnsTheSameCodeAsInputted() {
		String code = """
				public void addEdge(Node to, int capacity) {
					Edge e = new Edge(capacity, this, to);
					edges.add(e);
					to.getEdges().add(e.getBackwards());
				}""";
		String expectedCode = code;

		String returnedCode = FileReader.emptyTheStringLiterals(code);

		assertEquals(expectedCode, returnedCode);
	}

	@Test
	public void testEmptyTheStringLiterals_oneStringLiteral_correctlyEmptiesTheString() {
		String code = """
				public void testCode(Code someParam) {
					String firstStringLiteral = "some text, maybe and if, or switch to throw the analyzer off :)";
					//comment
					if( 1 == 2 )
						throw new Exception();
				}""";
		String expectedCode = """
				public void testCode(Code someParam) {
					String firstStringLiteral = "";
					//comment
					if( 1 == 2 )
						throw new Exception();
				}""";


		String returnedCode = FileReader.emptyTheStringLiterals(code);

		assertEquals(expectedCode, returnedCode);
	}

	@Test
	public void testEmptyTheStringLiterals_multipleStringLiterals_correctlyEmptiesTheString() {
		String code = """
				public void testCode(Code someParam) {
					String firstStringLiteral = "some text, maybe and if, or switch to throw the analyzer off :)";
					//comment
					if( "one".equals(2) )
						throw new Exception("Math is not mathin");
				}""";
		String expectedCode = """
				public void testCode(Code someParam) {
					String firstStringLiteral = "";
					//comment
					if( "".equals(2) )
						throw new Exception("");
				}""";

		String returnedCode = FileReader.emptyTheStringLiterals(code);

		assertEquals(expectedCode, returnedCode);
	}
	@Test
	public void testEmptyTheStringLiterals_multilineString_correctlyEmptiesTheString() {
		String code = """
					public void testEmptyTheStringLiterals_noStringLiterals_returnsTheSameCodeAsInputted() {
						String code = \"\"\"
								public void addEdge(Node to, int capacity) {
									Edge e = new Edge(capacity, this, to);
									edges.add(e);
									to.getEdges().add(e.getBackwards());
								}\"\"\";

						String returnedCode = FileReader.emptyTheStringLiterals(code);

						assertEquals(code, returnedCode);
					}
			""";
		String expectedCode = """
					public void testEmptyTheStringLiterals_noStringLiterals_returnsTheSameCodeAsInputted() {
						String code = \"\"\"\"\"\";

						String returnedCode = FileReader.emptyTheStringLiterals(code);

						assertEquals(code, returnedCode);
					}
			""";


		String returnedCode = FileReader.emptyTheStringLiterals(code);

		assertEquals(expectedCode, returnedCode);
	}


}

