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
		Function[] methods = new FileReader("src/test/resources").getFunctionStringsFromFile(bigJavaCodeFile);
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
		assertTrue(methods[0].body().contains("void methodWithAccessor    () {\n        // Method body\n    }"));
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
		assertTrue(methods[0].body().contains("void methodWithoutAccessor() {\n        // Method body\n    }"));
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
		assertTrue(methods[0].body().contains("void methodWithGenerics(T param) {\n        // Method body\n    }"));
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
}

