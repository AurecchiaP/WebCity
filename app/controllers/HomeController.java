package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import models.drawables.DrawablePackage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
import utils.BasicParser;
import models.JavaPackage;
import utils.RectanglePacking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static utils.DrawableUtils.toDrawable;
import static utils.FileUtils.deleteDir;
import static utils.JSON.toJSON;

public class HomeController extends Controller {

    private String currentRepo;
    private boolean web;
    private double percentage = 0;
    private int taskNumber = 0;
    private String taskName;

    @Inject
    private FormFactory formFactory;


    /**
     * used by Play Framework to create routes callable from Javascript;
     * shouldn't be called manually
     */
    public Result javascriptRoutes() {
        return ok(
                JavaScriptReverseRouter.create("jsRoutes",
                        routes.javascript.HomeController.getVisualizationData(),
                        routes.javascript.HomeController.visualization(),
                        routes.javascript.HomeController.poll()
                )
        ).as("text/javascript");
    }

    /**
     * default route for index page
     */
    public Result index() {
        try {
            FileRepository localRepo = new FileRepository("/Users/paolo/Documents/6th semester/thesis/webcity/.git");
            Git git = new Git(localRepo);
            String version = "";
            if (git.tagList().call().iterator().hasNext()) {
                version = git.tagList().call().get(git.tagList().call().size() - 1).getName().replace("refs/tags/", "");

            }
            git.close();

            return ok(views.html.main.render("Web City", version, null));

        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }

        return badRequest();
    }


    /**
     * downloads the linked repository, parses it, does the rectangle packing, and returns the data to be visualized
     */
    // FIXME should there be option to authenticate and use private repos? though then they
    // FIXME would be on the server
    public Result getVisualizationData() {
        JavaPackage pkg;
        List<String> versions = new ArrayList<>();

        if (web) {
            // remove the old downloaded repository
            deleteDir(new File("/Users/paolo/Documents/6th semester/thesis/webcity/repository"));

            // try to download the given repository
            try {
                Git git = Git.cloneRepository()
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider("AurecchiaP", "VHv5yeB2IxOu"))
                        // for debugging, prints data nicely in System.out
                        // .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))

                        // saves download progress information to send to client
                        .setProgressMonitor(new ProgressMonitor() {
                            int downloadedData = 1;
                            int totalData = 1;

                            @Override
                            public void start(int totalTasks) {
                            }

                            @Override
                            public void beginTask(String title, int totalWork) {
                                // new task has started; reset downloaded data for this task to 0
                                downloadedData = 0;

                                // increase task number
                                ++taskNumber;
                                taskName = title;
                                totalData = totalWork;
                            }

                            @Override
                            public void update(int completed) {
                                // update the number of total downloaded data
                                downloadedData += completed;

                                //update downloaded percentage
                                if (totalData != 0) {
                                    percentage = ((double) downloadedData / (double) totalData) * 100;
                                }
                            }

                            @Override
                            public void endTask() {
                            }

                            @Override
                            public boolean isCancelled() {
                                return false;
                            }
                        })
                        .setURI(currentRepo)
                        .setDirectory(new File("/Users/paolo/Documents/6th semester/thesis/webcity/repository"))
                        .call();

                // TODO to print tags
                for (int i = 0; i < git.tagList().call().size(); ++i) {
                    System.out.println(git.tagList().call().get(i).getName());
                }
                versions = git.tagList().call().stream().map(Ref::getName).collect(Collectors.toList());

                // TODO use this to set the version to visualise
//                git.checkout().setCreateBranch( true ).setName( "test" ).setStartPoint( git.tagList().call().get( git.tagList().call().size() -1 ).getName() ).call();
                git.close();
            } catch (GitAPIException e) {
                System.out.println("failed to download repo");
                e.printStackTrace();
                return badRequest();
            }

            // parse the .java files of the repository
            pkg = BasicParser.parseRepo("/Users/paolo/Documents/6th semester/thesis/webcity/repository");
        } else {
            pkg = BasicParser.parseRepo("/Users/paolo/Documents/6th semester/thesis/commons-math");
        }

        System.out.println("Done downloading repo");


        DrawablePackage drw = toDrawable(pkg);

        // do the rectangle packing
        new RectanglePacking(drw);
        System.out.println("Done packing");

        taskNumber = 0;

        // create json object
        Gson gson = new Gson();
        final JsonObject jsonObject = new JsonObject();
        // add visualization data
        jsonObject.add("visualization", gson.fromJson(toJSON(drw), JsonElement.class));
        // add list of versions
        jsonObject.add("versions", gson.fromJson(new Gson().toJson(versions), JsonElement.class));

        // send data to client
        return ok(gson.toJson(jsonObject));
    }


    /**
     * route for the visualisation page; not yet used
     */
    public Result visualization() {
        String repo = formFactory.form().bindFromRequest().get("repository");
        System.out.println("input repository: " + repo);

        // empty input uses local repo, for debugging
        if (repo.equals("")) {
            web = false;
            return getVisualizationData();
        } else {
            web = true;
            currentRepo = repo;

            final LsRemoteCommand lsCmd = new LsRemoteCommand(null)

                    // FIXME to download private repo
//                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider( "AurecchiaP", "" ))
                    .setRemote(repo);
            try {
                // print for debugging
//                System.out.println(lsCmd.call().toString());
                lsCmd.call();
                System.out.println("valid repo");
                currentRepo = repo;
                return ok();

            } catch (GitAPIException e) {
                System.out.println("invalid repo");
                e.printStackTrace();
                return badRequest();
            }
        }
    }


    /**
     * while the data is being sent from server to client, the client will poll the server to know the percentage
     */
    public Result poll() {
        JsonObject obj = new JsonObject();
        obj.addProperty("percentage", percentage);
        obj.addProperty("task", taskNumber);
        obj.addProperty("taskName", taskName);
        return ok(obj.toString());
    }
}
