package utils;

import models.JavaClass;
import models.JavaPackage;
import models.drawables.Drawable;
import models.drawables.DrawableClass;
import models.drawables.DrawablePackage;
import models.drawables.MaxDrawable;
import models.history.JavaPackageHistory;
import org.eclipse.jgit.internal.storage.pack.PackWriter;

import java.util.*;

import static utils.RectanglePacking.getMinClassesSize;


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

        //FIXME refactor into a class
        // map of all the packages present between all the versions
        Map<String, DrawablePackage> totalPackages = new HashMap<>();
        // Map(packageName, Map(className, DrawableClass))
        Map<String, Map<String, DrawableClass>> totalClasses = new HashMap<>();
        for (RectanglePacking packing : packings) {

//            System.out.println(packing.getVersion());

            packing.getDrwPackages().forEach((k, v) -> {
                if (!totalPackages.containsKey(k)) {
                    totalPackages.put(k, v);
                    totalClasses.put(k, new HashMap<>());
                }
//                    System.out.println("b " + totalClasses.get(k).size());
                    Map<String, DrawableClass> map = totalClasses.get(k);
                    v.getDrawableClasses().forEach(cls -> {
                        if(!map.containsKey(cls.getCls().getName()) || map.get(cls.getCls().getName()).getCls().getAttributes().getValue() < cls.getCls().getAttributes().getValue()) {
                            map.put(cls.getCls().getName(), cls);
                        }
                    });
//                    System.out.println("af " + totalClasses.get(k).size());
//                }
//                else {
//                    totalPackages.put(k, v);
//                    totalClasses.put(k, new HashMap<>());
//
//                }
            });
        }

        totalPackages.forEach(((k, drw) -> {
//            System.out.println(drw.getPkg().getName() + " " + totalClasses.get(k).values().size());
            drw.setDrawableClasses(new ArrayList<>(totalClasses.get(k).values()));
//            drw.setWidth(getMinClassesSize(drw));
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

    public static void compareWithMax(DrawablePackage drw, RectanglePacking packing) {
        // if in packing there is drw package
        if (packing.getDrwPackages().containsKey(drw.getPkg().getName())) {
            drw.setVisible(true);
            // if the package has some classes, place them
            if(drw.getClassesBin() != null) {
                ArrayList<DrawableClass> temp = new ArrayList<>();
                temp.addAll(packing.getDrwPackages().get(drw.getPkg().getName()).getDrawableClasses());
                drw.setDrawableClasses(temp);
                packing.fitClasses(drw.getClassesBin(), drw);
            }
        } else {
            drw.setVisible(false);
        }

        for (DrawablePackage childDrw : drw.getDrawablePackages()) {
            compareWithMax(childDrw, packing);
        }
    }
}
