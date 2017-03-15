package controllers;

import com.google.gson.Gson;

import com.google.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
import utils.BasicParser;
import models.JavaPackage;
import utils.RectanglePacking;

import java.io.File;

import static utils.FileUtils.deleteDir;

public class HomeController extends Controller {

    private String currentRepo;
    private boolean web = false;

    @Inject
    FormFactory formFactory;


    /**
     * used by Play Framework to create routes callable from Javascript;
     * shouldn't be called manually
     */
    public Result javascriptRoutes() {
        return ok(
                JavaScriptReverseRouter.create("jsRoutes",
                        routes.javascript.HomeController.getVisualizationData(),
                        routes.javascript.HomeController.visualization()
                )
        ).as("text/javascript");
    }

    /**
     * default route for index page
     */
    public Result index() {

        return ok(views.html.index.render());
    }



    /**
     * downloads the linked repository, parses it, does the rectangle packing, and returns the data to be visualized
     */
    // FIXME should there be option to authenticate and use private repos?though then they
    // FIXME would be on the server

    // FIXME add a token sent from javascript, which javascript received from the server from `visualization`
    public Result getVisualizationData() {

        JavaPackage pkg;

        if(web) {
            // remove the old downloaded repository
            deleteDir(new File("/Users/paolo/Documents/6th semester/thesis/webcity/repository"));

            // try to download the given repository
            try {
                Git git = Git.cloneRepository()
                        .setURI(currentRepo)
                        .setDirectory(new File("/Users/paolo/Documents/6th semester/thesis/webcity/repository"))
                        .call();
            } catch (GitAPIException e) {
                System.out.println("failed to download repo");
                e.printStackTrace();
                return badRequest();
            }

            // parse the .java files of the repository
            pkg = BasicParser.parseRepo("/Users/paolo/Documents/6th semester/thesis/webcity/repository");
        }
        else {
            pkg = BasicParser.parseRepo("/Users/paolo/Documents/6th semester/thesis/commons-math");
        }

        System.out.println("Done downloading repo");

        // do the rectangle packing
        Gson gson = new Gson();
        new RectanglePacking(pkg);
        System.out.println("Done packing");
        String json = gson.toJson(pkg);

        // return the data to be drawn
        return ok(json);
    }

    /**
     * route for the visualisation page; not yet used
     */
    public Result visualization() {

        if(!web) return ok(views.html.index2.render());


        // get the link of the given repo
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String formRepo = dynamicForm.get("repo");

        // see if the repository is available/can be downloaded
        final LsRemoteCommand lsCmd = new LsRemoteCommand(null);
            lsCmd.setRemote(formRepo);
        try {
            System.out.println(lsCmd.call().toString());
            System.out.println("valid repo");
            currentRepo = formRepo;
            return ok(views.html.index2.render());
        } catch (GitAPIException e) {
            e.printStackTrace();
            System.out.println("invalid repo");
            return badRequest();
        }
    }
}
