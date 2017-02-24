package utils;

import java.io.File;
import java.io.FilenameFilter;



public class BasicParser {

    public static File[] getFiles(String dirName, String extension){
        File dir = new File(dirName);
        System.out.println(dir.exists()?"dir exists":"dir doesn't exist");
        System.out.println(dir.getAbsolutePath());


        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename)
            { return filename.endsWith(extension); }
        } );

    }

}