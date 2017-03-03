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
        JavaPackage pkg = BasicParser.buildStructure("/Users/paolo/Documents/6th semester/thesis/commons-math", ".java");

        Gson gson = new Gson();
        String jsonClasses = gson.toJson(pkg);
        return ok(jsonClasses);
    }
}
