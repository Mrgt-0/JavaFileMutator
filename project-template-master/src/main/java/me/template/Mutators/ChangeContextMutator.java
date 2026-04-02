package me.template.Mutators;

import me.template.Core.JavaAbstractMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChangeContextMutator extends JavaAbstractMutator {
    private static final Logger log = LoggerFactory.getLogger(ChangeContextMutator.class);
    public ChangeContextMutator(String sourcePath, String outputDir) { super(sourcePath, outputDir); }

    @Override
    public List<String> generateMutants() throws IOException {
        List<String> generatedFiles = new ArrayList<>();
        CtModel analysisModel = loadModel();
        List<CtBinaryOperator<?>> comparisons = findComparisonOperators(analysisModel);

        log.info("Найдено выражений для изменения контекста: {}", comparisons.size());
        int index = 0;

        for (CtBinaryOperator<?> comparison : comparisons) {
            int line = comparison.getPosition().getLine();
            Launcher mutantLauncher2 = loadLauncher();
            CtModel mutantModel2 = mutantLauncher2.buildModel();
            CtBinaryOperator<?> targetOp2 = findOperatorByKindAndLine(
                    mutantModel2.getElements(new TypeFilter<>(CtBinaryOperator.class)),
                    line, comparison.getKind());
            if (targetOp2 != null) {
                wrapInTernaryOperator(targetOp2);
                String fileName2 = String.format("%s/%s_Context_Ternary_%d.java",
                        outputDir, baseName, index);
                saveMutant(mutantModel2, fileName2);
                generatedFiles.add(fileName2);
                log.info("Создан мутант: {} (обернут в тернарный оператор) в строке {}",
                        new File(fileName2).getName(), line);
                index++;
            }

            Launcher mutantLauncher3 = loadLauncher();
            CtModel mutantModel3 = mutantLauncher3.buildModel();
            CtBinaryOperator<?> targetOp3 = findOperatorByKindAndLine(
                    mutantModel3.getElements(new TypeFilter<>(CtBinaryOperator.class)),
                    line, comparison.getKind());
            if (targetOp3 != null) {
                wrapInAssert(targetOp3);
                String fileName3 = generateFileName(baseName, index);
                saveMutant(mutantModel3, fileName3);
                generatedFiles.add(fileName3);
                log.info("Создан мутант: {} (обернут в assert) в строке {}",
                        new File(fileName3).getName(), line);
                index++;
            }
        }
        return generatedFiles;
    }

    private List<CtBinaryOperator<?>> findComparisonOperators(CtModel model) {
        List<CtBinaryOperator<?>> operators = model.getElements(new TypeFilter<>(CtBinaryOperator.class));
        List<CtBinaryOperator<?>> comparisons = new ArrayList<>();

        for (CtBinaryOperator<?> op : operators) {
            BinaryOperatorKind kind = op.getKind();
            if (kind == BinaryOperatorKind.EQ || kind == BinaryOperatorKind.NE ||
                    kind == BinaryOperatorKind.LT || kind == BinaryOperatorKind.GT ||
                    kind == BinaryOperatorKind.LE || kind == BinaryOperatorKind.GE) {
                comparisons.add(op);
            }
        }
        return comparisons;
    }

    private void wrapInTernaryOperator(CtBinaryOperator<?> operator) {
        Factory factory = operator.getFactory();
        String exprStr = operator.toString();
        String newCode = "(" + exprStr + ") ? true : false";
        CtExpression<?> newExpr = factory.Code().createCodeSnippetExpression(newCode);
        operator.replace(newExpr);
    }

    private void wrapInAssert(CtBinaryOperator<?> operator) {
        Factory factory = operator.getFactory();
        String exprStr = operator.toString();
        String newCode = "assert " + exprStr + " : \"" + exprStr + "\";";
        CtExpression<?> newExpr = factory.Code().createCodeSnippetExpression(newCode);
        operator.replace(newExpr);
    }

    private String generateFileName(String baseName, int index) {
        return String.format("%s/%s_ChangeContext_%d.java",
                outputDir, baseName, index);
    }
}