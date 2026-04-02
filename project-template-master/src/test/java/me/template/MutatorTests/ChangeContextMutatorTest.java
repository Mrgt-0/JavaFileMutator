package me.template.MutatorTests;
import me.template.Mutators.ChangeContextMutator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBinaryOperator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ChangeContextMutatorTest {
    @TempDir
    private Path tempDir;

    private Path createTestFile(String content) throws IOException {
        Path file = tempDir.resolve("TestClass.java");
        Files.writeString(file, content);
        return file;
    }

    @Test
    public void testGenerateMutantsWithTernary() throws IOException {
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
        ChangeContextMutator mutator = new ChangeContextMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();
        assertNotNull(files);
        assertTrue(files.size() > 0, "Мутанты не созданы");

        boolean hasTernary = files.stream().anyMatch(f -> f.contains("Ternary"));
        assertTrue(hasTernary, "Нет мутанта с тернарным оператором");
    }

    @Test
    public void testGenerateMutantsWithAssert() throws IOException {
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
        ChangeContextMutator mutator = new ChangeContextMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();

        assertNotNull(files);
        boolean hasAssert = files.stream().anyMatch(f -> f.contains("ChangeContext"));
        assertTrue(hasAssert, "Нет мутанта с assert");
    }

    @Test
    public void testFindComparisonOperators() throws Exception {
        String testCode = """
            public class TestClass {
                public void test() {
                    if (a < b) { }
                    if (a > b) { }
                    if (a == b) { }
                    if (a != b) { }
                    int c = a + b;
                }
            }
            """;
        Path inputFile = createTestFile(testCode);
        ChangeContextMutator mutator = new ChangeContextMutator(
                inputFile.toString(), tempDir.toString());

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(21);
        launcher.addInputResource(inputFile.toString());
        CtModel model = launcher.buildModel();
        java.lang.reflect.Method method = ChangeContextMutator.class.getDeclaredMethod(
                "findComparisonOperators", CtModel.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<CtBinaryOperator<?>> operators =
                (List<CtBinaryOperator<?>>) method.invoke(mutator, model);
        assertNotNull(operators);
        assertTrue(operators.size() >= 4, "Найдено операторов: " + operators.size());
    }
}