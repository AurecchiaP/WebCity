package utils;


import models.drawables.DrawableClass;
import models.drawables.DrawablePackage;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import static utils.RGB.RGBtoInt;


public class RectanglePacking {


    private int maxDepth = 0;
    private int maxLines = 0;
    private final int padding = 10;


    /**
     * starts the calculations to find out the sizes and positions for each package and class contained in pkg
     *
     * @param drwPkg the root JavaPackage of the repository we want to visualize
     */
    public RectanglePacking(DrawablePackage drwPkg) {

        // traverse recursively the packages to find out the maximum size needed for the visualization and the maximum
        // depth of recursion
        int recDepth = 0;
        getPackageMaxWidth(drwPkg, recDepth);

        // traverse again, this time to find the positions for packages and classes, using recDepth to set the color
        recDepth = 0;

        List<Bin> openBins = new ArrayList<>();
        openBins.add(new Bin(0,9999,0,9999,0));

        pack(drwPkg, new Bin(0, 0, 0, 0, 0), openBins, recDepth);
    }


    /**
     * recursively iterate from pkg, to find out the sizes of its children packages to find out the space needed to draw
     * the package pkg
     *
     * @param drwPkg the JavaPackage of which we want to find out the size
     * @param depth  the current depth of recursion
     * @return the maximum edge size that pkg will occupy
     */
    // FIXME it doesn't give exact ordering, but its close enough
    // FIXME (by doing += width, we assume we're stacking them in one direction, not considering we could put them also
    // FIXME on top)
    private int getPackageMaxWidth(DrawablePackage drwPkg, int depth) {

        // store maximum depth of packages reached
        if (depth > maxDepth) maxDepth = depth;

        // recursively iterate on child packages
        for (DrawablePackage child : drwPkg.getDrawablePackages()) {
            drwPkg.setWidth(drwPkg.getWidth() + getPackageMaxWidth(child, depth + 1));
        }

        // store the width
        drwPkg.setWidth(drwPkg.getWidth() + getMinClassesSize(drwPkg) + (2 * padding));

        for (DrawableClass cls : drwPkg.getDrawableClasses()) {
            if (cls.getCls().getLinesOfCode().getValue() > maxLines)
                maxLines = cls.getCls().getLinesOfCode().getValue();
        }

        // sort the children packages by their size, in descending order
//        drwPkg.sortChildren();

        return drwPkg.getWidth();
    }

    /**
     * takes care of positioning the JavaPackage pkg and its children and classes
     *
     * @param drwPkg         the current package we're trying to position/fit
     * @param parentBin      the bin/space occupied by the parent package of pkg
     * @param parentOpenBins the available bins/free spaces where we could try to fit pkg
     * @param recDepth       the current depth of recursion
     * @return the bin/size occupied by pkg
     */
    private Bin pack(DrawablePackage drwPkg, Bin parentBin, List<Bin> parentOpenBins, int recDepth) {

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
            if (bin.width() > drwPkg.getWidth() && bin.depth() > drwPkg.getDepth()) {

                localBin = new Bin(bin.getX1(), bin.getX1(), bin.getY1(), bin.getY1(), bin.getZ());
                System.out.printf("bin %d %d %d %d \n", bin.getX1(), bin.getX2(), bin.getY1(), bin.getY2());
                parentOpenBins.remove(bin);
                break;
            }
        }

        // bin that will contain the classes of the current package
        Bin classesBin = new Bin(0, 0, 0, 0, 0);

        // add pkg's classes to the total of all classes contained in pkg (recursive as well)
        drwPkg.getPkg().addClassTotal(drwPkg.getDrawableClasses().size());

        // pack the children of pkg before fitting pkg itself
        for (DrawablePackage child : drwPkg.getDrawablePackages()) {
            Bin childBin = pack(child, localBin, openBins, recDepth + 1);

            // add the classes of the children to the total number of classes of pkg
            drwPkg.getPkg().addClassTotal(child.getPkg().getClassTotal());

            // add the size of the child package to the size of pkg
            localBin.mergeBin(childBin);
        }

        if (drwPkg.getPkg().getClassTotal() == 0) {
            drwPkg.setWidth(0);
            return new Bin(0, 0, 0, 0, 0);
        }

        // get the size to fit the classes of pkg into
        int minClassesSize = getMinClassesSize(drwPkg);

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

        // shift localBin by padding, draw it, and shift it back (we don't want to draw on the padding)
//        System.out.printf("%s %d %d %d %d \n", drwPkg.getPkg().getName(), localBin.getX1(), localBin.getX2(), localBin.getY1(), localBin.getY2());
        addPaddingAndFit(localBin, drwPkg, padding, recDepth, true);

        // set the height of the package, depending on the depth of the recursion of the current package
        drwPkg.setZ(recDepth);

        // set color of package depending on depth of recursion
        drwPkg.setColor(RGBtoInt(100 + (120 * recDepth / maxDepth), 100 + (120 * recDepth / maxDepth), 100 + (120 * recDepth / maxDepth)));

        // shift classesBin by padding, draw it, and shift it back (we don't want to draw on the padding)
        addPaddingAndFit(classesBin, drwPkg, padding, recDepth, false);

        Iterator<Bin> it = parentOpenBins.iterator();
        while (it.hasNext()) {
            Bin bin = it.next();
            if (bin.overlap(localBin)) {
                it.remove();
            }
        }

        // make open bins for extra space that was left after fitting local bin, extra space both on top and on the right
        Bin openBinTop = new Bin(localBin.getX2(), 9999, localBin.getY1(), 9999, localBin.getZ());
        parentOpenBins.add(openBinTop);
        Bin openBinRight = new Bin(localBin.getX1(), 9999, localBin.getY2(), 9999, localBin.getZ());
        parentOpenBins.add(openBinRight);

        return localBin;
    }


    /**
     * adds bottom-left padding to the bin before we find the position for the pkg/classes.
     * first we shift to clear space for the padding, then we fit in the bin, then we shift back so that the other packages
     * don't get put on top of the padding of this package.
     * then we add padding to the top right of the bin.

     * @param bin         the bin inside which we want to put the object
     * @param drwPkg      the package that we want to position (or its classes)
     * @param padding     the padding to be added on each side
     * @param recDepth    the depth of the recursion of current package
     * @param drawPackage true if we want to draw the package, false for its classes
     */
    private void addPaddingAndFit(Bin bin, DrawablePackage drwPkg, int padding, int recDepth, boolean drawPackage) {
        // add padding on bottom left of package; times depth of recursion to account space for the surrounding packages
        int pad = padding * recDepth;

        bin.setX1(bin.getX1() + pad);
        bin.setY1(bin.getY1() + pad);
        bin.setX2(bin.getX2() + pad);
        bin.setY2(bin.getY2() + pad);

        // draw the package or the classes of the package
        if (drawPackage) {
            fitPackage(bin, drwPkg);
        } else {
            fitClasses(bin, drwPkg);
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
     * @param drwPkg the package we want to know the minimum size of
     * @return the minimum edge size for this package
     */
    private int getMinClassesSize(DrawablePackage drwPkg) {
        List<DrawableClass> classes = drwPkg.getDrawableClasses();

        // if there are no classes, return 0
        if (classes.size() == 0) {
            return 0;
        }

        // sort the classes by their number of methods, in descending order
        drwPkg.sortClasses();

        // heuristic; assume every class is as big as the biggest class to find a lower bound for the size of the package
        return (classes.get(0).getCls().getAttributes().getValue() + 5 + padding * 2) * ((int) Math.ceil(Math.sqrt(classes.size())));
    }


    /**
     * sets the position of JavaPackage pkg to be contained inside the bin
     *
     * @param bin    the Bin in which the JavaPackage pkg will be put
     * @param drwPkg the JavaPackage that will be put in bin
     */
    private void fitPackage(Bin bin, DrawablePackage drwPkg) {
        // set position of package to center of bin
        drwPkg.setCx(bin.getX1() + bin.width() / 2);
        drwPkg.setCy(bin.getY1() + bin.depth() / 2);
        drwPkg.setZ(bin.getZ());
        drwPkg.setWidth(bin.width());
        drwPkg.setDepth(bin.depth());
    }


    /**
     * sets the position of the list of classes contained in pkg to be contained inside the bin
     *
     * @param bin    the Bin in which the classes will be put
     * @param drwPkg the JavaPackage that contains the classes that will be put in bin
     */
    private void fitClasses(Bin bin, DrawablePackage drwPkg) {

        List<DrawableClass> classes = drwPkg.getDrawableClasses();
        int totalClasses = classes.size();
        // calculate how many classes we can fit on one edge of the bin
        int classesPerWidth = (int) Math.ceil(Math.sqrt(totalClasses)) + 2;

        // calculate how far apart each class has to be
        int gridSpacing = (bin.getX2() - bin.getX1()) / (classesPerWidth);

        // find the positions of the classes on the grid
        for (int i = 0; i < totalClasses; ++i) {
            DrawableClass cls = classes.get(i);
            int x = i % (classesPerWidth - 1) + 1;
            int y = i / (classesPerWidth - 1) + 1;
            cls.setCx(bin.getX1() + gridSpacing * x);
            cls.setCy(bin.getY1() + gridSpacing * y);
            cls.setZ(drwPkg.getZ());
            cls.setColor(RGBtoInt(55 + (200 * cls.getCls().getLinesOfCode().getValue() / maxLines), 55 + (200 * cls.getCls().getLinesOfCode().getValue() / maxLines), 255));
        }
    }
}