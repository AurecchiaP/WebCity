package utils;

public class javaClass {

    private String path;
    private int methods;
    private int attributes;

    public javaClass(String path, int methods, int attributes) {
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
