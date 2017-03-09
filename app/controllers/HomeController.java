package controllers;

import com.google.gson.Gson;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
import utils.BasicParser;
import models.JavaPackage;
import utils.CubePacking;

import java.io.File;

public class HomeController extends Controller {

    /**
     * Used by Play Framework to create routes callable from Javascript.
     * Shouldn't be called manually.
     */
    public Result javascriptRoutes() {
        return ok(
                JavaScriptReverseRouter.create("jsRoutes",
                        routes.javascript.HomeController.getClasses()
                )
        ).as("text/javascript");
    }

    /**
     * Route for index page
     */
    public Result index() {
        return ok(views.html.index.render());
    }


    // TODO move to utils
    void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public Result getClasses() {
        // FIXME do this properly; should there be option to authenticate and use private repos?though then they
        // FIXME would be on the server

        boolean web = false;
        JavaPackage pkg;
        if(web) {
            deleteDir(new File("/Users/paolo/Documents/6th semester/thesis/webcity/repository"));
            try {
                Git git = Git.cloneRepository()
                        .setURI("https://github.com/junit-team/junit4.git")
                        .setDirectory(new File("/Users/paolo/Documents/6th semester/thesis/webcity/repository"))
                        .call();
            } catch (GitAPIException e) {
                System.out.println("failed to download repo");
                e.printStackTrace();
            }

            pkg = BasicParser.parseRepo("/Users/paolo/Documents/6th semester/thesis/webcity/repository");
        }
        else {
            pkg = BasicParser.parseRepo("/Users/paolo/Documents/6th semester/thesis/commons-math");
        }

        Gson gson = new Gson();
        new CubePacking(pkg);
        String json = gson.toJson(pkg);



        return ok(json);
    }

    /**
     * Route for the visualisation page; not yet used.
     */
    public Result visualization() {
        redirect("visualization");
        return ok();
    }
}
