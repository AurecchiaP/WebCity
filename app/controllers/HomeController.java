package controllers;

import play.mvc.*;

import java.io.*;
import java.nio.*;
import utils.*;

//import com.github.javaparser.*;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result index() {
//        try {
//            FileInputStream in = new FileInputStream("/Users/paolo/Documents/6th semester/thesis/commons-math/src/main/java/org/apache/commons/math4/geometry/VectorFormat.java");
//            System.out.println("file found");
//
//            CompilationUnit cu = JavaParser.parse(in);
//
//            // prints the resulting compilation unit to default system output
//            System.out.println(cu.toString());
//
//        } catch (FileNotFoundException e) {
//            System.out.println("failed to parse file");
//        }
//
//        File[] files = BasicParser.getFiles("/Users/paolo/Documents/6th semester/thesis/commons-math", ".java");
//        for (int i = 0; i < files.length; i++) {
//            System.out.println(files[i]);
//        }



        return ok(views.html.index.render());
    }

    public Result testo() {
        System.out.println("va");
        return ok("text");
    }

}
