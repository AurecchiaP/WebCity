package utils;

import models.JavaClass;
import models.JavaPackage;
import models.drawables.Drawable;
import models.drawables.DrawableClass;
import models.drawables.DrawablePackage;
import models.history.JavaPackageHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class DrawableUtils {

    /**
     * takes a JavaPackage, and returns a (recursive) Drawable package that wraps the JavaPackage with additional
     * information like position and color used for the visualization
     *
     * @param pkg the root package of the structure to be transformed into drawables
     * @return the corresponding drawable structure
     */
    public static DrawablePackage packageToDrawable(JavaPackage pkg) {

        DrawablePackage drw = new DrawablePackage(0, 0, 0, 0, pkg);

        for (JavaPackage child : pkg.getChildPackages()) {
            drw.addDrawablePackage(packageToDrawable(child));
        }

        for (JavaClass cls : pkg.getClasses()) {
            DrawableClass drwCls = new DrawableClass(0, 0, 0, 0, cls);
            drw.addDrawableClass(drwCls);
        }

        return drw;
    }

    /**
     * given a JavaPackageHistory, we make a drawable of a specific version of the jph
     *
     * @param version the current version of the Drawable we're building
     * @param jph     the JavaPackageHistory we are making into a drawable
     * @return the new DrawablePackage of the corresponding version
     */
    public static DrawablePackage historyToDrawable(String version, JavaPackageHistory jph) {

        if (!jph.PackageHistoriesContains(version)) {
            return null;
        }
        JavaPackage pkg = jph.getPackageHistory(version);
        DrawablePackage drw = new DrawablePackage(0, 0, 0, 0, pkg);

        for (JavaPackageHistory child : jph.getJpChildren()) {
            if (child.PackageHistoriesContains(version)) {
                drw.addDrawablePackage(historyToDrawable(version, child));
            }
        }

        for (JavaClass cls : pkg.getClasses()) {
            DrawableClass drwCls = new DrawableClass(0, 0, 0, 0, cls);
            drw.addDrawableClass(drwCls);
        }

        return drw;
    }

    /**
     * takes as input a list of rectangle packings for all the versions, and creates a "maximum size" drawable package
     * where each package has the maximum size of all the versions
     *
     * @param packings the list of rectangle packings
     * @return the drawablePackage containing the maximum sizes for all the versions
     */
    public static DrawablePackage getMaxDrawable(List<RectanglePacking> packings) {

        // make a map nameOfPackage -> DrawablePackage that contains all the possible packages; if there's more than one
        // version, we take the biggest one
        Map<String, DrawablePackage> totalPackages = new HashMap<>();
        for (RectanglePacking packing : packings) {
            packing.getDrwPackages().forEach((k, v) -> totalPackages.merge(k, v, (v1, v2) -> v1.getWidth() * v1.getDepth() > v2.getWidth() * v2.getDepth() ? v1 : v2));
        }

        // remove the list of childPackages, since these are specific to the versions they came from
        for (DrawablePackage drw : totalPackages.values()) {
            drw.setDrawablePackages(new ArrayList<>());
            drw.getPkg().setChildPackages(new ArrayList<>());
        }

        // set the root package of the maximum drawable
        DrawablePackage maxDrw = totalPackages.get("");

        // sort the paths by size (the number of "/")
        List<String> keys = new ArrayList<>(totalPackages.keySet());
        keys.sort((s1, s2) -> s1.split("/").length - s2.split("/").length);

        // remove the root we added earlier so that we don't consider it twice
        keys.remove(0);

        // build the maxDrawable
        for (String s : keys) {
            String[] tokens = s.split("/");
            DrawablePackage current = maxDrw;
            for (String token : tokens) {
                // if we are at the last token
                if (token.equals(tokens[tokens.length - 1])) {
                    current.addDrawablePackage(totalPackages.get(s));
                }
                // else look for the drawable that token corresponds to
                else {
                    String currPath = current.getPkg().getName() + "/" + token;

                    for (DrawablePackage drw : current.getDrawablePackages()) {
                        if (drw.getPkg().getName().equals(currPath)) {
                            current = drw;
                            break;
                        }
                    }
                }
            }
        }
        return maxDrw;
    }

    public static void compareWithMax(DrawablePackage drw, RectanglePacking packing) {
        // if in packing there is drw package
        if(packing.getDrwPackages().containsKey(drw.getPkg().getName())) {
            drw.setVisible(true);
            // FIXME can't just overwrite, have to keep position
            // FIXME when making max package, have a set of classes contained?
        }
        else {
            drw.setVisible(false);
        }

        for (DrawablePackage childDrw : drw.getDrawablePackages()) {
            compareWithMax(childDrw, packing);
        }
    }
}
