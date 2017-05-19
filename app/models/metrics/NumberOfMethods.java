package models.metrics;

import java.io.Serializable;

/**
 * Metric class that represents the number of methods in a class
 */
public class NumberOfMethods extends Metric<Integer> implements Serializable {

    private int nom;

    public NumberOfMethods(int nom) {
        this.nom = nom;
    }

    /**
     * @return the Integer value representing the Number of Methods
     */
    @Override
    public Integer getValue() {
        return nom;
    }

    /**
     * @param nom the Integer value representing the Number of Methods
     */
    @Override
    public void setValue(Integer nom) {
        this.nom = nom;
    }
}
