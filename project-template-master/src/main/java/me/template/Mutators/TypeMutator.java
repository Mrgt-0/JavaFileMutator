package me.template.Mutators;

import me.template.Core.JavaAbstractMutator;
import spoon.reflect.factory.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import java.util.ArrayList;
import java.util.List;

public class TypeMutator extends JavaAbstractMutator {
    private static final Logger log = LoggerFactory.getLogger(TypeMutator.class);
    public TypeMutator(String sourcePath, String outputDir) { super(sourcePath, outputDir); }

    @Override
    public List<String> generateMutants(){
        CtModel analysisModel = loadModel();
        List<String> generatedFiles = new ArrayList<>();
        List<CtTypeReference<?>> types = findTypes(analysisModel);
        int index = 0;

        for (CtTypeReference<?> type : types) {
            CtTypeReference<?> oldType = type;
            CtTypeReference<?> newType = getMutatedType(type);

            if (newType != null) {
                CtModel mutantModel = loadLauncher().buildModel();
                List<CtVariable<?>> variables = mutantModel.getElements(new TypeFilter<>(CtVariable.class));

                for (CtVariable<?> variable : variables) {
                    if (variable.getType().equals(oldType))
                        variable.setType(newType);
                }
                String fileName = generateFileName(baseName, oldType.toString(), newType.toString(), index);
                saveMutant(mutantModel, fileName);
                generatedFiles.add(fileName);
                index++;
            }
        }
        return generatedFiles;
    }

    private List<CtTypeReference<?>> findTypes(CtModel model){
        List<CtVariable<?>> variables = model.getElements(new TypeFilter<>(CtVariable.class));
        List<CtTypeReference<?>> types = new ArrayList<>();
        for (CtVariable<?> variable : variables)
            types.add(variable.getType());
        return types;
    }

    private CtTypeReference<?> getMutatedType(CtTypeReference<?> type) {
        Factory factory = loadModel().getRootPackage().getFactory();
        String simpleName = type.getSimpleName();
        switch (simpleName){
            case "int" -> { return factory.Type().createReference(Long.TYPE); }
            case "double" -> { return factory.Type().createReference(Float.TYPE); }
            case "short" -> { return factory.Type().createReference(Integer.TYPE); }
            default -> { return null; }
        }
    }

    private String generateFileName(String baseName, String oldType, String newType, int index){
        return String.format("%s/%s_ReplaceType_%s_%s_%d.java",
                outputDir, baseName, oldType, newType, index);
    }
}