package utils;

import models.JavaClass;
import models.JavaPackage;
import models.drawables.DrawableClass;
import models.drawables.DrawablePackage;

import java.util.List;

public abstract class DrawableUtils {

    public static DrawablePackage toDrawable(JavaPackage pkg) {

        DrawablePackage drw = new DrawablePackage(0,0,0,0, pkg);
        List<DrawablePackage> childDrawablePackages = drw.getDrawablePackages();
        List<DrawableClass> childDrawableClasses = drw.getDrawableClasses();

        for(JavaPackage child : pkg.getChildPackages()) {
            childDrawablePackages.add(toDrawable(child));
        }

        for(JavaClass cls : pkg.getClasses()) {
            DrawableClass drwCls = new DrawableClass(0,0,0,0, cls);
            childDrawableClasses.add(drwCls);
        }

        return drw;
    }
}
