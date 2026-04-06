package me.template.Mutators;
import me.template.Core.JavaAbstractMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.visitor.filter.TypeFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OperatorMutator extends JavaAbstractMutator {
    private static final Logger log = LoggerFactory.getLogger(OperatorMutator.class);
    public OperatorMutator(String sourcePath, String outputDir) {
        super(sourcePath, outputDir);
    }

    @Override
    public List<String> generateMutants() throws IOException {
        CtModel analysisModel = loadModel();
        List<String> generatedFiles = new ArrayList<>();
        List<CtBinaryOperator<?>> operators = findOperators(analysisModel);
        log.info("Найдено операторов для мутации: {}", operators.size());
        int index = 0;

        for (CtBinaryOperator<?> op : operators) {
            BinaryOperatorKind oldKind = op.getKind();
            BinaryOperatorKind newKind = getMutatedKind(oldKind);
            CtModel mutantModel = loadLauncher().buildModel();
            List<CtBinaryOperator<?>> mutantOps = mutantModel.getElements(new TypeFilter<>(CtBinaryOperator.class));

            for (CtBinaryOperator<?> mutantOp : mutantOps) {
                if (mutantOp.getKind() == oldKind) {
                    mutantOp.setKind(newKind);
                    break;
                }
            }
            String oldOpStr = operatorToString(oldKind);
            String newOpStr = operatorToString(newKind);
            String fileName = generateFileName(baseName, oldOpStr, newOpStr, index);
            saveMutant(mutantModel, fileName);
            generatedFiles.add(fileName);
            log.info("Создан мутант #{}: {} ({} -> {})",
                    index + 1, new File(fileName).getName(), oldKind, newKind);

            index++;
        }
        log.info("Всего создано мутантов: " + generatedFiles.size());
        return generatedFiles;
    }

    public BinaryOperatorKind getMutatedKind(BinaryOperatorKind kind) {
        switch (kind){
            case EQ -> { return BinaryOperatorKind.NE; }
            case NE -> { return BinaryOperatorKind.EQ; }
            case SL -> { return BinaryOperatorKind.SR; }
            case SR -> { return BinaryOperatorKind.SL; }
            case LT -> { return BinaryOperatorKind.GT; }
            case LE -> { return BinaryOperatorKind.GE; }
            case GT -> { return BinaryOperatorKind.LT; }
            case GE -> { return BinaryOperatorKind.LE; }
        }
        return null;
    }

    public String generateFileName(String baseName, String oldOp, String newOp, int index) {
        return String.format("%s/%s_ReplaceOperator_%s_%s_%d.java",
                outputDir, baseName, oldOp, newOp, index);
    }

    public String operatorToString(BinaryOperatorKind kind) {
        return switch (kind) {
            case EQ -> "eq";
            case NE -> "ne";
            case SL -> "sl";
            case SR -> "sr";
            case LT -> "lt";
            case LE -> "le";
            case GT -> "gt";
            case GE -> "ge";
            default -> kind.toString().toLowerCase();
        };
    }
}