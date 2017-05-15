package models;

import models.drawables.DrawablePackage;
import org.eclipse.jgit.lib.Ref;
import utils.RectanglePacking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * class used by the controller to keep track of the data of the different visualisations required
 */
public class RepositoryModel {

    private DrawablePackage maxDrwCommits;
    private DrawablePackage maxDrwTags;
    private Map<String, RectanglePacking> packingsCommits;
    private Map<String, RectanglePacking> packingsTags;
    private List<Commit> commits;
    private List<Commit> commitTags;
    private List<Ref> tags;

    public RepositoryModel(DrawablePackage maxDrwCommits,
                           DrawablePackage maxDrwTags,
                           Map<String, RectanglePacking> packingsCommits,
                           Map<String, RectanglePacking> packingsTags,
                           List<Commit> commits,
                           List<Commit> commitTags,
                           List<Ref> tags) {
        this.maxDrwCommits = maxDrwCommits;
        this.maxDrwTags = maxDrwTags;
        this.packingsCommits = packingsCommits;
        this.packingsTags = packingsTags;
        this.commits = commits;
        this.commitTags = commitTags;
        this.tags = tags;
    }

    public DrawablePackage getMaxDrw(String type) {
        if (type.equals("Commits")) {
            return this.maxDrwCommits;
        }else {
            return this.maxDrwTags;
        }
    }

    public void setMaxDrw(String type, DrawablePackage maxDrw) {
        if (type.equals("Commits")) {
            this.maxDrwCommits = maxDrw;
        } else {
            this.maxDrwTags = maxDrw;
        }
    }

    public Map<String, RectanglePacking> getPackings(String type) {
        if (type.equals("Commits")) return Collections.unmodifiableMap(packingsCommits);
        return Collections.unmodifiableMap(packingsTags);
    }

    public void setPackings(String type, Map<String, RectanglePacking> packings) {
        if (type.equals("Commits")) {
            this.packingsCommits.clear();
            this.packingsCommits.putAll(packings);
        } else {
            this.packingsTags.clear();
            this.packingsTags.putAll(packings);
        }
    }

    public List<Commit> getCommits() {
        return Collections.unmodifiableList(commits);
    }

    public void setCommits(List<Commit> commits) {
        this.commits.clear();
        this.commits.addAll(commits);
    }

    public List<Commit> getCommitTags() {
        return Collections.unmodifiableList(commitTags);
    }

    public void setCommitTags(List<Commit> commitTags) {
        this.commitTags.clear();
        this.commitTags.addAll(commitTags);
    }

    public List<Ref> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<Ref> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }
}
