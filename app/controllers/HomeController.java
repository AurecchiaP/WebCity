package controllers;

import play.mvc.*;
import utils.*;

import java.util.ArrayList;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;


public class HomeController extends Controller {

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result testo() {

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



        return ok("{}");
    }
}
