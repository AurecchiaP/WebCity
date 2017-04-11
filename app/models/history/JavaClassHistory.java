package models.history;

import models.JavaClass;

import java.util.Collections;
import java.util.List;

/**
 * a JavaClassHistory represents a container for the different versions of the class it represents.
 * classHistories is a list of different javaClass instances, each of which represent the JavaClass in a different
 * version of the system.
 */
public class JavaClassHistory {

    private String name;
    private List<JavaClass> classHistories;

    public JavaClassHistory(String name, List<JavaClass> classHistories) {
        this.name = name;
        this.classHistories = classHistories;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JavaClass> getClassHistories() {
        return Collections.unmodifiableList(classHistories);
    }

    public void setClassHistories(List<JavaClass> classHistories) {
        this.classHistories.clear();
        this.classHistories.addAll(classHistories);
    }

    public void addClassHistory(JavaClass jc) {
        this.classHistories.add(jc);
    }

}
