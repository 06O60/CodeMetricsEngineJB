import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileReaderTest {
	private File bigJavaCodeFile = new File("src/test/resources/MaxFlow.java");
	private File tmpFile;
	private File createTempFile(String content) throws IOException {
		tmpFile = File.createTempFile("TestClass", ".java", new File("src/test/resources"));
		try (FileWriter writer = new FileWriter(tmpFile)) {
			writer.write("public class TestClass {\n");
			writer.write(content);
			writer.write("}\n");
		}
		return tmpFile;
	}

	@AfterEach
	public void deleteTemporaryFiles() {
		if(tmpFile != null)
			tmpFile.delete();
	}

	@Test
	public void testGetMethodStringsFromFile () {
		try {
			String[] methods = FileReader.getMethodStringsFromFile(bigJavaCodeFile);
			assertEquals(29, methods.length);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Test
	public void testMethodWithAccessor() throws IOException {
		File file = createTempFile("public void methodWithAccessor() {\n        // Method body\n    }\n");
		String[] methods = FileReader.getMethodStringsFromFile(file);
		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertTrue(methods[0].contains("methodWithAccessor()"));
	}

	@Test
	public void testMethodWithoutAccessor() throws IOException {
		File file = createTempFile("void methodWithoutAccessor() {\n        // Method body\n    }\n");
		String[] methods = FileReader.getMethodStringsFromFile(file);
		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertTrue(methods[0].contains("methodWithoutAccessor()"));
	}

	@Test
	public void testConstructorMethod() throws IOException {
		File file = createTempFile("public TestClass() {\n        // Constructor body\n    }\n");
		String[] methods = FileReader.getMethodStringsFromFile(file);
		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertTrue(methods[0].contains("TestClass()"));
	}

	@Test
	public void testMethodWithThrows() throws IOException {
		File file = createTempFile("public void methodWithThrows() throws Exception, IOException {\n        // Method body\n    }\n");
		String[] methods = FileReader.getMethodStringsFromFile(file);
		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertTrue(methods[0].contains("methodWithThrows()"));
	}

	@Test
	public void testMethodWithGenerics() throws IOException {
		File file = createTempFile("public <T> void methodWithGenerics(T param) {\n        // Method body\n    }\n");
		String[] methods = FileReader.getMethodStringsFromFile(file);
		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertTrue(methods[0].contains("methodWithGenerics(T param)"));
	}

	@Test
	public void testMethodInNestedClass() throws IOException {
		File file = createTempFile("static class NestedClass {\n        public void methodInNestedClass() {\n            // Method body\n        }\n    }\n");
		String[] methods = FileReader.getMethodStringsFromFile(file);
		assertNotNull(methods);
		assertEquals(1, methods.length);
		assertTrue(methods[0].contains("methodInNestedClass()"));
	}

}

