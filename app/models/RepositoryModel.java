package models;

import models.drawables.DrawablePackage;
import utils.RectanglePacking;

import java.util.Collections;
import java.util.List;


/**
 * class used by the controller to keep track of the data of the different visualisations required
 */
public class RepositoryModel {

    private DrawablePackage maxDrw;
    private List<RectanglePacking> packings;
    private List<Commit> commits;

    public RepositoryModel(DrawablePackage maxDrw, List<RectanglePacking> packings, List<Commit> commits) {
        this.maxDrw = maxDrw;
        this.packings = packings;
        this.commits = commits;
    }

    public DrawablePackage getMaxDrw() {
        return maxDrw;
    }

    public void setMaxDrw(DrawablePackage maxDrw) {
        this.maxDrw = maxDrw;
    }

    public List<RectanglePacking> getPackings() {
        return Collections.unmodifiableList(packings);
    }

    public void setPackings(List<RectanglePacking> packings) {
        this.packings.clear();
        this.packings.addAll(packings);
    }

    public List<Commit> getCommits() {
        return Collections.unmodifiableList(commits);
    }

    public void setCommits(List<Commit> commits) {
        this.commits.clear();
        this.commits.addAll(commits);
    }
}
