package models.metrics;

import java.io.Serializable;

/**
 * Metric class that represents the number of lines of code in a class
 */
public class LinesOfCode extends Metric<Integer> implements Serializable {

    private int loc;

    public LinesOfCode(int loc) {
        this.loc = loc;
    }

    /**
     * @return the Integer value representing the number of Lines of Code
     */
    @Override
    public Integer getValue() {
        return loc;
    }

    /**
     * @param loc the Integer value representing the number of Lines of Code
     */
    @Override
    public void setValue(Integer loc) {
        this.loc = loc;
    }
}
