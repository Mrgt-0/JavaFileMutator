package me.template;
import me.template.Core.JavaFileMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    void main() {
        try {
            String inputFolder = "testFiles/input";
            String outputFolder = "testFiles/output";
            File inputDir = new File(inputFolder);

            if (!inputDir.exists() || !inputDir.isDirectory()) {
                log.error("Папка не найдена: {}", inputFolder);
                log.info("Создайте папку {} и положите туда Java файлы", inputFolder);
                return;
            }
            new File(outputFolder).mkdirs();
            List<File> javaFiles = findJavaFiles(inputDir);

            if (javaFiles.isEmpty()) {
                log.error("В папке " + inputFolder + " нет Java файлов");
                return;
            }
            for (File javaFile : javaFiles) {
                String fileName = javaFile.getName();
                String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                String fileOutputFolder = outputFolder + "/" + baseName;

                JavaFileMutator mutator = new JavaFileMutator(
                        javaFile.getAbsolutePath(),
                        fileOutputFolder
                );
                mutator.generateAllMutations();
            }
        } catch (Exception e) {
            log.error("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<File> findJavaFiles(File folder) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    javaFiles.addAll(findJavaFiles(file));
                else if (file.getName().endsWith(".java"))
                    javaFiles.add(file);
            }
        }
        return javaFiles;
    }
}