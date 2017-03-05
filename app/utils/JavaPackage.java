package utils;

import java.util.ArrayList;

public class JavaPackage {

    private String name;
    private ArrayList<JavaClass> classes;
    private ArrayList<JavaPackage> children;

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

    public void setClasses(ArrayList<JavaClass> classes) {
        this.classes = classes;
    }

    public ArrayList<JavaClass> getClasses() {
        return classes;
    }

    public void setChildren(ArrayList<JavaPackage> children) {
        this.children = children;
    }

    public ArrayList<JavaPackage> getChildren() {
        return children;
    }

    public void addChild(JavaPackage child) {
        this.children.add(child);
    }


    public void addClass(JavaClass cls) {
        this.classes.add(cls);
    }
}