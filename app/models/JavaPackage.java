package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model for Java packages
 */
public class JavaPackage implements Serializable {

    private String name;
    private List<JavaPackage> childPackages;
    private List<JavaClass> classes;
    private int totalClasses = 0;

    public JavaPackage(String name) {
        this.name = name;
        this.childPackages = new ArrayList<>();
        this.classes = new ArrayList<>();
    }

    /**
     * @return the name of the package
     */
    public String getName() {
        return name;
    }

    /**
     * Replaces the list of classes contained in this package with the given classes parameter
     *
     * @param classes the new list of classes to be set
     */
    public void setClasses(List<JavaClass> classes) {
        this.classes.clear();
        this.classes.addAll(classes);
    }

    /**
     * @return a list of classes contained in this package (only depth 1, it does not count the classes contained
     * in child packages)
     */
    public List<JavaClass> getClasses() {
        return Collections.unmodifiableList(classes);
    }

    public void setChildPackages(List<JavaPackage> childPackages) {
        this.childPackages.clear();
        this.childPackages.addAll(childPackages);
    }

    /**
     * @return the list of packages contained in this package
     */
    public List<JavaPackage> getChildPackages() {
        return Collections.unmodifiableList(childPackages);
    }

    /**
     * @param child the JavaPackage to be added to the list of child packages of this package
     */
    public void addChildPackage(JavaPackage child) {
        this.childPackages.add(child);
    }

    /**
     * @param cls adds a single class to the list of classes of this package
     */
    public void addClass(JavaClass cls) {
        this.classes.add(cls);
    }


    /**
     * @param num adds num to the number of total classes contained in this package, at any depth
     */
    public void addClassTotal(int num) {
        this.totalClasses += num;
    }

    /**
     * @return the number of total classes contained in this package, at any depth
     */
    public int getClassTotal() {
        return totalClasses;
    }

    /**
     * @param totalClasses the number of total classes contained in this package, at any depth
     */
    public void setClassTotal(int totalClasses) {
        this.totalClasses = totalClasses;
    }
}