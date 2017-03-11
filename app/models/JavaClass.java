package models;

/**
 * A model for Java classes
 */
public class JavaClass {

    private String name;
    private int methods;
    private int attributes;

    public int cx;
    public int cy;
    public int cz;

    public JavaClass(String name, int methods, int attributes) {
        this.name = name;
        this.methods = methods;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public int getMethods() {
        return methods;
    }

    public int getAttributes() {
        return attributes;
    }
}
