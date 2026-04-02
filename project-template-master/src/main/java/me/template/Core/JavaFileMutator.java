package me.template.Core;
import me.template.Mutators.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class JavaFileMutator {
    private final List<JavaAbstractMutator> mutators;
    private static final Logger log = LoggerFactory.getLogger(JavaFileMutator.class);
    private final String outputDir;

    public JavaFileMutator(String sourcePath, String outputDir){
        this.mutators = new ArrayList<>();
        this.mutators.add(new OperatorMutator(sourcePath, outputDir));
        this.mutators.add(new TypeMutator(sourcePath, outputDir));
        this.mutators.add(new BracketMutator(sourcePath, outputDir));
        this.mutators.add(new ComplicatingOperandMutator(sourcePath, outputDir));
        this.mutators.add(new AddCastMutator(sourcePath, outputDir));
        this.mutators.add(new ChangeContextMutator(sourcePath, outputDir));
        this.outputDir = outputDir;
    }

    public void generateAllMutations() throws IOException {
        List<String> generatedFiles = new ArrayList<>();
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists())
            outputDirectory.mkdirs();

        for(JavaAbstractMutator mutator : mutators){
            List<String> files = mutator.generateMutants();
            generatedFiles.addAll(files);
        }
        log.info("Всего создано мутантов: {}", generatedFiles.size());
    }
}