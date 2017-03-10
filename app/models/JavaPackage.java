package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model for Java packages
 */
public class JavaPackage {

    private String name;
    private List<JavaClass> classes;
    private int totalClasses = 0;
    private List<JavaPackage> children;

    public int cx;
    public int cy;
    public int w;
    public int z;
    public int color;

    public JavaPackage(String name) {
        this.name = name;
        this.children = new ArrayList<>();
        this.classes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setClasses(List<JavaClass> classes) {
        this.classes.clear();
        this.classes.addAll(classes);
    }

    public List<JavaClass> getClasses() {
        return Collections.unmodifiableList(classes);
    }

    public void setChildren(List<JavaPackage> children) {
        this.children.clear();
        this.children.addAll(children);
    }

    public void sortChildren() {
        this.children.sort((c1, c2) -> {
            if (c1.w == c2.w)
                return 0;
            return c1.w > c2.w ? -1 : 1;
        });
    }

    public void sortClasses() {
        this.classes.sort((c1, c2) -> {
            if (c1.getMethods() == c2.getMethods())
                return 0;
            return c1.getMethods() > c2.getMethods() ? -1 : 1;
        });
    }


    public List<JavaPackage> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(JavaPackage child) {
        this.children.add(child);
    }

    public void addClass(JavaClass cls) {
        this.classes.add(cls);
    }

    public void addClassTotal(int num) {
        this.totalClasses += num;
    }

    public int getClassTotal() {
        return totalClasses;
    }
}