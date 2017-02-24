package utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BasicParser {

    public static ArrayList<File> getFiles(String path, String extension) {
        ArrayList<File> files = new ArrayList<>();
        try {
            Files.walk(Paths.get(path))
                    .filter((p) -> !p.toFile().isDirectory() && p.toFile().getAbsolutePath().endsWith(extension))
                    .forEach((p) -> files.add(p.toFile()));
        } catch (Exception e) {
            System.out.println("failed");
        }
        return files;
    }
}