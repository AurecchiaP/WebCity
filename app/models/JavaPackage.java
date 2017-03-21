package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model for Java packages
 */
public class JavaPackage {

    private String name;
    private List<JavaPackage> childPackages;
    private List<JavaClass> classes;
    private int totalClasses = 0;

    public int cx;
    public int cy;
    public int w;
    public int d;
    public int z;
    public int color;

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
     * replaces the list of classes contained in this package with the given classes parameter
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
     * sorts the child packages by their size, in descending order
     */
    public void sortChildren() {
        this.childPackages.sort((c1, c2) -> {
            if (c1.w == c2.w)
                return 0;
            return c1.w > c2.w ? -1 : 1;
        });
    }

    /**
     * sorts the classes by their number of attributes, in descending order
     */
    public void sortClasses() {
        this.classes.sort((c1, c2) -> {
            if (c1.getAttributes().getValue().equals(c2.getAttributes().getValue()))
                return 0;
            return c1.getAttributes().getValue() > c2.getAttributes().getValue() ? -1 : 1;
        });
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
}