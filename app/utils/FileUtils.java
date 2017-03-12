package utils;

import java.io.File;

public class FileUtils {

    /**
     * recursively deletes subfolders and files
     *
     * @param file the file from which we want to start deleting
     */
    public static void deleteDir(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
