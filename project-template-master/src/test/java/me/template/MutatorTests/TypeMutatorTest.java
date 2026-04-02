package me.template.MutatorTests;
import me.template.Mutators.TypeMutator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.reflect.reference.CtTypeReference;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TypeMutatorTest {
    @TempDir
    private Path tempDir;

    @Test
    public void testGetMutatedType() throws Exception {
        Path dummyFile = tempDir.resolve("test.java");
        Files.writeString(dummyFile, "public class test { }");

        TypeMutator mutator = new TypeMutator(dummyFile.toString(), tempDir.toString());
        Method method = TypeMutator.class.getDeclaredMethod("getMutatedType", CtTypeReference.class);
        method.setAccessible(true);
        spoon.Launcher launcher = new spoon.Launcher();
        spoon.reflect.factory.Factory factory = launcher.getFactory();

        CtTypeReference<?> intType = factory.Type().createReference(int.class);
        CtTypeReference<?> result = (CtTypeReference<?>) method.invoke(mutator, intType);
        assertNotNull(result);
        assertEquals("long", result.getSimpleName());

        CtTypeReference<?> doubleType = factory.Type().createReference(double.class);
        result = (CtTypeReference<?>) method.invoke(mutator, doubleType);
        assertNotNull(result);
        assertEquals("float", result.getSimpleName());

        CtTypeReference<?> shortType = factory.Type().createReference(short.class);
        result = (CtTypeReference<?>) method.invoke(mutator, shortType);
        assertNotNull(result);
        assertEquals("int", result.getSimpleName());

        CtTypeReference<?> stringType = factory.Type().createReference(String.class);
        result = (CtTypeReference<?>) method.invoke(mutator, stringType);
        assertNull(result);
    }

    @Test
    public void testFindTypes() throws Exception {
        String testCode = """
            public class TestClass {
                int intVar = 10;
                double doubleVar = 10.5;
                short shortVar = 5;
                String stringVar = "test";
                
                public void testMethod() {
                    int localInt = 20;
                    double localDouble = 20.5;
                }
            }
            """;
        Path inputFile = tempDir.resolve("TestClass.java");
        Files.writeString(inputFile, testCode);
        TypeMutator mutator = new TypeMutator(inputFile.toString(), tempDir.toString());

        spoon.Launcher launcher = new spoon.Launcher();
        launcher.getEnvironment().setComplianceLevel(21);
        launcher.addInputResource(inputFile.toString());
        spoon.reflect.CtModel model = launcher.buildModel();

        Method method = TypeMutator.class.getDeclaredMethod("findTypes", spoon.reflect.CtModel.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<CtTypeReference<?>> types = (List<CtTypeReference<?>>) method.invoke(mutator, model);
        assertNotNull(types);
        assertTrue(types.size() >= 5, "Найдено типов: " + types.size());

        boolean hasInt = types.stream().anyMatch(t -> t.getSimpleName().equals("int"));
        boolean hasDouble = types.stream().anyMatch(t -> t.getSimpleName().equals("double"));
        boolean hasShort = types.stream().anyMatch(t -> t.getSimpleName().equals("short"));

        assertTrue(hasInt, "Тип int не найден");
        assertTrue(hasDouble, "Тип double не найден");
        assertTrue(hasShort, "Тип short не найден");
    }
}