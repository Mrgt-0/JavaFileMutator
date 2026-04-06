package me.template.MutatorTests;
import me.template.Mutators.BracketMutator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static spoon.testing.utils.Check.assertNotNull;

public class BracketMutatorTest {
    @TempDir
    private Path tempDir;

    private Path createTestFile(String content) throws IOException {
        Path file = tempDir.resolve("TestClass.java");
        Files.writeString(file, content);
        return file;
    }

    @Test
    public void testGenerateMutantsWithParentheses() throws IOException {
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
        BracketMutator mutator = new BracketMutator(
                inputFile.toString(), outputPath);
        List<String> files = mutator.generateMutants();

        assertNotNull(files);
        assertFalse(files.isEmpty(), "Мутанты не созданы");
        boolean hasParentheses = files.stream().anyMatch(f -> f.contains("AddParentheses"));
        assertTrue(hasParentheses, "Нет мутанта со скобками");
    }

    @Test
    public void testAddParentheses() throws Exception {
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
        BracketMutator mutator = new BracketMutator(
                inputFile.toString(), outputPath.toString());

        List<String> files = mutator.generateMutants();
        assertFalse(files.isEmpty(), "Мутанты не созданы");
        String content = Files.readString(Path.of(files.getFirst()));
        assertTrue(content.contains("(") && content.contains(")"),
                "Скобки не добавлены. Содержимое: " + content);
    }
}