package me.template.MutatorTests;
import me.template.Mutators.OperatorMutator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.reflect.code.BinaryOperatorKind;
import java.lang.reflect.*;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class OperatorMutatorTest {
    @TempDir
    private Path tempDir;

    @Test
    public void testGetMutatedKind() {
        OperatorMutator mutator = new OperatorMutator("test.java", tempDir.toString());

        assertEquals(BinaryOperatorKind.NE, mutator.getMutatedKind(BinaryOperatorKind.EQ));
        assertEquals(BinaryOperatorKind.EQ, mutator.getMutatedKind(BinaryOperatorKind.NE));
        assertEquals(BinaryOperatorKind.SR, mutator.getMutatedKind(BinaryOperatorKind.SL));
        assertEquals(BinaryOperatorKind.SL, mutator.getMutatedKind(BinaryOperatorKind.SR));
        assertEquals(BinaryOperatorKind.GT, mutator.getMutatedKind(BinaryOperatorKind.LT));
        assertEquals(BinaryOperatorKind.LT, mutator.getMutatedKind(BinaryOperatorKind.GT));
        assertEquals(BinaryOperatorKind.GE, mutator.getMutatedKind(BinaryOperatorKind.LE));
        assertEquals(BinaryOperatorKind.LE, mutator.getMutatedKind(BinaryOperatorKind.GE));
    }

    @Test
    public void testOperatorToString() throws Exception {
        OperatorMutator mutator = new OperatorMutator("dummy.java", tempDir.toString());

        assertEquals("eq", mutator.operatorToString(BinaryOperatorKind.EQ));
        assertEquals("ne", mutator.operatorToString(BinaryOperatorKind.NE));
        assertEquals("sl", mutator.operatorToString(BinaryOperatorKind.SL));
        assertEquals("sr", mutator.operatorToString(BinaryOperatorKind.SR));
        assertEquals("lt", mutator.operatorToString(BinaryOperatorKind.LT));
        assertEquals("le", mutator.operatorToString(BinaryOperatorKind.LE));
        assertEquals("gt", mutator.operatorToString(BinaryOperatorKind.GT));
        assertEquals("ge", mutator.operatorToString(BinaryOperatorKind.GE));
    }

    @Test
    public void testGenerateFileName() throws Exception {
        OperatorMutator mutator = new OperatorMutator("dummy.java", tempDir.toString());
        String result = mutator.generateFileName("TestClass", "eq", "ne", 0);

        assertTrue(result.contains("TestClass"));
        assertTrue(result.contains("eq"));
        assertTrue(result.contains("ne"));
        assertTrue(result.contains("0"));
        assertTrue(result.startsWith(tempDir.toString()));
    }
}
