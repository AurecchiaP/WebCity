package utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;


public class BasicParser {

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

    private static JavaPackage getPackage(String path) {
        JavaPackage currentPackage = new JavaPackage(path);
        currentPackage.setClasses(getClasses(path, currentPackage));

        for (File file : new File(path).listFiles()) {
            if (file.isDirectory()) {
                JavaPackage pkg = getPackage(file.getPath());
                currentPackage.addChild(pkg);
            }
        }
        return currentPackage;
    }


    private static ArrayList<JavaClass> getClasses(String path, JavaPackage pkg) {
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