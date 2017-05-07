package models.metrics;

import java.io.Serializable;

/**
 * metric class that represents the number of methods in a class
 */
public class NumberOfMethods extends Metric<Integer> implements Serializable {

    private int nom;

    public NumberOfMethods(int nom) {
        this.nom = nom;
    }

    @Override
    public Integer getValue() {
        return nom;
    }

    @Override
    public void setValue(Integer nom) {
        this.nom = nom;
    }
}
