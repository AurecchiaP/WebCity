package models.metrics;


public class NumberOfMethods extends Metric<Integer> {

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
