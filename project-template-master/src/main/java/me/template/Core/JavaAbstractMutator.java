package me.template.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class JavaAbstractMutator {
    private static final Logger log = LoggerFactory.getLogger(JavaAbstractMutator.class);
    protected final String sourcePath;
    protected final String outputDir;
    protected final String baseName;

    public JavaAbstractMutator(String sourcePath, String outputDir){
        this.sourcePath = sourcePath;
        this.outputDir = outputDir;
        String name = new File(sourcePath).getName();
        this.baseName = name.substring(0, name.lastIndexOf('.'));
    }
    public abstract List<String> generateMutants();

    protected void saveMutant(CtModel model, String fileName){
        try {
            File targetFile = new File(fileName);
            targetFile.getParentFile().mkdirs();

            if (model.getAllTypes() == null || model.getAllTypes().isEmpty()) {
                log.error("Модель не содержит типов для сохранения!");
                return;
            }
            CtType<?> type = model.getAllTypes().iterator().next();
            StringBuilder content = new StringBuilder();

            if (type.getPackage() != null && type.getPackage().getQualifiedName() != null) {
                String packageName = type.getPackage().getQualifiedName();
                if (!packageName.isEmpty())
                    content.append("package ").append(packageName).append(";\n\n");
            }

            // не добавляет импорты
            content.append(type.toString());

            java.nio.file.Files.write(targetFile.toPath(), content.toString().getBytes());
            log.info("Мутант сохранен: {}", targetFile.getName());

        } catch (Exception e) {
            log.error("Ошибка при сохранении мутанта {}: {}", fileName, e.getMessage());
            e.printStackTrace();
        }
    }

    protected List<CtBinaryOperator<?>> findOperators(CtModel model){
        List<CtBinaryOperator<?>> operators = model.getElements(new TypeFilter<>(CtBinaryOperator.class));
        List<CtBinaryOperator<?>> mutatedOperators = new ArrayList<>();
        for(CtBinaryOperator<?> op : operators){
            if(op.getKind() == BinaryOperatorKind.EQ ||
                    op.getKind() == BinaryOperatorKind.NE ||
                    op.getKind() == BinaryOperatorKind.SR ||
                    op.getKind() == BinaryOperatorKind.SL ||
                    op.getKind() == BinaryOperatorKind.LT ||
                    op.getKind() == BinaryOperatorKind.LE ||
                    op.getKind() == BinaryOperatorKind.GT ||
                    op.getKind() == BinaryOperatorKind.GE)
                mutatedOperators.add(op);
        }
        return mutatedOperators;
    }

    protected CtModel loadModel(){
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(21);
        launcher.addInputResource(sourcePath);
        return launcher.buildModel();
    }

    protected Launcher loadLauncher(){
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(21);
        launcher.addInputResource(sourcePath);
        return launcher;
    }
}