package controllers;

import com.google.gson.Gson;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
import play.twirl.api.Content;
import utils.BasicParser;
import utils.JavaPackage;
import utils.CubePacking;

import java.io.File;

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

    public Result visualization() {
        redirect("visualization");
        return ok();
    }
}
