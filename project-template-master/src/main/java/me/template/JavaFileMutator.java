package me.template;
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

public class JavaFileMutator {
    private static final Logger log = LoggerFactory.getLogger(JavaFileMutator.class);
    private final String sourcePath;
    private final String outputDir;
    private final Launcher spoon;
    private CtModel model;

    public JavaFileMutator(String sourcePath, String outputDir){
        this.sourcePath = sourcePath;
        this.outputDir = outputDir;
        spoon = new Launcher();
        spoon.getEnvironment().setComplianceLevel(21);
        spoon.addInputResource(sourcePath);
        model = spoon.buildModel();
    }

    public List<String> generateAllMutations(){
        List<String> generatedFiles = new ArrayList<>();
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists())
            outputDirectory.mkdirs();

        String baseName = new File(sourcePath).getName();
        baseName = baseName.substring(0, baseName.lastIndexOf('.'));

        List<CtBinaryOperator<?>> operators = findOperators();
        log.info("Найдено операторов для мутации: " + operators.size());

        for(int i = 0; i < operators.size(); i++){
            Launcher mutantLauncher = new Launcher();
            mutantLauncher.getEnvironment().setComplianceLevel(21);
            mutantLauncher.addInputResource(sourcePath);

            CtModel mutantModel = mutantLauncher.buildModel();

            List<CtBinaryOperator<?>> mutantOps = mutantModel.getElements(new TypeFilter<>(CtBinaryOperator.class));
            List<CtBinaryOperator<?>> targetOps = new ArrayList<>();

            for (CtBinaryOperator<?> op : mutantOps) {
                if (op.getKind() == BinaryOperatorKind.EQ ||
                        op.getKind() == BinaryOperatorKind.NE ||
                        op.getKind() == BinaryOperatorKind.SR ||
                        op.getKind() == BinaryOperatorKind.SL) {
                    targetOps.add(op);
                }
            }

            if (i < targetOps.size()) {
                CtBinaryOperator<?> op = targetOps.get(i);
                BinaryOperatorKind oldKind = op.getKind();
                BinaryOperatorKind newKind = getMutatedKind(oldKind);

                op.setKind(newKind);
                String fileName = generateMutationFileName(baseName, oldKind, newKind, i);
                saveMutant(mutantLauncher, fileName);
                generatedFiles.add(fileName);
                log.info("Создан мутант #{}: {} ({} -> {})",
                        i + 1, fileName, oldKind, newKind);
            }
        }
        log.info("Всего создано мутантов: " + generatedFiles.size());
        return generatedFiles;
    }

    private List<CtBinaryOperator<?>> findOperators(){
        List<CtBinaryOperator<?>> operators = model.getElements(new TypeFilter<>(CtBinaryOperator.class));
        List<CtBinaryOperator<?>> mutatedOperators = new ArrayList<>();
        for(CtBinaryOperator<?> op : operators){
            if(op.getKind() == BinaryOperatorKind.EQ ||
            op.getKind() == BinaryOperatorKind.NE ||
            op.getKind() == BinaryOperatorKind.SR ||
            op.getKind() == BinaryOperatorKind.SL)
                mutatedOperators.add(op);
        }
        return mutatedOperators;
    }

    private BinaryOperatorKind getMutatedKind(BinaryOperatorKind kind) {
        if (kind == BinaryOperatorKind.EQ) return BinaryOperatorKind.NE;
        if (kind == BinaryOperatorKind.NE) return BinaryOperatorKind.EQ;
        if (kind == BinaryOperatorKind.SL) return BinaryOperatorKind.SR;
        if (kind == BinaryOperatorKind.SR) return BinaryOperatorKind.SL;
        return kind;
    }

    private void saveMutant(Launcher mutantLauncher, String fileName){
        try {
            File targetFile = new File(fileName);
            targetFile.getParentFile().mkdirs();
            CtModel model = mutantLauncher.getModel();

            CtType<?> type = model.getAllTypes().iterator().next();
            StringBuilder content = new StringBuilder();
            if (type.getPackage() != null)
                content.append("package ").append(type.getPackage().getQualifiedName()).append(";\n\n");

            content.append(type.toString());
            java.nio.file.Files.write(targetFile.toPath(), content.toString().getBytes());

        } catch (Exception e) {
            log.error("Ошибка при сохранении мутанта {}: {}", fileName, e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateMutationFileName(String baseName,
                                            BinaryOperatorKind oldKind,
                                            BinaryOperatorKind newKind,
                                            int index) {
        String oldOp = operatorToString(oldKind);
        String newOp = operatorToString(newKind);

        return String.format("%s/%s_ReplaceOperator_%s_%s_%d.java",
                outputDir, baseName, oldOp, newOp, index);
    }

    private String operatorToString(BinaryOperatorKind kind) {
        switch (kind) {
            case EQ: return "eq";
            case NE: return "ne";
            case SL: return "sl";
            case SR: return "sr";
            case PLUS: return "plus";
            case MINUS: return "minus";
            case MUL: return "mul";
            case DIV: return "div";
            case AND: return "and";
            case OR: return "or";
            case GT: return "gt";
            case LT: return "lt";
            case GE: return "ge";
            case LE: return "le";
            default: return kind.toString().toLowerCase();
        }
    }
}