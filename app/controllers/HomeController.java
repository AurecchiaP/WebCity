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
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import play.api.Play;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
import utils.BasicParser;
import models.JavaPackage;
import utils.DownloadProgress;
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

    private Map<String, RepositoryModel> rms = new HashMap<>();
    private Map<String, DownloadProgress> downloadProgresses = new HashMap<>();


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
        String currentId = formFactory.form().bindFromRequest().get("id");
        String type = formFactory.form().bindFromRequest().get("type");

        DrawablePackage maxDrw;
        Map<String, RectanglePacking> packings;
        List<Commit> commits;
        List<Ref> tags;
        List<Ref> peeledTags = new ArrayList<>();
        Map<String, String> details = new HashMap<>();

        String repoName = currentRepo.replace("https://github.com/", "");

        DownloadProgress downloadProgress = downloadProgresses.get(currentId);

        // try to download the given repository
        File directory = new File(Play.current().path() + "/repository/" + repoName + "/.git/");
        Git git = null;

        try {
            git = Git.open(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (git == null) {
            System.out.println("Couldn't open git folder");
            return badRequest();
        }

        // get the commits and tags of the repository
        RepositoryModel rm = rms.get(currentRepo);
        commits = rm.getCommits();
        tags = rm.getTags();

        Repository repository = git.getRepository();

        // if we have cached the repository earlier, load it
        if (rm.getMaxDrw(type) != null) {
            System.out.println("Loading cached repository");
            details.put("repository", repoName);
            details.put("repositoryUrl", currentRepo);

            // create json object
            Gson gson = new Gson();
            final JsonObject jsonObject = new JsonObject();
            // add visualization data
            jsonObject.add("visualization", gson.fromJson(toJSON(rm.getMaxDrw(type)), JsonElement.class));
            if (type.equals("Commits")) {
                jsonObject.add("commits", gson.fromJson(new Gson().toJson(commits), JsonElement.class));
                jsonObject.add("visibles", gson.fromJson(new Gson().toJson(rm.getPackings(type).get(commits.get(0).getName()).getDrws()), JsonElement.class));
            } else {
                List<Commit> commitTags = rm.getCommitTags();
                jsonObject.add("commits", gson.fromJson(new Gson().toJson(commitTags), JsonElement.class));
                jsonObject.add("visibles", gson.fromJson(new Gson().toJson(rm.getPackings(type).get(commitTags.get(0).getName()).getDrws()), JsonElement.class));
            }
            jsonObject.add("details", gson.fromJson(new Gson().toJson(details), JsonElement.class));

            // send data to client
            return ok(gson.toJson(jsonObject));
        }

        // peel the tags
        for (Ref ref : tags) {
            Ref peeledRef = repository.peel(ref);
            if (peeledRef.getPeeledObjectId() != null) {
                peeledTags.add(peeledRef);
            } else {
                peeledTags.add(ref);
            }
        }


        // sort the tags
        final RevWalk walk = new RevWalk(repository);

        peeledTags.sort((o1, o2) -> {
            try {
                Date d1 = walk.parseCommit(o1.getObjectId()).getCommitterIdent().getWhen();
                Date d2 = walk.parseCommit(o2.getObjectId()).getCommitterIdent().getWhen();
                return d1.compareTo(d2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        });

        JavaPackageHistory jph = null;

        HistoryUtils historyUtils = new HistoryUtils();

        System.out.println("Repository name: " + repoName);

        File serialized = new File(Play.current().path() + "/repository/" + repoName + "/history_" + type + ".ser");

        // if we have a serialized object, load it
        if (serialized.exists() && !serialized.isDirectory()) {
            downloadProgress.setTaskName("Loading visualization");
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
        }
        // it's the first time we visualize this repository; we have to parse it
        else {
            System.out.println("Start parsing...");

            try {
                tags = git.tagList().call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }

            // if the user wants a visualization by commits
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
                    // update the progress
                    downloadProgress.setParsingPercentage((double) i / commits.size() * 100);

                }
            }
            // if the user wants a visualization by tags
            else {

                // iterate all the tags of the repo
                for (int i = 0; i < peeledTags.size(); i++) {
                    Ref tag = peeledTags.get(i);
                    String tagId = null;
                    try {
                        tagId = walk.parseCommit(tag.getObjectId()).getName();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // find the matching commit of the current tag
                    for (Commit commit : commits) {
                        if (tagId != null && tagId.equals(commit.getName())) {
                            try {
                                git.checkout().setName(commit.getName()).call();

                                // parse the current version
                                JavaPackage temp = BasicParser.parseRepo(Play.current().path() + "/repository/" + repoName);
                                jph = historyUtils.toHistory(commit.getName(), temp);

                            } catch (GitAPIException e) {
                                e.printStackTrace();
                            }
                            downloadProgress.setParsingPercentage((double) i / tags.size() * 100);
                            break;
                        }
                    }
                }
            }

            System.out.println("Done parsing.");

            System.out.println("Start serializing object.");


            // serialize the object so that we don't have to parse it again
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
        System.out.println(new java.util.Date());

        List<Commit> commitTags = new ArrayList<>();

        // do the rectangle packing, either by commits or by tags
        if (type.equals("Commits")) {

            for (Commit commit : commits) {

                // make the packages into drawables to be used for rectangle packing
                DrawablePackage drw = historyToDrawable(commit.getName(), jph);

                // do the rectangle packing
                packings.put(commit.getName(), new RectanglePacking(drw, null, 2, 1, commit.getName()));
            }
        } else {
            for (Ref tag : peeledTags) {
                String tagId = null;
                try {
                    tagId = walk.parseCommit(tag.getObjectId()).getName();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (Commit commit : commits) {
                    if (tagId != null && tagId.equals(commit.getName())) {
                        commitTags.add(0, commit);
                        // make the packages into drawables to be used for rectangle packing
                        DrawablePackage drw = historyToDrawable(commit.getName(), jph);

                        // do the rectangle packing
                        packings.put(commit.getName(), new RectanglePacking(drw, null, 2, 1, commit.getName()));
                        break;
                    }
                }
            }
        }

        System.out.println("Done packing.");


        // find the biggest drawable, considering all commits
        maxDrw = getMaxDrawable(new ArrayList<>(packings.values()));

        new RectanglePacking(maxDrw, maxDrw, 2, 1, "max");

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
            jsonObject.add("visibles", gson.fromJson(new Gson().toJson(packings.get(commits.get(0).getName()).getDrws()), JsonElement.class));
        } else {
            jsonObject.add("commits", gson.fromJson(new Gson().toJson(commitTags), JsonElement.class));
            jsonObject.add("visibles", gson.fromJson(new Gson().toJson(packings.get(commitTags.get(0).getName()).getDrws()), JsonElement.class));
        }
        jsonObject.add("details", gson.fromJson(new Gson().toJson(details), JsonElement.class));

        rm.setMaxDrw(type, maxDrw);
        rm.setPackings(type, packings);
        rm.setCommitTags(commitTags);

        rms.put(currentRepo, rm);

        // send data to client
        return ok(gson.toJson(jsonObject));
    }


    /**
     * route for the visualisation page
     */
    public Result visualization() {
        String currentRepo = formFactory.form().bindFromRequest().get("repository");
        String currentId = formFactory.form().bindFromRequest().get("id");
        System.out.println("input repository: " + currentRepo);

        final LsRemoteCommand lsCmd = new LsRemoteCommand(null)
                .setRemote(currentRepo);
        try {
            lsCmd.call();
            System.out.println("Valid repository.");

            downloadRepo(currentRepo, currentId);

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

    private void downloadRepo(String currentRepo, String currentId) {
//        DrawablePackage maxDrw;
//        List<RectanglePacking> packings;
        List<Commit> commits = new ArrayList<>();
//        Map<String, String> details = new HashMap<>();


        // try to download the given repository
        Git git;

        // repoName is author/repository
        String repoName = currentRepo.replace("https://github.com/", "");

        File directory = new File(Play.current().path() + "/repository/" + repoName + "/.git/");

        DownloadProgress downloadProgress = new DownloadProgress();
        downloadProgresses.put(currentId, downloadProgress);

        // if we have cached this repository before
        try {
            if (directory.exists()) {
                git = Git.open(directory);
//                git.checkout().setName("refs/remotes/origin/master").call();
                git.pull();
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

                                downloadProgress.setTaskName(title);
                                totalData = totalWork;
                            }

                            @Override
                            public void update(int completed) {
                                // update the number of total downloaded data
                                downloadedData += completed;

                                //update downloaded percentage
                                if (totalData != 0) {
                                    downloadProgress.setPercentage(((double) downloadedData / (double) totalData) * 100);
                                }
                            }

                            @Override
                            public void endTask() {
                                downloadProgress.setPercentage(100.0);
                            }

                            @Override
                            public boolean isCancelled() {
                                return false;
                            }
                        })
                        .setURI(currentRepo)
                        .setDirectory(new File(Play.current().path() + "/repository/" + repoName))
                        .setGitDir(new File(Play.current().path() + "/repository/" + repoName + "/.git/"))
                        .call();
                System.out.println("Done downloading repository.");
            }

            Iterable<RevCommit> revCommits;
            revCommits = git.log().all().call();


            // iterate the RevCommit and create out own Commit, with the information that we want
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

            // if it's the first time we download this repository create a new RepositoryModel, else update it
            if (!rms.containsKey(currentRepo)) {
                RepositoryModel rm = new RepositoryModel(
                        "refs/remotes/origin/" + git.getRepository().getBranch(),
                        null,
                        null,
                        new HashMap<>(),
                        new HashMap<>(),
                        commits,
                        new ArrayList<>(),
                        tags);
                rms.put(currentRepo, rm);
            } else {
                RepositoryModel rm = rms.get(currentRepo);
                rm.setCommits(commits);
                rm.setTags(tags);
            }

        } catch (IOException e) {
            System.out.println("IO error");
            e.printStackTrace();
        } catch (GitAPIException e) {
            System.out.println("GitAPI error");
            e.printStackTrace();
        }
    }

    /**
     * while the data is being sent from server to client, the client will poll the server to know the percentage
     */
    public Result poll() {
        String currentId = formFactory.form().bindFromRequest().get("id");
        JsonObject obj = new JsonObject();
        DownloadProgress downloadProgress = downloadProgresses.get(currentId);
        if (downloadProgress != null) {
            obj.addProperty("percentage", downloadProgress.getPercentage());
            obj.addProperty("taskName", downloadProgress.getTaskName());
            obj.addProperty("parsingPercentage", downloadProgress.getParsingPercentage());
        }
        return ok(obj.toString());
    }

    /**
     * returns the visualisation data for a given version of the repository
     */
    public Result getCommit() {
        String currentRepo = formFactory.form().bindFromRequest().get("repository");
        String type = formFactory.form().bindFromRequest().get("type");
        RepositoryModel rm = rms.get(currentRepo);
        Map<String, RectanglePacking> packings = rm.getPackings(type);

        String commit = formFactory.form().bindFromRequest().get("commit");

        // create json object
        Gson gson = new Gson();
        final JsonObject jsonObject = new JsonObject();
        // add list of objects that are visible in this version
        jsonObject.add("visibles", gson.fromJson(new Gson().toJson(packings.get(commit).getDrws()), JsonElement.class));

        // send data to client
        return ok(gson.toJson(jsonObject));
    }

    /**
     * reloads a visualization with the new given sizes (padding, miClassSize, package height)
     */
    public Result reloadVisualization() {
        String currentRepo = formFactory.form().bindFromRequest().get("repository");
        String type = formFactory.form().bindFromRequest().get("type");
        String commit = formFactory.form().bindFromRequest().get("commit");
        int padding = Integer.parseInt(formFactory.form().bindFromRequest().get("padding"));
        int minClassSize = Integer.parseInt(formFactory.form().bindFromRequest().get("minClassSize"));

        RepositoryModel rm = rms.get(currentRepo);
        Map<String, RectanglePacking> packings = rm.getPackings(type);

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
