package utils;


import models.JavaClass;
import models.JavaPackage;

import java.util.List;
import java.util.ArrayList;

import static utils.RGB.RGBtoInt;


public class CubePacking {


    private int maxDepth = 0;


    /**
     * starts the calculations to find out the sizes and positions for each package and class contained in pkg
     *
     * @param pkg the root JavaPackage of the repository we want to visualize
     */
    public CubePacking(JavaPackage pkg) {

        Bin localBin = new Bin(0, 0, 0, 0, 0);
        List<Bin> openBins = new ArrayList<>();
        int recDepth = 0;

        // traverse recursively the packages to find out the maximum size needed for the visualization and the maximum
        // depth of recursion
        getPackageMaxWidth(pkg, recDepth);

        // traverse again, this time to find the positions for packages and classes and their colors
        recDepth = 0;
        pack(pkg, localBin, openBins, recDepth);
    }


    /**
     * recursively iterate from pkg, to find out the sizes of its children packages to find out the space needed to draw
     * the package pkg
     *
     * @param pkg the JavaPackage of which we want to find out the size
     * @param depth the current depth of recursion
     * @return the maximum edge size that pkg will occupy
     */
    // FIXME it doesn't give exact ordering, but its close enough
    // FIXME (by doing += width, we assume we're stacking them in one direction, not considering we could put them also
    // FIXME on top)
    // FIXME is this heuristic good enough?
    private int getPackageMaxWidth(JavaPackage pkg, int depth) {
        if (depth > maxDepth) maxDepth = depth;

        for (JavaPackage child : pkg.getChildPackages()) {
            pkg.w += getPackageMaxWidth(child, depth + 1);
        }

        // FIXME add 200 for the paddings (for now 2*paddings)
        pkg.w += getMinClassesSize(pkg) + 200;

        pkg.sortChildren();

        return pkg.w;
    }

    /**
     *
     *
     * @param pkg the current package we're trying to position/fit
     * @param parentBin the bin/space occupied by the parent package of pkg
     * @param parentOpenBins the available bins/free spaces where we could try to fit pkg
     * @param recDepth the current depth of recursion
     * @return the bin/size occupied by pkg
     */
    private Bin pack(JavaPackage pkg, Bin parentBin, List<Bin> parentOpenBins, int recDepth) {
        List<Bin> openBins = new ArrayList<>();
        Bin localBin = new Bin(0, 0, 0, 0, 0);
        boolean found = false;

        // FIXME try to put classesBin in openBins as well
        for (Bin bin : parentOpenBins) {
            if (bin.width() > pkg.w && bin.depth() > pkg.w) {
                localBin = new Bin(bin.getX1(), bin.getX1(), bin.getY1(), bin.getY1(), bin.getZ());
                found = true;
                parentOpenBins.remove(bin);
                break;
            }
        }


        if (!found) {
            // decide in which direction to "grow" the representation
            if (parentBin.depth() > parentBin.width()) {
                localBin = new Bin(parentBin.getX2(), parentBin.getX2(), parentBin.getY1(), parentBin.getY2(), parentBin.getZ());
            } else {
                localBin = new Bin(parentBin.getX1(), parentBin.getX2(), parentBin.getY2(), parentBin.getY2(), parentBin.getZ());
            }
        }

        // bin that will contain the classes of the current package
        Bin classesBin = new Bin(0, 0, 0, 0, 0);

        pkg.addClassTotal(pkg.getClasses().size());
        for (JavaPackage child : pkg.getChildPackages()) {
            Bin temp = pack(child, localBin, openBins, recDepth + 1);
            pkg.addClassTotal(child.getClassTotal());
            localBin.mergeBin(temp);
        }

        int minClassesSize = getMinClassesSize(pkg);

        if (localBin.depth() > localBin.width()) {
            classesBin.setX1(localBin.getX2());
            classesBin.setX2(localBin.getX2() + minClassesSize);
            classesBin.setY1(localBin.getY1());
            classesBin.setY2(localBin.getY1() + minClassesSize);
        } else {
            classesBin.setY1(localBin.getY2());
            classesBin.setY2(localBin.getY2() + minClassesSize);
            classesBin.setX1(localBin.getX1());
            classesBin.setX2(localBin.getX1() + minClassesSize);
        }

        localBin.mergeBin(classesBin);

        // makes localBin into a square
        if (localBin.depth() > localBin.width()) {
            localBin.setX2(localBin.getX2() + localBin.depth() - localBin.width());
        } else if (localBin.width() > localBin.depth()) {
            localBin.setY2(localBin.getY2() + localBin.width() - localBin.depth());
        }
        fitPackage(localBin, pkg);

        // add padding to right/top of packages
        localBin.setX2(localBin.getX2() + 100);
        localBin.setY2(localBin.getY2() + 100);

        // FIXME find better height for packages
        // FIXME when changing this, also change height in recDraw
        // set the height of the package, depending on the depth of the recursion of the current package
        pkg.z = recDepth * 50;

        //TODO interpolate between grey and red?
        pkg.color = RGBtoInt(255 * recDepth / maxDepth, 100, 100);

        fitClasses(classesBin, pkg);

        Bin remainderBin = new Bin(localBin.getX1(), localBin.getX2(), localBin.getY2(), parentBin.getY2() - localBin.getY2(), localBin.getZ());
        parentOpenBins.add(remainderBin);
        Bin remainderBin2 = new Bin(localBin.getX2(), parentBin.getX2() - localBin.getX2(), localBin.getY1(), localBin.getY2(), localBin.getZ());
        parentOpenBins.add(remainderBin2);

        return localBin;
    }
//
//    private void printBin(String name, Bin bin) {
//        System.out.println("x1:" + bin.x1 + " x2:" + bin.x2 + " y1:" + bin.y1 + " y2:" + bin.y2 + " " + name);
//    }

    /**
     * finds out a minimum edge size for the classes inside pkg to be drawn in.
     * it uses the heuristic that the needed space will be at most the number of classes contained in the package times
     * the size of the biggest class.
     *
     * @param pkg the package we want to know the minimum size of
     * @return the minimum edge size for this package
     */
    private int getMinClassesSize(JavaPackage pkg) {
        List<JavaClass> classes = pkg.getClasses();
        if (classes.size() == 0) {
            return 0;
        }

        pkg.sortClasses();

        // FIXME this isn't right, the second one should be right
        // FIXME find out why I have to scale it/ find the proper way to scale things
//        return classes.get(0).getMethods() * (classes.size()) / 2;
        return (int) Math.ceil(Math.sqrt(classes.get(0).getMethods() * (classes.size()))) * 40;
    }

    /**
     * sets the position of JavaPackage pkg to be contained inside the bin
     *
     * @param bin the Bin in which the JavaPackage pkg will be put
     * @param pkg the JavaPackage that will be put in bin
     */
    private void fitPackage(Bin bin, JavaPackage pkg) {
        pkg.cx = bin.getX1() + bin.width() / 2;
        pkg.cy = bin.getY1() + bin.depth() / 2;
        pkg.z = bin.getZ();
        pkg.w = bin.depth();
    }

    /**
     * sets the position of the list of classes contained in pkg to be contained inside the bin
     *
     * @param bin the Bin in which the classes will be put
     * @param pkg the JavaPackage that contains the classes that will be put in bin
     */
    private void fitClasses(Bin bin, JavaPackage pkg) {
        List<JavaClass> classes = pkg.getClasses();
        int totalClasses = classes.size();
        int cubesPerWidth = (int) Math.ceil(Math.sqrt(totalClasses)) + 2;
        int gridSpacing = (bin.getX2() - bin.getX1()) / (cubesPerWidth);
        for (int i = 0; i < totalClasses; ++i) {
            JavaClass cls = classes.get(i);
            int x = i % (cubesPerWidth - 1) + 1;
            int y = i / (cubesPerWidth - 1) + 1;
            cls.cx = bin.getX1() + gridSpacing * x;
            cls.cy = bin.getY1() + gridSpacing * y;
            cls.cz = pkg.z;
        }
    }

}
