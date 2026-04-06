package me.template.Mutators;

import me.template.Core.JavaAbstractMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.filter.TypeFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BracketMutator extends JavaAbstractMutator {
    private static final Logger log = LoggerFactory.getLogger(BracketMutator.class);
    public BracketMutator(String sourcePath, String outputDir) { super(sourcePath, outputDir); }

    @Override
    public List<String> generateMutants() throws IOException {
        List<String> generatedFiles = new ArrayList<>();
        CtModel analysisModel = loadModel();
        List<CtBinaryOperator<?>> operators = findOperators(analysisModel);
        log.info("Найдено операторов для добавления скобок: {}", operators.size());
        int index = 0;

        for (CtBinaryOperator<?> operator : operators) {
            BinaryOperatorKind kind = operator.getKind();
            int line = operator.getPosition().getLine();

            Launcher mutantLauncher = loadLauncher();
            CtModel mutantModel = mutantLauncher.buildModel();
            List<CtBinaryOperator<?>> mutantOps = mutantModel.getElements(new TypeFilter<>(CtBinaryOperator.class));
            CtBinaryOperator<?> targetOp = findOperatorByKindAndLine(mutantOps, line, kind);

            if (targetOp != null) {
                addParentheses(targetOp);
                String fileName = generateFileName(baseName, index);
                saveMutant(mutantModel, fileName);
                generatedFiles.add(fileName);

                log.info("Создан мутант #{}: {}",
                        index + 1, new File(fileName).getName());
                index++;
            }
        }
        log.info("Всего создано мутантов: {}", generatedFiles.size());
        return generatedFiles;
    }

    private void addParentheses(CtBinaryOperator<?> operator) {
        try {
            CtExpression<?> left = operator.getLeftHandOperand();
            CtExpression<?> right = operator.getRightHandOperand();
            String operatorStr = getOperatorString(operator.getKind());

            String newCode = "(" + left.toString() + ") " + operatorStr + " (" + right.toString() + ")";

            CtExpression<?> newExpr = operator.getFactory().Code().createCodeSnippetExpression(newCode);
            operator.replace(newExpr);

        } catch (Exception e) {
            log.error("Ошибка при добавлении скобок: {}", e.getMessage());
        }
    }


    private String generateFileName(String baseName, int index) {
        return String.format("%s/%s_AddParentheses_%d.java",
                outputDir, baseName, index);
    }
}