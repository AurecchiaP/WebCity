package models;

import models.drawables.DrawablePackage;
import org.eclipse.jgit.lib.Ref;
import utils.RectanglePacking;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * class used by the controller to keep track of the data of the different visualisations required
 */
public class RepositoryModel {

    private DrawablePackage maxDrw;
    private Map<String, RectanglePacking> packings;
    private List<Commit> commits;
    private List<Ref> tags;

    public RepositoryModel(DrawablePackage maxDrw, Map<String, RectanglePacking> packings, List<Commit> commits, List<Ref> tags) {
        this.maxDrw = maxDrw;
        this.packings = packings;
        this.commits = commits;
        this.tags = tags;
    }

    public DrawablePackage getMaxDrw() {
        return maxDrw;
    }

    public void setMaxDrw(DrawablePackage maxDrw) {
        this.maxDrw = maxDrw;
    }

    public Map<String, RectanglePacking> getPackings() {
        return Collections.unmodifiableMap(packings);
    }

    public void setPackings(Map<String, RectanglePacking> packings) {
        this.packings.clear();
        this.packings.putAll(packings);
    }

    public List<Commit> getCommits() {
        return Collections.unmodifiableList(commits);
    }

    public void setCommits(List<Commit> commits) {
        this.commits.clear();
        this.commits.addAll(commits);
    }

    public List<Ref> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<Ref> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }
}
