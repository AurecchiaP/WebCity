package models;

/**
 * A model for Java classes. The attributes for the position will later be moved
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

    /**
     * @return the name of the class
     */
    public String getName() {
        return name;
    }

    /**
     * @return the number of methods contained in the class
     */
    public int getMethods() {
        return methods;
    }

    /**
     * @return the number of attributes contained in the class
     */
    public int getAttributes() {
        return attributes;
    }
}
