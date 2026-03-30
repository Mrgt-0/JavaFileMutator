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
import java.util.ArrayList;
import java.util.List;

public class BracketMutator extends JavaAbstractMutator {
    private static final Logger log = LoggerFactory.getLogger(BracketMutator.class);
    public BracketMutator(String sourcePath, String outputDir) { super(sourcePath, outputDir); }

    @Override
    public List<String> generateMutants(){
        List<String> generatedFiles = new ArrayList<>();
        CtModel analysisModel = loadModel();
        List<CtBinaryOperator<?>> operators = findOperators(analysisModel);
        log.info("Найдено операторов для добавления скобок: {}", operators.size());
        int index = 0;

        for (CtBinaryOperator<?> operator : operators) {
            BinaryOperatorKind kind = operator.getKind();

            Launcher mutantLauncher = loadLauncher();
            CtModel mutantModel = mutantLauncher.buildModel();
            List<CtBinaryOperator<?>> mutantOps = mutantModel.getElements(new TypeFilter<>(CtBinaryOperator.class));
            CtBinaryOperator<?> targetOp = findOperatorByKind(mutantOps, kind);

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

    private CtBinaryOperator<?> findOperatorByKind(List<CtBinaryOperator<?>> operators, BinaryOperatorKind kind){
        for(CtBinaryOperator<?> op : operators){
            if(op.getKind() == kind)
                return op;
        }
        return null;
    }

    private void addParentheses(CtBinaryOperator<?> operator) {
        CtExpression<?> left = operator.getLeftHandOperand();
        CtExpression<?> right = operator.getRightHandOperand();

        String operatorStr = getOperatorString(operator.getKind());
        String newCode = "(" + left.toString() + ") " + operatorStr + " (" + right.toString() + ")";

        CtExpression<?> newExpr = operator.getFactory().Code().createCodeSnippetExpression(newCode);
        operator.replace(newExpr);
    }

    private String generateFileName(String baseName, int index) {
        return String.format("%s/%s_AddParentheses_%d.java",
                outputDir, baseName, index);
    }

    private String getOperatorString(BinaryOperatorKind kind) {
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
}