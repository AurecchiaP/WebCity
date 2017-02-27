package controllers;

import play.mvc.*;
import utils.*;

import play.routing.JavaScriptReverseRouter;


import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.visitor.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.*;

import java.io.IOException;

import java.io.FileNotFoundException;

public class HomeController extends Controller {

    public Result javascriptRoutes() {
        return ok(
                JavaScriptReverseRouter.create("jsRoutes",
                        routes.javascript.HomeController.getClasses()
                )
        ).as("text/javascript");
    }

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result getClasses() {

        ArrayList<JavaClass> classes = new ArrayList<>();
        JavaPackage pkg = BasicParser.getPackage("/Users/paolo/Documents/6th semester/thesis/commons-math", ".java");

        /*files.forEach(file-> {
            try{
//                FileInputStream in = new FileInputStream(file);
//                CompilationUnit cu = JavaParser.parse(in);
//                new MethodVisitor().visit(cu, null);

            }
            catch(FileNotFoundException e) {
                System.out.println("file not found");
            }
            //TODO parse it
//            javaClass cls = new javaClass(file.toString(),40,50);
//            classes.add(cls);
        });

//        classes.forEach(cls->{
//            System.out.println(cls.getPath());
//        });
        */
        Gson gson = new Gson();
        String jsonClasses = gson.toJson(pkg);
//        System.out.println(jsonClasses);
        System.out.println(BasicParser.totClasses);
        //TODO toJSON
        return ok(jsonClasses);
    }
}
