package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import models.drawables.DrawablePackage;
import models.history.JavaPackageHistory;
import models.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.revwalk.RevCommit;
import play.api.Play;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
import utils.BasicParser;
import models.JavaPackage;
import utils.HistoryUtils;
import utils.RectanglePacking;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import java.io.File;
import java.util.*;

import static utils.DrawableUtils.compareWithMax;
import static utils.DrawableUtils.getMaxDrawable;
import static utils.DrawableUtils.historyToDrawable;
import static utils.JSON.toJSON;

public class HomeController extends Controller {

    private String currentRepo;
    private double percentage = 0;
    private int taskNumber = 0;
    private String taskName;
    private DrawablePackage maxDrw;
    private List<RectanglePacking> packings;
    private List<Commit> commits;

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
                        routes.javascript.HomeController.poll(),
                        routes.javascript.HomeController.getCommit()
                )
        ).as("text/javascript");
    }

    /**
     * default route for index page
     */
    public Result index() {
        return ok(views.html.main.render("Web City", "version", null));
    }

    /**
     * downloads the linked repository, parses it, does the rectangle packing, and returns the data to be visualized
     */
    public Result getVisualizationData() {
        commits = new ArrayList<>();

        // remove the old downloaded repository
//        deleteDir(new File(Play.current().path() + "/repository"));

        // try to download the given repository
        Git git = null;

        // repoName is author/repository
        String repoName = currentRepo.replace("https://github.com/", "");

        File directory = new File(Play.current().path() + "/repository/" + repoName + "/.git/");

        // if we have cached this repository before
        try {
            if (directory.exists()) {

                git = Git.open(directory);
                git.checkout().setName("master").call();
            } else {
                System.out.println("Downloading depo...");
                git = Git.cloneRepository()
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
                        //                        .
                        .setDirectory(new File(Play.current().path() + "/repository/" + repoName))
                        .setGitDir(new File(Play.current().path() + "/repository/" + repoName + "/.git/"))
                        .call();
                System.out.println("Done downloading repository.");
            }

            Iterable<RevCommit> revCommits = null;
            revCommits = git.log().call();

            for (RevCommit revCommit : revCommits) {
                PersonIdent authorIdent = revCommit.getAuthorIdent();
                Date authorDate = authorIdent.getWhen();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                commits.add(new Commit(revCommit.getName(), authorIdent.getName(), dateFormat.format(authorDate)));
            }
        } catch (IOException e) {
            System.out.println("IO error");
            e.printStackTrace();
            return badRequest();
        } catch (GitAPIException e) {
            System.out.println("GitAPI error");
            e.printStackTrace();
            return badRequest();
        }

        // parse the .java files of the repository
        BasicParser.parseRepo(Play.current().path() + "/repository");

        JavaPackageHistory jph = null;

        HistoryUtils historyUtils = new HistoryUtils();

        System.out.println("reponame " + repoName);
        System.out.println("Start parsing...");
        // iterate all the commits of the repo
        for (Commit commit : commits) {

            // set repository to the next version
            try {
//                git.checkout().setCreateBranch(true).setName("test_" + version).setStartPoint(version).call();
                git.checkout().setName(commit.getName()).call();

                // parse the current version
                JavaPackage temp = BasicParser.parseRepo(Play.current().path() + "/repository/" + repoName);
                jph = historyUtils.toHistory(commit.getName(), temp);

            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Done parsing.");

        // a list of rectangle packings for all the commits
        packings = new ArrayList<>();

        System.out.println("Start packing...");


        for (Commit commit : commits) {

            // make the packages into drawables to be used for rectangle packing
            DrawablePackage drw = historyToDrawable(commit.getName(), jph);

            // do the rectangle packing
            packings.add(new RectanglePacking(drw, null, commit.getName()));
        }

        System.out.println("Done packing.");

        // find the biggest drawable, considering all commits
        maxDrw = getMaxDrawable(packings);

        new RectanglePacking(maxDrw, maxDrw, "max");

        compareWithMax(maxDrw, packings.get(0));

        taskNumber = 0;

        git.close();

        Map <String, String> details = new HashMap<>();

        // fixme reponame != repo url
        details.put("repositoryOwner", repoName);
        details.put("repositoryName", repoName);
        details.put("repositoryUrl", currentRepo);

        // create json object
        Gson gson = new Gson();
        final JsonObject jsonObject = new JsonObject();
        // add visualization data
        jsonObject.add("visualization", gson.fromJson(toJSON(maxDrw), JsonElement.class));
        jsonObject.add("commits", gson.fromJson(new Gson().toJson(commits), JsonElement.class));
        jsonObject.add("maxDrw", gson.fromJson(new Gson().toJson(maxDrw), JsonElement.class));
        jsonObject.add("details", gson.fromJson(new Gson().toJson(details), JsonElement.class));

        // send data to client
        return ok(gson.toJson(jsonObject));
    }


    /**
     * route for the visualisation page
     */
    public Result visualization() {
        String repo = formFactory.form().bindFromRequest().get("repository");
        System.out.println("input repository: " + repo);

        // empty input uses local repo, for debugging
        currentRepo = repo;

        final LsRemoteCommand lsCmd = new LsRemoteCommand(null)
                .setRemote(repo);
        try {
            // print for debugging
//                System.out.println(lsCmd.call().toString());
            lsCmd.call();
            System.out.println("Valid repository.");
            currentRepo = repo;
            return ok();

        } catch (GitAPIException e) {
            System.out.println("Invalid repository.");
            e.printStackTrace();
            return badRequest();
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

    /**
     * returns the visualisation data for a given version of the repository
     */
    public Result getCommit() {
        String commit = formFactory.form().bindFromRequest().get("commit");
        System.out.println("version required: " + commit);

        // look for the packing that commit corresponds to
        for (int i = 0; i < commits.size(); ++i) {
            if (commits.get(i).getName().equals(commit)) {
                compareWithMax(maxDrw, packings.get(i));
                break;
            }
        }

        // create json object
        Gson gson = new Gson();
        final JsonObject jsonObject = new JsonObject();
        // add visualization data
        jsonObject.add("visualization", gson.fromJson(toJSON(maxDrw), JsonElement.class));
        // add list of commits
        jsonObject.add("maxDrw", gson.fromJson(new Gson().toJson(maxDrw), JsonElement.class));

        // send data to client
        return ok(gson.toJson(jsonObject));
    }
}
