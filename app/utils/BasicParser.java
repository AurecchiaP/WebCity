package utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import models.*;
import models.metrics.LinesOfCode;
import models.metrics.NumberOfAttributes;
import models.metrics.NumberOfMethods;
import play.api.Play;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that takes the path of the root of a local repository and parses its contents. For each package found
 * a JavaPackage is created, containing possibly other JavaPackage or JavaClass. The parsing of the .Java file found
 * is done with the library JavaParser.
 */
public class BasicParser {
    
    private static String repoPath;

    /**
     * Starts the recursive parsing of the repository corresponding to the given path.
     *
     * @param path a String path to the root of a local repository to be parsed
     * @return a JavaPackage which is the highest level package of the repository, containing the other packages and
     * classes recursively
     */
    public static JavaPackage parseRepo(String path) {
        repoPath = path;
        return makePackage(path);
    }

    /**
     * given the path of a folder, which represents a package, it creates a JavaPackage with the respective JavaPackage
     * and JavaClass which are contained in the folder.
     *
     * @param path the path to a folder that represents a package
     * @return the new JavaPackage that corresponds to the given path
     */
    private static JavaPackage makePackage(String path) {
        // create a new JavaPackage for the given path
        JavaPackage currentPackage = new JavaPackage(path.replaceFirst(repoPath, ""));
        // find the list of classes contained directly in this package and add then to it
        List<JavaClass> clss = findClasses(path, currentPackage);
        currentPackage.setClasses(new ArrayList<>(clss));

        // for each sub-directory of pkg, recursively call makePackage, and then add them as children to this package
        for (File file : new File(path).listFiles()) {
            if (file.isDirectory()) {
                JavaPackage pkg = makePackage(file.getPath());
                if(pkg.getClasses().size() > 0 || pkg.getChildPackages().size() > 0) {
                    currentPackage.addChildPackage(pkg);
                }
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
    private static List<JavaClass> findClasses(String path, JavaPackage pkg) {

        // get the list of files contained in the given path
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {

                // if the file is not a directory and it is a .java file, parse it, and add its contained classes to
                // the JavaPackage pkg
                if (!file.isDirectory() && file.getPath().endsWith(".java")) {
                    FileInputStream in;
                    try {
                        in = new FileInputStream(file);
//                        System.out.println(file.getName());
                        CompilationUnit cu = JavaParser.parse(in);

                        ClassVisitor cv = new ClassVisitor(pkg, file.getPath().replaceFirst(repoPath, ""));
                        cv.visit(cu, null);

                    } catch (IOException e) {
                        System.out.println("IO reading exception in file " + file.getName() + ", file will be ignored");
                    }
                    catch (ParseProblemException e) {
                        System.out.println("Parse exception in file " + file.getName() + ", file will be ignored");
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
        private String filename;
        private int numberOfLines = 0;

        private ClassVisitor(JavaPackage pkg, String filename) {
            this.pkg = pkg;
            this.filename = filename;

        }

        // for each class found
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {

            // if the node class n has some content, set it's number of lines
            if (n.getBegin().isPresent() && n.getEnd().isPresent()) {
                numberOfLines = n.getEnd().get().line - n.getBegin().get().line + 1;
            } else {
                numberOfLines = 0;
            }

            // create the new JavaClass with the given number of methods and attributes, and add it to the parent package
            cls = new JavaClass(filename,
                    n.getName().toString(),
                    new NumberOfMethods(n.getMethods().size()),
                    new NumberOfAttributes(n.getFields().size()),
                    new LinesOfCode(numberOfLines));


            pkg.addClass(cls);
            super.visit(n, arg);
        }
    }
}