package models.drawables;

import java.util.HashMap;
import java.util.Map;

public class MaxDrawable {

    Map<String, Map<String, DrawableClass>> totalClasses;

    public MaxDrawable() {
        this.totalClasses = new HashMap<>();
    }
}
