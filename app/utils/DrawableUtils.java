package utils;

import models.JavaClass;
import models.JavaPackage;
import models.drawables.DrawableClass;
import models.drawables.DrawablePackage;
import models.history.JavaPackageHistory;

import java.util.*;


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

        // map of all the packages present between all the versions
        Map<String, DrawablePackage> totalPackages = new HashMap<>();
        // Map(packageName, Map(className, DrawableClass))
        Map<String, Map<String, DrawableClass>> totalClasses = new HashMap<>();

        // for every packing(which means every version)
        for (RectanglePacking packing : packings) {

            // for every package in a version, update the list of total classes adding (if necessary) the classes
            // of this specific version
            packing.getDrwPackages().forEach((k, v) -> {

                if (!totalPackages.containsKey(k)) {
                    DrawablePackage newDrwPkg = new DrawablePackage(v);
                    totalPackages.put(k, newDrwPkg);
                    totalClasses.put(k, new HashMap<>());
                }
                Map<String, DrawableClass> map = totalClasses.get(k);
                v.getDrawableClasses().forEach(cls -> {
                    String name = cls.getCls().getName();
                    if (!map.containsKey(name)
                            ||
                            map.get(name).getCls().getAttributes().getValue() < cls.getCls().getAttributes().getValue()) {
                        map.put(cls.getCls().getName(), new DrawableClass(cls));
                    }
                });
            });
        }

        // add the total list of classes to the corresponding package drawable
        totalPackages.forEach(((k, drw) -> {
            drw.setDrawableClasses(new ArrayList<>(totalClasses.get(k).values()));
        }));

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

    /**
     * this method sets to visible the packages and classes of the maximum drawable only if they are present in the
     * specific version represented by packing
     *
     * @param maxDrw  the drawable that contains all packages and classes considering all versions
     * @param packing the rectangle packing of a specific version we want to visualise
     */
    public static void compareWithMax(DrawablePackage maxDrw, RectanglePacking packing) {

        // if in packing there is drw package
        if (packing.getDrwPackages().containsKey(maxDrw.getPkg().getName())) {
            maxDrw.getPkg().setClassTotal(
                    packing.getDrwPackages().get(maxDrw.getPkg().getName()).getPkg().getClassTotal());

            maxDrw.setVisible(true);
            // if the package has some classes, place them
            if (maxDrw.getClassesBin() != null) {

                // set the correct classes to visible
                maxDrw.getDrawableClasses().forEach(maxDrwCls -> {
                    boolean found = false;
                    for (DrawableClass packingDrwCls : packing.getDrwPackages().get(maxDrw.getPkg().getName()).getDrawableClasses()) {
                        found = false;
                        if (packingDrwCls.getCls().getName().equals(maxDrwCls.getCls().getName())) {
                            found = true;
                            maxDrwCls.getCls().setLinesOfCode(packingDrwCls.getCls().getLinesOfCode());
                            maxDrwCls.getCls().setMethods(packingDrwCls.getCls().getMethods());
                            maxDrwCls.getCls().setAttributes(packingDrwCls.getCls().getAttributes());
                            break;
                        }
                    }
                    maxDrwCls.setVisible(found);
                });
            }
        } else {
            maxDrw.setVisible(false);
        }

        for (DrawablePackage childDrw : maxDrw.getDrawablePackages()) {
            compareWithMax(childDrw, packing);
        }
    }
}
