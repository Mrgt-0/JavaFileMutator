package me.template;

class Main {
    void main() {
        String sourcePath = "C:\\Users\\Trita\\Downloads\\project-template-master\\project-template-master\\testFiles\\input\\V6001TypicalCases.java";
        String outputPath = "C:\\Users\\Trita\\Downloads\\project-template-master\\project-template-master\\testFiles\\output\\";

        JavaFileMutator javaFileMutator = new JavaFileMutator(sourcePath, outputPath);
        javaFileMutator.generateAllMutations();
    }
}
