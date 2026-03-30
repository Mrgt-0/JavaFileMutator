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

import java.util.ArrayList;
import java.util.List;

public class ComplicatingOperandMutator extends JavaAbstractMutator {
    private static final Logger log = LoggerFactory.getLogger(ComplicatingOperandMutator.class);
    public ComplicatingOperandMutator(String sourcePath, String outputDir) { super(sourcePath, outputDir); }

    @Override
    public List<String> generateMutants(){
        List<String> generatedFiles = new ArrayList<>();
        CtModel analysisModel = loadModel();
        List<CtBinaryOperator<?>> operators = findOperators(analysisModel);
        log.info("Найдено операторов для усложнения операндов: {}", operators.size());
        int index = 0;

        for (CtBinaryOperator<?> operator : operators) {
            BinaryOperatorKind kind = operator.getKind();

            Object[][] mutations = {
                    {" + 1", 1, "add_one"},
                    {" + 2", 2, "add_two"},
                    {" * 2", 2, "mul_two"},
                    {" - 1", 1, "sub_one"},
                    {" / 2", 2, "div_two"}
            };

            for (Object[] mut : mutations) {
                String operation = (String) mut[0];
                String name = (String) mut[2];
                Launcher mutantLauncher = loadLauncher();
                CtModel mutantModel = mutantLauncher.buildModel();

                CtBinaryOperator<?> targetOp = findOperatorByKind(
                        mutantModel.getElements(new TypeFilter<>(CtBinaryOperator.class)), kind);

                if (targetOp != null) {
                    complicateOperand(targetOp, operation);
                    String fileName = generateFileName(baseName, name, index);
                    saveMutant(mutantModel, fileName);
                    generatedFiles.add(fileName);
                    index++;
                }
            }
        }
        return generatedFiles;
    }

    private void complicateOperand(CtBinaryOperator<?> operator, String operation) {
        try {
            CtExpression<?> left = operator.getLeftHandOperand();
            CtExpression<?> right = operator.getRightHandOperand();
            String operatorStr = getOperatorString(operator.getKind());

            String newCode = "(" + left.toString() + operation + ") " +
                    operatorStr + " (" + right.toString() + operation + ")";

            CtExpression<?> newExpr = operator.getFactory().Code().createCodeSnippetExpression(newCode);
            operator.replace(newExpr);

        } catch (Exception e) {
            log.error("Ошибка: {}", e.getMessage());
        }
    }

    private String getOperatorString(BinaryOperatorKind kind) {
        switch (kind) {
            case EQ: return "==";
            case NE: return "!=";
            case LT: return "<";
            case GT: return ">";
            case LE: return "<=";
            case GE: return ">=";
            default: return kind.toString();
        }
    }

    private String generateFileName(String baseName, String operand, int index){
        return String.format("%s/%s_ComplicatingOperand_%s_%d.java",
                outputDir, baseName, operand, index);
    }

    private CtBinaryOperator<?> findOperatorByKind(List<CtBinaryOperator<?>> operators, BinaryOperatorKind kind){
        for(CtBinaryOperator<?> op : operators){
            if(op.getKind() == kind)
                return op;
        }
        return null;
    }
}
