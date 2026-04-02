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
    public void testGetMutatedKind() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        OperatorMutator mutator = new OperatorMutator("test.java", tempDir.toString());
        Method method = OperatorMutator.class.getDeclaredMethod("getMutatedKind", BinaryOperatorKind.class);
        method.setAccessible(true);

        assertEquals(BinaryOperatorKind.NE, method.invoke(mutator, BinaryOperatorKind.EQ));
        assertEquals(BinaryOperatorKind.EQ, method.invoke(mutator, BinaryOperatorKind.NE));
        assertEquals(BinaryOperatorKind.SR, method.invoke(mutator, BinaryOperatorKind.SL));
        assertEquals(BinaryOperatorKind.SL, method.invoke(mutator, BinaryOperatorKind.SR));
        assertEquals(BinaryOperatorKind.GT, method.invoke(mutator, BinaryOperatorKind.LT));
        assertEquals(BinaryOperatorKind.LT, method.invoke(mutator, BinaryOperatorKind.GT));
        assertEquals(BinaryOperatorKind.GE, method.invoke(mutator, BinaryOperatorKind.LE));
        assertEquals(BinaryOperatorKind.LE, method.invoke(mutator, BinaryOperatorKind.GE));
    }

    @Test
    public void testOperatorToString() throws Exception {
        OperatorMutator mutator = new OperatorMutator("dummy.java", tempDir.toString());
        Method method = OperatorMutator.class.getDeclaredMethod("operatorToString", BinaryOperatorKind.class);
        method.setAccessible(true);

        assertEquals("eq", method.invoke(mutator, BinaryOperatorKind.EQ));
        assertEquals("ne", method.invoke(mutator, BinaryOperatorKind.NE));
        assertEquals("sl", method.invoke(mutator, BinaryOperatorKind.SL));
        assertEquals("sr", method.invoke(mutator, BinaryOperatorKind.SR));
        assertEquals("lt", method.invoke(mutator, BinaryOperatorKind.LT));
        assertEquals("le", method.invoke(mutator, BinaryOperatorKind.LE));
        assertEquals("dt", method.invoke(mutator, BinaryOperatorKind.GT));
        assertEquals("ge", method.invoke(mutator, BinaryOperatorKind.GE));
    }

    @Test
    public void testGenerateFileName() throws Exception {
        OperatorMutator mutator = new OperatorMutator("dummy.java", tempDir.toString());
        Method method = OperatorMutator.class.getDeclaredMethod(
                "generateFileName", String.class, String.class, String.class, int.class);
        method.setAccessible(true);
        String result = (String) method.invoke(mutator, "TestClass", "eq", "ne", 0);

        assertTrue(result.contains("TestClass"));
        assertTrue(result.contains("eq"));
        assertTrue(result.contains("ne"));
        assertTrue(result.contains("0"));
        assertTrue(result.startsWith(tempDir.toString()));
    }
}
