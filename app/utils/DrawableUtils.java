package utils;

import models.JavaClass;
import models.JavaPackage;
import models.drawables.DrawableClass;
import models.drawables.DrawablePackage;
import models.history.JavaPackageHistory;


public abstract class DrawableUtils {

    /**
     * takes a JavaPackage, and returns a (recursive) Drawable package that wraps the JavaPackage with additional
     * information like position and color used for the visualization
     *
     * @param pkg the root package of the structure to be transformed into drawables
     * @return the corresponding drawable structure
     */
    public static DrawablePackage packageToDrawable(JavaPackage pkg) {

        DrawablePackage drw = new DrawablePackage(0,0,0,0, pkg);

        for(JavaPackage child : pkg.getChildPackages()) {
            drw.addDrawablePackage(packageToDrawable(child));
        }

        for(JavaClass cls : pkg.getClasses()) {
            DrawableClass drwCls = new DrawableClass(0,0,0,0, cls);
            drw.addDrawableClass(drwCls);
        }

        return drw;
    }

    public static DrawablePackage historyToDrawable(String version, JavaPackageHistory jph) {

        if(!jph.PackageHistoriesContains(version)) {
            return null;
        }
        JavaPackage pkg = jph.getPackageHistory(version);
        DrawablePackage drw = new DrawablePackage(0,0,0,0, pkg);

        for(JavaPackageHistory child : jph.getJpChildren()) {
            if(child.PackageHistoriesContains(version)) {
                drw.addDrawablePackage(historyToDrawable(version, child));
            }
        }

        for(JavaClass cls : pkg.getClasses()) {
            DrawableClass drwCls = new DrawableClass(0,0,0,0, cls);
            drw.addDrawableClass(drwCls);
        }

        return drw;
    }
}
