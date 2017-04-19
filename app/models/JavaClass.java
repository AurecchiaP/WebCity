package models;

import models.metrics.LinesOfCode;
import models.metrics.Metric;
import models.metrics.NumberOfAttributes;
import models.metrics.NumberOfMethods;

/**
 * A model for Java classes. The attributes for the position will later be moved
 */
public class JavaClass {

    private String filename;
    private String name;

    private Metric<Integer> methods;
    private Metric<Integer> attributes;
    private Metric<Integer> linesOfCode;

    public JavaClass(String filename, String name, Metric<Integer> methods, Metric<Integer> attributes, Metric<Integer> linesOfCode) {
        this.filename = filename;
        this.name = name;
        this.methods = methods;
        this.attributes = attributes;
        this.linesOfCode = linesOfCode;
    }

    /**
     * copy constructor
     */
    public JavaClass(JavaClass cls) {
        this.filename = cls.getFilename();
        this.name = cls.getName();
        this.methods = new NumberOfMethods(cls.getMethods().getValue());
        this.attributes = new NumberOfAttributes(cls.getAttributes().getValue());
        this.linesOfCode = new LinesOfCode(cls.getLinesOfCode().getValue());
    }

    /**
     * @return the name of the file that the class is contained in
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return the name of the class
     */
    public String getName() {
        return name;
    }

    /**
     * @param linesOfCode the new Metric that we will set to the class
     */
    public void setLinesOfCode(Metric<Integer> linesOfCode) {
        this.linesOfCode = linesOfCode;
    }


    /**
     * @return a Metric object representing the number of lines of code
     */
    public Metric<Integer> getLinesOfCode() {
        return new LinesOfCode(linesOfCode.getValue());
    }

    /**
     * @return a Metric object representing the number of methods
     */
    public Metric<Integer> getMethods() {
        return new NumberOfMethods(methods.getValue());
    }

    /**
     * @param methods the new Metric that we will set to the class
     */
    public void setMethods(Metric<Integer> methods) {
        this.methods = methods;
    }

    /**
     * @param attributes the new Metric that we will set to the class
     */
    public void setAttributes(Metric<Integer> attributes) {
        this.attributes = attributes;
    }



    /**
     * @return a Metric object representing the number of attributes
     */
    public Metric<Integer> getAttributes() {
        return new NumberOfAttributes(attributes.getValue());
    }
}
