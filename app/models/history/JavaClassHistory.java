package models.history;

import models.JavaClass;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * A JavaClassHistory represents a container for the different versions of the class it represents.
 * classHistories is a list of different javaClass instances, each of which represent the JavaClass in a different
 * version of the system.
 */
public class JavaClassHistory implements Serializable {

    private String name;
    private List<JavaClass> classHistories;

    public JavaClassHistory(String name, List<JavaClass> classHistories) {
        this.name = name;
        this.classHistories = classHistories;
    }

    /**
     * @return the name of the class that this JavaClassHistory represents
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the class that this JavaClassHistory represents
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the list of JavaClass contained in this JavaClassHistory
     */
    public List<JavaClass> getClassHistories() {
        return Collections.unmodifiableList(classHistories);
    }

    /**
     * @param classHistories the list of JavaClass to be contained in this JavaClassHistory
     */
    public void setClassHistories(List<JavaClass> classHistories) {
        this.classHistories.clear();
        this.classHistories.addAll(classHistories);
    }

    /**
     * @param jc the JavaClass to be added to the list of classes contained in this JavaClassHistory
     */
    public void addClassHistory(JavaClass jc) {
        this.classHistories.add(jc);
    }

}
