package controllers;

import play.mvc.*;
import utils.*;

import play.routing.JavaScriptReverseRouter;


import java.util.ArrayList;
import java.io.File;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;


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

        ArrayList<javaClass> classes = new ArrayList<>();
        ArrayList<File> files = BasicParser.getFiles("/Users/paolo/Documents/6th semester/thesis/commons-math", ".java");
        files.forEach(file-> {
            //TODO parse it
            javaClass cls = new javaClass(file.toString(),4,5);
            classes.add(cls);
        });

        classes.forEach(cls->{
            System.out.println(cls.getPath());
        });


        //TODO toJSON
        return ok("{}");
    }
}
