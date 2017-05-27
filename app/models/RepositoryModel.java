package models;

import models.drawables.DrawablePackage;
import org.eclipse.jgit.lib.Ref;
import utils.RectanglePacking;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Class used by the controller to keep track of the data of the different visualisations required
 */
public class RepositoryModel {

    private String mainBranch;
    private DrawablePackage maxDrwCommits;
    private DrawablePackage maxDrwTags;
    private Map<String, RectanglePacking> packingsCommits;
    private Map<String, RectanglePacking> packingsTags;
    private List<Commit> commits;
    private List<Commit> commitTags;
    private List<Ref> tags;

    public RepositoryModel( String mainBranch,
                            DrawablePackage maxDrwCommits,
                            DrawablePackage maxDrwTags,
                            Map<String, RectanglePacking> packingsCommits,
                            Map<String, RectanglePacking> packingsTags,
                            List<Commit> commits,
                            List<Commit> commitTags,
                            List<Ref> tags) {
        this.mainBranch = mainBranch;
        this.maxDrwCommits = maxDrwCommits;
        this.maxDrwTags = maxDrwTags;
        this.packingsCommits = packingsCommits;
        this.packingsTags = packingsTags;
        this.commits = commits;
        this.commitTags = commitTags;
        this.tags = tags;
    }

    /**
     * @return the main branch that the repository is set to
     */
    public String getMainBranch() {
        return mainBranch;
    }

    /**
     * @param mainBranch the branch that we want the repository to be set to
     */
    public void setMainBranch(String mainBranch) {
        this.mainBranch = mainBranch;
    }

    /**
     * @param type either "Commits" or "Tags", to choose which maxDrw we want to get
     * @return the required maxDrw of the given type
     */
    public DrawablePackage getMaxDrw(String type) {
        if (type.equals("Commits")) {
            return this.maxDrwCommits;
        } else {
            return this.maxDrwTags;
        }
    }

    /**
     * @param type   either "Commits" or "Tags", to choose which maxDrw we want to set
     * @param maxDrw the data of the maxDrw we are setting
     */
    public void setMaxDrw(String type, DrawablePackage maxDrw) {
        if (type.equals("Commits")) {
            this.maxDrwCommits = maxDrw;
        } else {
            this.maxDrwTags = maxDrw;
        }
    }

    /**
     * @param type either "Commits" or "Tags", to choose which packings we want to get
     * @return the map of packings of the given type
     */
    public Map<String, RectanglePacking> getPackings(String type) {
        if (type.equals("Commits")) return Collections.unmodifiableMap(packingsCommits);
        return Collections.unmodifiableMap(packingsTags);
    }

    /**
     * @param type     either "Commits" or "Tags", to choose which packings we want to set
     * @param packings the data of the packings we want to set
     */
    public void setPackings(String type, Map<String, RectanglePacking> packings) {
        if (type.equals("Commits")) {
            this.packingsCommits.clear();
            this.packingsCommits.putAll(packings);
        } else {
            this.packingsTags.clear();
            this.packingsTags.putAll(packings);
        }
    }

    /**
     * @return the list of commits
     */
    public List<Commit> getCommits() {
        return Collections.unmodifiableList(commits);
    }

    /**
     * @param commits the new list of commits
     */
    public void setCommits(List<Commit> commits) {
        this.commits.clear();
        this.commits.addAll(commits);
    }

    /**
     * @return the list of commits that are linked to a tag
     */
    public List<Commit> getCommitTags() {
        return Collections.unmodifiableList(commitTags);
    }

    /**
     * @param commitTags the new list of commits that are linked to a tag
     */
    public void setCommitTags(List<Commit> commitTags) {
        this.commitTags.clear();
        this.commitTags.addAll(commitTags);
    }

    /**
     * @return the list of Ref that represent the tags
     */
    public List<Ref> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * @param tags the list of Ref that represent the tags
     */
    public void setTags(List<Ref> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }
}
