package models.metrics;

import java.io.Serializable;

/**
 * Metric class that represents the number of attributes in a class
 */
public class NumberOfAttributes extends Metric<Integer> implements Serializable {

    private int noa;

    public NumberOfAttributes(int noa) {
        this.noa = noa;
    }

    /**
     * @return the Integer value representing the Number of Attributes
     */
    @Override
    public Integer getValue() {
        return noa;
    }

    /**
     * @param noa the Integer value representing the Number of Attributes
     */
    @Override
    public void setValue(Integer noa) {
        this.noa = noa;
    }
}
