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

public class AddCastMutator extends JavaAbstractMutator {
    private static final Logger log = LoggerFactory.getLogger(AddCastMutator.class);
    public AddCastMutator(String sourcePath, String outputDir) { super(sourcePath, outputDir); }

    @Override
    public List<String> generateMutants() throws IOException {
        List<String> generatedFiles = new ArrayList<>();
        CtModel analysisModel = loadModel();
        List<CtBinaryOperator<?>> operators = findOperators(analysisModel);
        log.info("Найдено операторов для добавления кастов: {}", operators.size());
        int index = 0;
        for (CtBinaryOperator<?> operator : operators) {
            int line = operator.getPosition().getLine();
            BinaryOperatorKind kind = operator.getKind();

            String targetType = operator.getLeftHandOperand().getType().getSimpleName();
            if (!operator.getLeftHandOperand().getType().isPrimitive())
                targetType = operator.getLeftHandOperand().getType().getQualifiedName();

            Launcher mutantLauncher = loadLauncher();
            CtModel mutantModel = mutantLauncher.buildModel();
            CtBinaryOperator<?> targetOp = findOperatorByKindAndLine(
                    mutantModel.getElements(new TypeFilter<>(CtBinaryOperator.class)), line, kind);

            if (targetOp != null) {
                addCast(targetOp, targetType);
                String fileName = generateFileName(baseName, targetType, index);
                saveMutant(mutantModel, fileName);
                generatedFiles.add(fileName);
                log.info("Создан мутант: {} (cast to {}) в строке {}",
                        new File(fileName).getName(), targetType, line);
                index++;
            }
        }
        return generatedFiles;
    }

    private void addCast(CtBinaryOperator<?> operator, String castType) {
        CtExpression<?> left = operator.getLeftHandOperand();
        CtExpression<?> right = operator.getRightHandOperand();
        Factory factory = operator.getFactory();
        String operatorStr = getOperatorString(operator.getKind());
        String newLeftCode = "(" + castType + ") " + left.toString();
        String newRightCode = "(" + castType + ") " + right.toString();
        String newCode = newLeftCode + " " + operatorStr + " " + newRightCode;

        CtExpression<?> newExpr = factory.Code().createCodeSnippetExpression(newCode);
        operator.replace(newExpr);
    }

    private String generateFileName(String baseName, String cast, int index) {
        String cleanCastType = cast.replace('.', '_').replace('$', '_');
        return String.format("%s/%s_AddCast_%s_%d.java",
                outputDir, baseName, cleanCastType, index);
    }
}