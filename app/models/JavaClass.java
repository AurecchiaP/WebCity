package models;

/**
 * A model for Java classes. The attributes for the position will later be moved
 */
public class JavaClass {

    private String name;
    private int methods;
    private int attributes;
    private int linesOfCode;

    public int cx;
    public int cy;
    public int cz;
    public int color;

    public JavaClass(String name, int methods, int attributes, int linesOfCode) {
        this.name = name;
        this.methods = methods;
        this.attributes = attributes;
        this.linesOfCode = linesOfCode;
    }

    /**
     * @return the name of the class
     */
    public String getName() {
        return name;
    }


    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }



    public int getLinesOfCode() {
        return linesOfCode;
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
