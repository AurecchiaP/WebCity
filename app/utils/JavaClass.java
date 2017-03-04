package utils;

public class JavaClass {

    private String path;
    private int methods;
    private int attributes;

    public int cx;
    public int cy;

    public JavaClass(String path, int methods, int attributes) {
        this.path = path;
        this.methods = methods;
        this.attributes = attributes;
    }

    public String getPath() {
        return path;
    }

    public int getMethods() {
        return methods;
    }

    public int getAttributes() {
        return attributes;
    }
}
