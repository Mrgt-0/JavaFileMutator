package me.template;

import me.template.Core.JavaFileMutator;
import java.lang.reflect.InvocationTargetException;

class Main {
    void main() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String sourcePath = "C:\\Users\\Trita\\Downloads\\project-template-master\\project-template-master\\testFiles\\input\\V6001TypicalCases.java";
        String outputPath = "C:\\Users\\Trita\\Downloads\\project-template-master\\project-template-master\\testFiles\\output\\";

        JavaFileMutator javaFileMutator = new JavaFileMutator(sourcePath, outputPath);
        javaFileMutator.generateAllMutations();
    }
}
