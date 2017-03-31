package models.history;

import models.JavaPackage;

import java.util.Collections;
import java.util.List;

/**
 * a JavaPackageHistory represents a container for the different versions of the system we are analysing.
 * packageHistories is a list of different JavaPackage instances, each of which represent the JavaPapckage in a different
 * version of the system.
 */
public class JavaPackageHistory {

    private List<JavaPackageHistory> jpChildren;
    private List<JavaClassHistory> jcChildren;
    private List<JavaPackage> packageHistories;

    public JavaPackageHistory(List<JavaPackageHistory> jpChildren, List<JavaClassHistory> jcChildren, List<JavaPackage> packageHistories) {
        this.jpChildren = jpChildren;
        this.jcChildren = jcChildren;
        this.packageHistories = packageHistories;
    }

    public List<JavaPackageHistory> getJpChildren() {
        return Collections.unmodifiableList(jpChildren);
    }

    public void setJpChildren(List<JavaPackageHistory> jpChildren) {
        this.jpChildren.clear();
        this.jpChildren.addAll(jpChildren);
    }

    public List<JavaClassHistory> getJcChildren() {
        return Collections.unmodifiableList(jcChildren);
    }

    public void setJcChildren(List<JavaClassHistory> jcChildren) {
        this.jcChildren.clear();
        this.jcChildren.addAll(jcChildren);
    }

    public List<JavaPackage> getPackageHistories() {
        return Collections.unmodifiableList(packageHistories);
    }

    public void setPackageHistories(List<JavaPackage> packageHistories) {
        this.packageHistories.clear();
        this.packageHistories.addAll(packageHistories);
    }
}
