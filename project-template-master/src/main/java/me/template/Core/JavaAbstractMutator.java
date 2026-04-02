package me.template.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import java.io.File;
import java.io.IOException;
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
    public abstract List<String> generateMutants() throws IOException;

    protected void saveMutant(CtModel model, String fileName) throws IOException {
        File targetFile = new File(fileName);
        targetFile.getParentFile().mkdirs();

        if (model.getAllTypes() == null || model.getAllTypes().isEmpty()) {
            log.error("Модель не содержит типов для сохранения!");
            return;
        }
        CtType<?> mutatedType = model.getAllTypes().iterator().next();
        StringBuilder content = new StringBuilder();

        if (mutatedType.getPackage() != null && mutatedType.getPackage().getQualifiedName() != null) {
            String packageName = mutatedType.getPackage().getQualifiedName();
            if (!packageName.isEmpty())
                content.append("package ").append(packageName).append(";\n\n");
        }
        java.util.Set<String> imports = new java.util.TreeSet<>();
        collectImports(mutatedType, imports);
        for (String imp : imports) {
            if (!imp.startsWith("java.lang.") && !imp.equals(fileName + ".*"))
                content.append("import ").append(imp).append(";\n");
        }
        if (!imports.isEmpty())
            content.append("\n");

        content.append(mutatedType.toString());
        java.nio.file.Files.write(targetFile.toPath(), content.toString().getBytes());
        log.info("Мутант сохранен: {}", targetFile.getName());
    }

    private void collectImports(CtType<?> type, java.util.Set<String> imports) {
        List<spoon.reflect.reference.CtTypeReference<?>> references =
                type.getElements(new TypeFilter<>(spoon.reflect.reference.CtTypeReference.class));

        for (spoon.reflect.reference.CtTypeReference<?> ref : references) {
            String qName = ref.getQualifiedName();
            if (qName != null && !qName.isEmpty() && !qName.startsWith("java.lang.")) {
                if (!ref.isPrimitive() && !ref.isArray()) {
                    imports.add(qName);
                }
            }
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

    protected CtBinaryOperator<?> findOperatorByKindAndLine(List<CtBinaryOperator<?>> operators, int line, BinaryOperatorKind kind){
        for(CtBinaryOperator<?> op : operators){
            if(op.getPosition().getLine() == line && op.getKind() == kind)
                return op;
        }
        return null;
    }

    protected String getOperatorString(BinaryOperatorKind kind) {
        return switch (kind) {
            case EQ -> "==";
            case NE -> "!=";
            case LT -> "<";
            case GT -> ">";
            case LE -> "<=";
            case GE -> ">=";
            case AND -> "&&";
            case OR -> "||";
            case PLUS -> "+";
            case MINUS -> "-";
            case MUL -> "*";
            case DIV -> "/";
            case MOD -> "%";
            default -> kind.toString();
        };
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