package models.history;

import models.JavaPackage;

import java.util.Collections;
import java.util.List;

/**
 * a JavaPackageHistory represents a container for the different versions of the system we are analysing.
 * packageHistories is a list of different JavaPackage instances, each of which represent the JavaPackage in a different
 * version of the system.
 */
public class JavaPackageHistory {
    private String name;

    private List<JavaPackageHistory> jpChildren;
    private List<JavaClassHistory> jcChildren;
    private List<JavaPackage> packageHistories;

    public JavaPackageHistory(String name, List<JavaPackageHistory> jpChildren, List<JavaClassHistory> jcChildren, List<JavaPackage> packageHistories) {
        this.name = name;
        this.jpChildren = jpChildren;
        this.jcChildren = jcChildren;
        this.packageHistories = packageHistories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<JavaPackageHistory> getJpChildren() {
        return Collections.unmodifiableList(jpChildren);
    }

    public void setJpChildren(List<JavaPackageHistory> jpChildren) {
        this.jpChildren.clear();
        this.jpChildren.addAll(jpChildren);
    }

    public void addJpChildren(List<JavaPackageHistory> jpChildren) {
        this.jpChildren.addAll(jpChildren);
    }

    public List<JavaClassHistory> getJcChildren() {
        return Collections.unmodifiableList(jcChildren);
    }

    public void setJcChildren(List<JavaClassHistory> jcChildren) {
        this.jcChildren.clear();
        this.jcChildren.addAll(jcChildren);
    }

    public void addJcChildren(List<JavaClassHistory> jcChildren) {
        this.jcChildren.addAll(jcChildren);
    }

    public List<JavaPackage> getPackageHistories() {
        return Collections.unmodifiableList(packageHistories);
    }

    public void setPackageHistories(List<JavaPackage> packageHistories) {
        this.packageHistories.clear();
        this.packageHistories.addAll(packageHistories);
    }

    public void addPackageHistory(JavaPackage packageHistory) {
        this.packageHistories.add(packageHistory);
    }
}
