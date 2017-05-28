package models.history;

import models.JavaPackage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A JavaPackageHistory represents a container for the different versions of the system we are analysing.
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

    /**
     * @return the name of the package that this JavaPackageHistory represents
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the package that this JavaPackageHistory represents
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the list of JavaPackage contained in this JavaPackageHistory
     */
    public List<JavaPackageHistory> getJpChildren() {
        return Collections.unmodifiableList(jpChildren);
    }

    /**
     * @param jpChildren the list of JavaPackage to be contained in this JavaPackageHistory
     */
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
        for (JavaPackageHistory JpChild : jpChildren) {
            boolean found = false;
            for (int j = 0; j < this.jpChildren.size(); ++j) {
                if (JpChild.getName().equals(this.jpChildren.get(j).getName())) {
                    this.jpChildren.set(j, JpChild);
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.jpChildren.add(JpChild);
            }
        }
    }

    /**
     * @return the list of JavaClass contained in this JavaPackageHistory
     */
    public List<JavaClassHistory> getJcChildren() {
        return Collections.unmodifiableList(jcChildren);
    }

    /**
     * @param jcChildren the list of JavaClass to be contained in this JavaPackageHistory
     */
    public void setJcChildren(List<JavaClassHistory> jcChildren) {
        this.jcChildren.clear();
        this.jcChildren.addAll(jcChildren);
    }

    /**
     * @param jcChildren the list of JavaClass to be added to the list of classes contained in this JavaClassHistory
     */
    public void addJcChildren(List<JavaClassHistory> jcChildren) {
        this.jcChildren.addAll(jcChildren);
    }

    /**
     * @param version the version in which we want to know if this JavaPackage was present
     * @return true if this package existed in the given version, false otherwise
     */
    public boolean PackageHistoriesContains(String version) {
        return packageHistories.containsKey(version);
    }

    /**
     * @param version the version to which we want to set packageHistory
     * @param packageHistory the packageHistory to be added to this JavaPackageHistory
     */
    public void addPackageHistory(String version, JavaPackage packageHistory) {
        this.packageHistories.put(version, packageHistory);
    }

    /**
     * @param version the version from which we want to get packageHistory
     * @return the packageHistory corresponding to version
     */
    public JavaPackage getPackageHistory(String version) {
        return this.packageHistories.get(version);
    }
}
