package utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


import play.routing.JavaScriptReverseRouter;


import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.visitor.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import utils.*;

import java.io.IOException;

import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;


public class BasicParser {

    public static JavaPackage getPackage(String path, String extension) {
        JavaPackage currentPackage = new JavaPackage(path);
        currentPackage.setClasses(getClasses(path, extension));

        ArrayList<File> packageList = new ArrayList<>();
        for (File file : new File(path).listFiles()) {
            if (file.isDirectory()) {
                JavaPackage pkg = getPackage(file.getPath(), extension);
                currentPackage.addChild(pkg);
            }
        }
        return currentPackage;
    }

    private static ArrayList<JavaClass> getClasses(String path, String extension) {
        ArrayList<JavaClass> classesList = new ArrayList<>();
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory() && file.getPath().endsWith(extension)) {
                    try {
                        FileInputStream in = new FileInputStream(file);

                        CompilationUnit cu = JavaParser.parse(in);
//                        System.out.println(file);
                        new ClassVisitor().visit(cu, null);
                    } catch(Exception e) {
                        System.out.println("failed");
                    }
                    JavaClass cls = new JavaClass(file.getPath(), 5, 4);
                    classesList.add(cls);
                }
            }
        }
        return classesList;
    }

    private static class ClassVisitor extends VoidVisitorAdapter<Void> {

        private JavaClass cls;
        private int methods = 0;
        private int attributes = 0;

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
//            System.out.println(n.getName());

            n.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(MethodDeclaration n, Void arg) {
                    methods += 1;
//                    System.out.println("Method name: " + n.getName());
                }
            }, null);

            n.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(FieldDeclaration n, Void arg) {
                    attributes += 1;
//                    System.out.println("Field name: " + n);
                }
            }, null);

            cls = new JavaClass(n.getName().toString(), methods, attributes);
//            System.out.println("class:" + n.getName() + " " + methods + " " + attributes);
            super.visit(n, arg);
        }
    }
}