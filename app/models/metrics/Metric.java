package models.metrics;


public abstract class Metric<T> {

    public abstract T getValue();
    public abstract void setValue(T value);
}
