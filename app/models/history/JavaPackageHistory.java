package models.history;

import models.JavaPackage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * a JavaPackageHistory represents a container for the different versions of the system we are analysing.
 * packageHistories is a list of different JavaPackage instances, each of which represent the JavaPackage in a different
 * version of the system.
 */
public class JavaPackageHistory implements Serializable {
    private String name;

    private List<JavaPackageHistory> jpChildren;
    private List<JavaClassHistory> jcChildren;
    private Map<String, JavaPackage> packageHistories;

    public JavaPackageHistory(String name, List<JavaPackageHistory> jpChildren, List<JavaClassHistory> jcChildren, Map<String, JavaPackage> packageHistories) {
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

    /**
     * if the element of jpChildren is already in this.jpChildren, then update it; else add it
     *
     * @param jpChildren elements to be added to this.jpChildren
     */
    public void addOrUpdateJpChildren(List<JavaPackageHistory> jpChildren) {
        for (JavaPackageHistory aJpChildren : jpChildren) {
            boolean found = false;
            for (int j = 0; j < this.jpChildren.size(); ++j) {
                if (aJpChildren.getName().equals(this.jpChildren.get(j).getName())) {
                    this.jpChildren.set(j, aJpChildren);
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.jpChildren.add(aJpChildren);
            }
        }
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

    public boolean PackageHistoriesContains(String version) {
        return packageHistories.containsKey(version);
    }

    public void addPackageHistory(String version, JavaPackage packageHistory) {
        this.packageHistories.put(version, packageHistory);
    }

    public JavaPackage getPackageHistory(String version) {
        return this.packageHistories.get(version);
    }
}
