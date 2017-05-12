package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import models.RepositoryModel;
import models.drawables.DrawablePackage;
import models.history.JavaPackageHistory;
import models.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
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

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import java.util.*;

import static utils.DrawableUtils.compareWithMax;
import static utils.DrawableUtils.getMaxDrawable;
import static utils.DrawableUtils.historyToDrawable;
import static utils.JSON.toJSON;

public class HomeController extends Controller {

    private double percentage = 0;
    private String taskName;
    private double parsingPercentage;
    private Map<String, RepositoryModel> rms = new HashMap<>();

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
                        routes.javascript.HomeController.getCommit(),
                        routes.javascript.HomeController.reloadVisualization()
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
        String currentRepo = formFactory.form().bindFromRequest().get("repository");
        String type = formFactory.form().bindFromRequest().get("type");
        DrawablePackage maxDrw;
        Map<String, RectanglePacking> packings;
        List<Commit> commits;
        List<Ref> tags = new ArrayList<>();
        List<Ref> peeledTags = new ArrayList<>();
        Map<String, String> details = new HashMap<>();

        String repoName = currentRepo.replace("https://github.com/", "");


        // try to download the given repository
        File directory = new File(Play.current().path() + "/repository/" + repoName + "/.git/");
        Git git = null;

        try {
            git = Git.open(directory);
            git.checkout().setName("master").call();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }


        RepositoryModel rm = rms.get(currentRepo);
        commits = rm.getCommits();
        tags = rm.getTags();

        for (Ref ref : tags) {
            Ref peeledRef = git.getRepository().peel(ref);
            if (peeledRef.getPeeledObjectId() != null) {
                System.out.println(peeledRef.getObjectId().getName());
                peeledTags.add(peeledRef);
            } else {
                System.out.println(ref.getObjectId().getName());
                peeledTags.add(ref);
            }
        }


        JavaPackageHistory jph = null;

        HistoryUtils historyUtils = new HistoryUtils();

        System.out.println("Repository name: " + repoName);

        File serialized = new File(Play.current().path() + "/repository/" + repoName + "/history_" + type + ".ser");
        if (serialized.exists() && !serialized.isDirectory()) {
            System.out.println("Found serialized object.");

            FileInputStream fin = null;
            ObjectInputStream ois = null;

            try {
                fin = new FileInputStream(Play.current().path() + "/repository/" + repoName + "/history_" + type + ".ser");
                ois = new ObjectInputStream(fin);
                jph = (JavaPackageHistory) ois.readObject();

                System.out.println("Done reading serialized object.");

            } catch (Exception ex) {
                System.out.println("Couldn't read serialized object.");
                ex.printStackTrace();
            } finally {

                if (fin != null) {
                    try {
                        fin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Start parsing...");

            try {
                tags = git.tagList().call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }

            if (type.equals("Commits")) {
                // iterate all the commits of the repo
                for (int i = 0; i < commits.size(); i++) {
                    Commit commit = commits.get(i);

                    // set repository to the next version
                    try {
                        git.checkout().setName(commit.getName()).call();

                        // parse the current version
                        JavaPackage temp = BasicParser.parseRepo(Play.current().path() + "/repository/" + repoName);
                        jph = historyUtils.toHistory(commit.getName(), temp);

                    } catch (GitAPIException e) {
                        e.printStackTrace();
                    }

                    parsingPercentage = (double) i / commits.size() * 100;
                }
            } else {
                List<Ref> orderedTags = new ArrayList<>();
                    for (int i = 0; i < commits.size(); i++) {
                        Commit commit = commits.get(i);
                        for (Ref tag : peeledTags) {
                            String tagId = tag.getObjectId().getName();

                        if (tagId.equals(commit.getName())) {
                            try {
                                orderedTags.add(tag);
                                git.checkout().setName(commit.getName()).call();

                                // parse the current version
                                JavaPackage temp = BasicParser.parseRepo(Play.current().path() + "/repository/" + repoName);
                                jph = historyUtils.toHistory(commit.getName(), temp);

                            } catch (GitAPIException e) {
                                e.printStackTrace();
                            }

                            parsingPercentage = (double) i / tags.size() * 100;
                            break;
                        }
                    }
                }
                peeledTags = orderedTags;
            }

            System.out.println("Done parsing.");

            System.out.println("Start serializing object.");


            FileOutputStream fout = null;
            ObjectOutputStream oos = null;
            try {

                fout = new FileOutputStream(Play.current().path() + "/repository/" + repoName + "/history_" + type + ".ser");
                oos = new ObjectOutputStream(fout);
                oos.writeObject(jph);

                System.out.println("Done serializing object.");

            } catch (Exception ex) {
                System.out.println("Couldn't serialize object.");
                ex.printStackTrace();
            } finally {

                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (fout != null) {
                    try {
                        fout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // a list of rectangle packings for all the commits
        packings = new HashMap<>();

        System.out.println("Start packing...");

        List<Commit> commitTags = new ArrayList<>();

        if (type.equals("Commits")) {

            for (Commit commit : commits) {

                // make the packages into drawables to be used for rectangle packing
                DrawablePackage drw = historyToDrawable(commit.getName(), jph);

                // do the rectangle packing
                packings.put(commit.getName(), new RectanglePacking(drw, null, 20,20, commit.getName()));
            }
        } else {
            for (Ref tag : peeledTags) {
                String tagId = tag.getObjectId().getName();
                for (Commit commit : commits) {

                    if (tagId.equals(commit.getName())) {
                        commitTags.add(commit);
                        // make the packages into drawables to be used for rectangle packing
                        DrawablePackage drw = historyToDrawable(commit.getName(), jph);

                        // do the rectangle packing
                        packings.put(commit.getName(), new RectanglePacking(drw, null, 20, 20, commit.getName()));
                        break;
                    }
                }
            }
        }

        System.out.println("Done packing.");


        // find the biggest drawable, considering all commits
        maxDrw = getMaxDrawable(new ArrayList<>(packings.values()));

        new RectanglePacking(maxDrw, maxDrw, 20, 20, "max");

        if(type.equals("Commits")) {
            compareWithMax(maxDrw, packings.get(commits.get(0).getName()));
        }
        else {
            compareWithMax(maxDrw, packings.get(commitTags.get(0).getName()));
        }


        git.close();

        details.put("repository", repoName);
        details.put("repositoryUrl", currentRepo);

        // create json object
        Gson gson = new Gson();
        final JsonObject jsonObject = new JsonObject();
        // add visualization data
        jsonObject.add("visualization", gson.fromJson(toJSON(maxDrw), JsonElement.class));
        if (type.equals("Commits")) {
            jsonObject.add("commits", gson.fromJson(new Gson().toJson(commits), JsonElement.class));
        } else {
            jsonObject.add("commits", gson.fromJson(new Gson().toJson(commitTags), JsonElement.class));
        }
        jsonObject.add("details", gson.fromJson(new Gson().toJson(details), JsonElement.class));

        rm.setMaxDrw(maxDrw);
        rm.setPackings(packings);

        rms.put(currentRepo, rm);

        // send data to client
        return ok(gson.toJson(jsonObject));
    }


    /**
     * route for the visualisation page
     */

    public Result visualization() {
        String currentRepo = formFactory.form().bindFromRequest().get("repository");
        System.out.println("input repository: " + currentRepo);

        final LsRemoteCommand lsCmd = new LsRemoteCommand(null)
                .setRemote(currentRepo);
        try {
            lsCmd.call();
            System.out.println("Valid repository.");

            downloadRepo(currentRepo);

            RepositoryModel rm = rms.get(currentRepo);

            // create json object
            Gson gson = new Gson();
            final JsonObject jsonObject = new JsonObject();
            // add visualization data
            jsonObject.add("commits", gson.fromJson(new Gson().toJson(rm.getCommits()), JsonElement.class));
            jsonObject.add("tags", gson.fromJson(new Gson().toJson(rm.getTags()), JsonElement.class));

            // send data to client
            return ok(gson.toJson(jsonObject));

        } catch (GitAPIException e) {
            System.out.println("Invalid repository.");
            e.printStackTrace();
            return badRequest();
        }
    }

    private int downloadRepo(String currentRepo) {
        DrawablePackage maxDrw;
        List<RectanglePacking> packings;
        List<Commit> commits = new ArrayList<>();
        Map<String, String> details = new HashMap<>();


        // try to download the given repository
        Git git = null;

        // repoName is author/repository
        String repoName = currentRepo.replace("https://github.com/", "");

        File directory = new File(Play.current().path() + "/repository/" + repoName + "/.git/");

//        if (rms.containsKey(currentRepo)) {
//            return 0;
//        }

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
                                percentage = 100.0;
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                commits.add(new Commit(
                        revCommit.getName(),
                        authorIdent.getName(),
                        dateFormat.format(authorDate),
                        revCommit.getShortMessage()));
            }

            List<Ref> tags = git.tagList().call();

            RepositoryModel rm = new RepositoryModel(null, new HashMap<>(), commits, tags);
            rms.put(currentRepo, rm);

            return 0;

        } catch (IOException e) {
            System.out.println("IO error");
            e.printStackTrace();
            return 1;
        } catch (GitAPIException e) {
            System.out.println("GitAPI error");
            e.printStackTrace();
            return 1;
        }
    }


    /**
     * while the data is being sent from server to client, the client will poll the server to know the percentage
     */
    public Result poll() {
        String currentRepo = formFactory.form().bindFromRequest().get("repository");
        JsonObject obj = new JsonObject();
        obj.addProperty("percentage", percentage);
        obj.addProperty("taskName", taskName);
        obj.addProperty("parsingPercentage", parsingPercentage);
        return ok(obj.toString());
    }

    /**
     * returns the visualisation data for a given version of the repository
     */
    public Result getCommit() {
        String currentRepo = formFactory.form().bindFromRequest().get("repository");
        RepositoryModel rm = rms.get(currentRepo);
        DrawablePackage maxDrw = rm.getMaxDrw();
        Map<String, RectanglePacking> packings = rm.getPackings();
        List<Commit> commits = rm.getCommits();

        String commit = formFactory.form().bindFromRequest().get("commit");
        System.out.println("version required: " + commit);

        // look for the packing that commit corresponds to

        compareWithMax(maxDrw, packings.get(commit));

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

    public Result reloadVisualization() {
        String currentRepo = formFactory.form().bindFromRequest().get("repository");
        String commit = formFactory.form().bindFromRequest().get("commit");
        int padding = Integer.parseInt(formFactory.form().bindFromRequest().get("padding"));
        int minClassSize = Integer.parseInt(formFactory.form().bindFromRequest().get("minClassSize"));

        RepositoryModel rm = rms.get(currentRepo);
        Map<String, RectanglePacking> packings = rm.getPackings();

        DrawablePackage maxDrw = getMaxDrawable(new ArrayList<>(packings.values()));

        new RectanglePacking(maxDrw, maxDrw, padding, minClassSize, "max");

        compareWithMax(maxDrw, packings.get(commit));

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
