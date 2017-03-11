package utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import models.JavaClass;
import models.JavaPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Utility class that takes the path of the root of a local repository and parses its contents, starting if possible
 * from the folder "main". For each package found a JavaPackage is created, containing possibly other JavaPackage or
 * JavaClass. The parsing of the .Java file found is done with the library JavaParser.
 */

public class BasicParser {

    // FIXME change this to thesis/repository (?) when using the download feature
    static String repoPath = "/Users/paolo/Documents/6th semester/thesis/";

    /**
     * Starts the recursive parsing of the repository corresponding to the given path.
     *
     * @param path a String path to the root of a local repository to be parsed
     * @return a JavaPackage which is the highest level package of the repository, containing the other packages and
     * classes recursively
     */
    public static JavaPackage parseRepo(String path) {
        Path p1 = Paths.get(path);
        try {
            Optional<Path> hit = Files.walk(p1)
                    .filter(file -> file.getFileName().equals(Paths.get("main")))
                    .findAny();

            if (hit.isPresent()) {
                return getPackage(hit.get().toString());
            } else {
                System.out.println("folder src/main not found");
                return getPackage(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * given the path of a folder, which represents a package, it creates a JavaPackage with the respective JavaPackage
     * and JavaClass which are contained in the folder.
     *
     * @param path the path to a folder that represents a package
     * @return the new JavaPackage that corresponds to the given path
     */
    private static JavaPackage getPackage(String path) {
        JavaPackage currentPackage = new JavaPackage(path.replaceFirst(repoPath, ""));
        List<JavaClass> clss = getClasses(path, currentPackage);
        currentPackage.setClasses(new ArrayList<>(clss));

        for (File file : new File(path).listFiles()) {
            if (file.isDirectory()) {
                JavaPackage pkg = getPackage(file.getPath());
                currentPackage.addChildPackage(pkg);
//                currentPackage.addClassTotal(pkg.getClasses().size());
            }
        }
        return currentPackage;
    }


    /**
     * Given a path and a JavaPackage that represents this path, it finds the .java files contained in it, parses them
     * with JavaParser, and adds the found classes to the package.
     *
     * @param path the path to the package that contains the .java files we want to parse
     * @param pkg  the JavaPackage that will contain the classes that we are parsing
     * @return a List of JavaClass contained in pkg
     */
    private static List<JavaClass> getClasses(String path, JavaPackage pkg) {
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory() && file.getPath().endsWith(".java")) {
                    FileInputStream in;
                    try {
                        in = new FileInputStream(file);
                        CompilationUnit cu = JavaParser.parse(in);

                        ClassVisitor cv = new ClassVisitor(pkg);
                        cv.visit(cu, null);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return pkg.getClasses();
    }


    /**
     * Class that takes care of parsing the .java files, looking for methods and attributes contained in each class
     * found in the files. See the library JavaParser.
     */
    private static class ClassVisitor extends VoidVisitorAdapter<Void> {

        private JavaPackage pkg;
        private JavaClass cls;
        private int methods = 0;
        private int attributes = 0;

        private ClassVisitor(JavaPackage pkg) {
            this.pkg = pkg;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {

            n.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(MethodDeclaration n, Void arg) {
                    methods += 1;
                }
            }, null);

            n.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(FieldDeclaration n, Void arg) {
                    attributes += 1;
                }
            }, null);

            cls = new JavaClass(n.getName().toString(), methods, attributes);
            pkg.addClass(cls);
            super.visit(n, arg);
        }
    }
}