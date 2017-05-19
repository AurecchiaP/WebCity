package models.metrics;

/**
 * Abstract class that will be extended by specific metric types
 */
public abstract class Metric<T> {

    public abstract T getValue();

    public abstract void setValue(T value);
}
