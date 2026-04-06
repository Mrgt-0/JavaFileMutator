package me.template.MutatorTests;
import me.template.Mutators.AddCastMutator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AddCastMutatorTest {
    @TempDir
    private Path tempDir;

    private Path createTestFile(String content) throws IOException {
        Path file = tempDir.resolve("TestClass.java");
        Files.writeString(file, content);
        return file;
    }

    @Test
    public void testGenerateMutantsWithIntCast() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int a = 5;
                    int b = 10;
                    if (a == b) {
                        System.out.println("equal");
                    }
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        AddCastMutator mutator = new AddCastMutator(
                inputFile.toString(), outputPath);

        List<String> files = mutator.generateMutants();
        assertNotNull(files);
        assertFalse(files.isEmpty(), "Мутанты не созданы");

        boolean hasCast = files.stream().anyMatch(f -> f.contains("AddCast"));
        assertTrue(hasCast, "Нет мутанта с кастом");
    }

    @Test
    public void testAddCast() throws Exception {
        String testCode = """
        public class TestClass {
            public void test() {
                if (5 < 10) { }
            }
        }
        """;
        Path inputFile = createTestFile(testCode);
        Path outputPath = tempDir.resolve("output");
        Files.createDirectories(outputPath);
        AddCastMutator mutator = new AddCastMutator(
                inputFile.toString(), outputPath.toString());

        List<String> files = mutator.generateMutants();
        assertFalse(files.isEmpty(), "Мутанты не созданы");
        for (String file : files) {
            String content = Files.readString(Path.of(file));
            assertTrue(content.contains("(int)"),
                    "Каст int не найден в файле: " + file);
        }
    }

    @Test
    public void testCastTypeMatchesOperandType() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int intVar = 5;
                    double doubleVar = 10.5;
                    if (intVar < doubleVar) {
                        System.out.println("less");
                    }
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        AddCastMutator mutator = new AddCastMutator(
                inputFile.toString(), outputPath);

        List<String> files = mutator.generateMutants();
        assertNotNull(files);
        boolean hasIntCast = files.stream().anyMatch(f -> f.contains("int"));
        assertTrue(hasIntCast, "Нет каста к типу int");
    }
}