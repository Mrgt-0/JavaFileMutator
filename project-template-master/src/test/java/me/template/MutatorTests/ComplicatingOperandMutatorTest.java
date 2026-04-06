package me.template.MutatorTests;
import me.template.Mutators.ComplicatingOperandMutator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ComplicatingOperandMutatorTest {
    @TempDir
    private Path tempDir;

    private Path createTestFile(String content) throws IOException {
        Path file = tempDir.resolve("TestClass.java");
        Files.writeString(file, content);
        return file;
    }

    @Test
    public void testGenerateMutantsWithAddOne() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int a = 5;
                    int b = 10;
                    if (a < b) {
                        System.out.println("less");
                    }
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        ComplicatingOperandMutator mutator = new ComplicatingOperandMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();
        assertNotNull(files);
        assertTrue(files.size() > 0, "Мутанты не созданы");
        boolean hasAddOne = files.stream().anyMatch(f -> f.contains("add_one"));
        assertTrue(hasAddOne, "Нет мутанта с add_one");
    }

    @Test
    public void testGenerateMutantsWithAddTwo() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int x = 10;
                    int y = 20;
                    if (x == y) {
                        System.out.println("equal");
                    }
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        ComplicatingOperandMutator mutator = new ComplicatingOperandMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();
        assertNotNull(files);
        boolean hasAddTwo = files.stream().anyMatch(f -> f.contains("add_two"));
        assertTrue(hasAddTwo, "Нет мутанта с add_two");
    }

    @Test
    public void testGenerateMutantsWithMulTwo() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int value = 5;
                    if (value > 0) {
                        System.out.println("positive");
                    }
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        ComplicatingOperandMutator mutator = new ComplicatingOperandMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();
        assertNotNull(files);
        boolean hasMulTwo = files.stream().anyMatch(f -> f.contains("mul_two"));
        assertTrue(hasMulTwo, "Нет мутанта с mul_two");
    }

    @Test
    public void testGenerateMutantsWithSubOne() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int count = 10;
                    if (count != 0) {
                        System.out.println("not zero");
                    }
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        ComplicatingOperandMutator mutator = new ComplicatingOperandMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();
        assertNotNull(files);
        boolean hasSubOne = files.stream().anyMatch(f -> f.contains("sub_one"));
        assertTrue(hasSubOne, "Нет мутанта с sub_one");
    }

    @Test
    public void testGenerateMutantsWithDivTwo() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int num = 100;
                    if (num >= 50) {
                        System.out.println("large");
                    }
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        ComplicatingOperandMutator mutator = new ComplicatingOperandMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();
        assertNotNull(files);
        boolean hasDivTwo = files.stream().anyMatch(f -> f.contains("div_two"));
        assertTrue(hasDivTwo, "Нет мутанта с div_two");
    }

    @Test
    public void testGenerateMutantsAllMutationsCount() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int a = 5;
                    int b = 10;
                    if (a < b) {
                        System.out.println("less");
                    }
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        ComplicatingOperandMutator mutator = new ComplicatingOperandMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();
        assertEquals(5, files.size(),
                "Должно быть 5 мутантов, но создано: " + files.size());
    }

    @Test
    public void testGenerateMutantsWithMultipleOperators() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int a = 5;
                    int b = 10;
                    if (a < b) {
                        System.out.println("less");
                    }
                    if (a == b) {
                        System.out.println("equal");
                    }
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        ComplicatingOperandMutator mutator = new ComplicatingOperandMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();
        assertEquals(10, files.size(),
                "Должно быть 10 мутантов, но создано: " + files.size());
    }

    @Test
    public void testGenerateMutantsNoOperators() throws IOException {
        String testCode = """
            public class TestClass {
                public void test() {
                    int a = 5;
                    int b = a + 10;
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        String outputPath = tempDir.resolve("output").toString();
        ComplicatingOperandMutator mutator = new ComplicatingOperandMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();
        assertEquals(0, files.size(),
                "Должно быть 0 мутантов, но создано: " + files.size());
    }
}