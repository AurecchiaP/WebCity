package models.metrics;


public class NumberOfAttributes extends Metric<Integer> {

    private int noa;

    public NumberOfAttributes(int noa) {
        this.noa = noa;
    }

    @Override
    public Integer getValue() {
        return noa;
    }

    @Override
    public void setValue(Integer noa) {
        this.noa = noa;
    }
}
