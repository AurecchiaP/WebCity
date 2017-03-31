package utils;

import models.JavaClass;
import models.JavaPackage;
import models.drawables.DrawableClass;
import models.drawables.DrawablePackage;


public abstract class DrawableUtils {

    /**
     * takes a JavaPackage, and returns a (recursive) Drawable package that wraps the JavaPackage with additional
     * information like position and color used for the visualization
     *
     * @param pkg the root package of the structure to be transformed into drawables
     * @return the corresponding drawable structure
     */
    public static DrawablePackage toDrawable(JavaPackage pkg) {

        DrawablePackage drw = new DrawablePackage(0,0,0,0, pkg);

        for(JavaPackage child : pkg.getChildPackages()) {
            drw.addDrawablePackage(toDrawable(child));
        }

        for(JavaClass cls : pkg.getClasses()) {
            DrawableClass drwCls = new DrawableClass(0,0,0,0, cls);
            drw.addDrawableClass(drwCls);
        }

        return drw;
    }
}
