package utils;

/**
 *
 */
public class JavaClass {

    private String name;
    private int methods;
    private int attributes;

    public int cx;
    public int cy;
    public int cz;

    /**
     * @param name
     * @param methods
     * @param attributes
     */
    public JavaClass(String name, int methods, int attributes) {
        this.name = name;
        this.methods = methods;
        this.attributes = attributes;
    }

    public String getPath() {
        return name;
    }

    public int getMethods() {
        return methods;
    }

    public int getAttributes() {
        return attributes;
    }
}
