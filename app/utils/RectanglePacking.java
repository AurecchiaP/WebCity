package utils;


import models.JavaClass;
import models.JavaPackage;

import java.util.List;
import java.util.ArrayList;

import static utils.RGB.RGBtoInt;


public class RectanglePacking {


    private int maxDepth = 0;
    private final int padding = 100;


    /**
     * starts the calculations to find out the sizes and positions for each package and class contained in pkg
     *
     * @param pkg the root JavaPackage of the repository we want to visualize
     */
    public RectanglePacking(JavaPackage pkg) {

        // traverse recursively the packages to find out the maximum size needed for the visualization and the maximum
        // depth of recursion
        int recDepth = 0;
        getPackageMaxWidth(pkg, recDepth);

        // traverse again, this time to find the positions for packages and classes, using recDepth to set the color
        recDepth = 0;
        pack(pkg, new Bin(0,0,0,0, 0), new ArrayList<>(), recDepth);
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
    private int getPackageMaxWidth(JavaPackage pkg, int depth) {

        // store maximum depth of packages reached
        if (depth > maxDepth) maxDepth = depth;

        // recursively iterate on child packages
        for (JavaPackage child : pkg.getChildPackages()) {
            pkg.w += getPackageMaxWidth(child, depth + 1);
        }

        // store the width
        pkg.w += getMinClassesSize(pkg) + (2 * padding);

        // sort the children packages by their size, in descending order
        pkg.sortChildren();

        return pkg.w;
    }


    /**
     * takes care of positioning the JavaPackage pkg and its children and classes
     *
     * @param pkg the current package we're trying to position/fit
     * @param parentBin the bin/space occupied by the parent package of pkg
     * @param parentOpenBins the available bins/free spaces where we could try to fit pkg
     * @param recDepth the current depth of recursion
     * @return the bin/size occupied by pkg
     */
    private Bin pack(JavaPackage pkg, Bin parentBin, List<Bin> parentOpenBins, int recDepth) {

        // the Bins available to pkg's children packages
        List<Bin> openBins = new ArrayList<>();

        // the Bin occupied by pkg
        Bin localBin = new Bin(0, 0, 0, 0, 0);

        // found: if we have found an open Bin to fit pkg into
        boolean found = false;

        // FIXME try to put classesBin in openBins as well

        // see if there is an open Bin in which pkg can fit into
        for (Bin bin : parentOpenBins) {
            // if can fit in openBin
            if (bin.width() > pkg.w && bin.depth() > pkg.w) {
                localBin = new Bin(bin.getX1(), bin.getX1(), bin.getY1(), bin.getY1(), bin.getZ());
                found = true;
                parentOpenBins.remove(bin);
                break;
            }
        }

        // if no valid open Bin, put local Bin either to the right or above of parentBin
        if (!found) {
            // decide in which direction to "grow" the representation to keep it as square as possible
            if (parentBin.depth() > parentBin.width()) {
                localBin = new Bin(parentBin.getX2(), parentBin.getX2(), parentBin.getY1(), parentBin.getY2(), parentBin.getZ());
            } else {
                localBin = new Bin(parentBin.getX1(), parentBin.getX2(), parentBin.getY2(), parentBin.getY2(), parentBin.getZ());
            }
        }


        // bin that will contain the classes of the current package
        Bin classesBin = new Bin(0, 0, 0, 0, 0);


        // add pkg's classes to the total of all classes contained in pkg (recursive as well)
        pkg.addClassTotal(pkg.getClasses().size());

        // pack the children of pkg before fitting pkg itself
        for (JavaPackage child : pkg.getChildPackages()) {
            Bin childBin = pack(child, localBin, openBins, recDepth + 1);

            // add the classes of the children to the total number of classes of pkg
            pkg.addClassTotal(child.getClassTotal());

            // add the size of the child package to the size of pkg
            localBin.mergeBin(childBin);
        }

        // get the size to fit the classes of pkg into
        int minClassesSize = getMinClassesSize(pkg);

        // put the classes either to the right or above the local Bin
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

        // update localBin with extra size of classesBin
        localBin.mergeBin(classesBin);

        // make localBin into a square
        if (localBin.depth() > localBin.width()) {
            localBin.setX2(localBin.getX2() + localBin.depth() - localBin.width());
        } else if (localBin.width() > localBin.depth()) {
            localBin.setY2(localBin.getY2() + localBin.width() - localBin.depth());
        }


        // shift localBin by padding, draw it, and shift it back (we don't want to draw on the padding)
        addPaddingAndFit(localBin, pkg, padding, recDepth, true);

        // TODO when changing this, also change height in recDraw
        // set the height of the package, depending on the depth of the recursion of the current package
        pkg.z = recDepth;


        //TODO interpolate between grey and red?
        // set color of package depending on depth of recursion
        pkg.color = RGBtoInt(255 * recDepth / maxDepth, 100, 100);

        // shift classesBin by padding, draw it, and shift it back (we don't want to draw on the padding)
        addPaddingAndFit(classesBin, pkg, padding, recDepth, false);

        // make open bins for extra space that was left after fitting local bin, extra space both on top and on the right
        Bin openBinTop = new Bin(localBin.getX1(), localBin.getX2(), localBin.getY2(), parentBin.getY2() - localBin.getY2(), localBin.getZ());
        parentOpenBins.add(openBinTop);
        Bin openBinRight = new Bin(localBin.getX2(), parentBin.getX2() - localBin.getX2(), localBin.getY1(), localBin.getY2(), localBin.getZ());
        parentOpenBins.add(openBinRight);

        return localBin;
    }


    /**
     * adds bottom-left padding to the bin before we find the position for the pkg/classes.
     * first we shift to clear space for the padding, then we fit in the bin, then we shift back so that the other packages
     * don't get put on top of the padding of this package.
     * then we add padding to the top right of the bin.
     *
     *          old local bin                                new local bin
     *                                                     _____________________
     *           ____________                             |       padding       |
     *          |            |                            |     ____________    |
     *          |            |                            |  p |            |   |
     *          |            |                            |  a |            |   |
     *          |            |                            |  d |  drawn     |   |
     *          |            |                            |  d |  package   |   |
     *          |            |                            |  i |            |   |
     *          |____________|                            |  n |____________|   |
     *                                                    |  g    padding       |
     *                                                    |_____________________|
     *
     * @param bin the bin inside which we want to put the object
     * @param pkg the package that we want to position (or its classes)
     * @param padding the padding to be added on each side
     * @param recDepth the depth of the recursion of current package
     * @param drawPackage true if we want to draw the package, false for its classes
     */
    private void addPaddingAndFit(Bin bin, JavaPackage pkg, int padding, int recDepth, boolean drawPackage) {
        // add padding on bottom left of package; times depth of recursion to account space for the surrounding packages
        int pad = padding * recDepth;

        bin.setX1(bin.getX1() + pad);
        bin.setY1(bin.getY1() + pad);
        bin.setX2(bin.getX2() + pad);
        bin.setY2(bin.getY2() + pad);

        // draw the package or the classes of the package
        if(drawPackage) {
            fitPackage(bin, pkg);
        }else {
            fitClasses(bin, pkg);
        }

        bin.setX1(bin.getX1() - pad);
        bin.setY1(bin.getY1() - pad);
        bin.setX2(bin.getX2() - pad);
        bin.setY2(bin.getY2() - pad);

        // add padding to right/top of packages
        bin.setX2(bin.getX2() + 2 * padding);
        bin.setY2(bin.getY2() + 2 * padding);
    }


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

        // if there are no classes, return 0
        if (classes.size() == 0) {
            return 0;
        }

        // sort the classes by their number of methods, in descending order
        pkg.sortClasses();

        // FIXME find out why I have to scale it/ find the proper way to scale things
        // heuristic; assume every class is as big as the biggest class to find a lower bound for the size of the package
        return (int) Math.ceil(Math.sqrt((classes.get(0).getMethods()) * (classes.size()))) * (padding/2);
    }


    /**
     * sets the position of JavaPackage pkg to be contained inside the bin
     *
     * @param bin the Bin in which the JavaPackage pkg will be put
     * @param pkg the JavaPackage that will be put in bin
     */
    private void fitPackage(Bin bin, JavaPackage pkg) {
        // set position of package to center of bin
        pkg.cx = bin.getX1() + bin.width() / 2;
        pkg.cy = bin.getY1() + bin.depth() / 2;
        pkg.z = bin.getZ();
        pkg.w = bin.width();
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
        // calculate how many classes we can fit on one edge of the bin
        int classesPerWidth = (int) Math.ceil(Math.sqrt(totalClasses)) + 2;

        // calculate how far apart each class has to be
        int gridSpacing = (bin.getX2() - bin.getX1()) / (classesPerWidth);

        // find the positions of the classes on the grid
        for (int i = 0; i < totalClasses; ++i) {
            JavaClass cls = classes.get(i);
            int x = i % (classesPerWidth - 1) + 1;
            int y = i / (classesPerWidth - 1) + 1;
            cls.cx = bin.getX1() + gridSpacing * x;
            cls.cy = bin.getY1() + gridSpacing * y;
            cls.cz = pkg.z;
        }
    }

}