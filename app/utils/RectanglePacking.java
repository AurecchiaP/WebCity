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
    private final int padding = 25;


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

        pack(drwPkg, new Bin(0, 0, 0, 0, 0), new ArrayList<>(), new ArrayList<>(), recDepth);
    }


    /**
     * recursively iterate from pkg, to find out the sizes of its children packages to find out the space needed to draw
     * the package pkg (it's a heuristic, we stack them in a line, which gives a value that is bigger than the ideal size;
     * we don't know the size of a package before we do the rectangle packing)
     *
     * @param drwPkg the JavaPackage of which we want to find out the size
     * @param depth  the current depth of recursion
     * @return the maximum edge size that pkg will occupy
     */
    private int getPackageMaxWidth(DrawablePackage drwPkg, int depth) {

        // store maximum depth of packages reached
        if (depth > maxDepth) maxDepth = depth;

        // recursively iterate on child packages
        for (DrawablePackage child : drwPkg.getDrawablePackages()) {
            drwPkg.setWidth(drwPkg.getWidth() + getPackageMaxWidth(child, depth + 1));
        }

        // store the width
        drwPkg.setWidth(drwPkg.getWidth() + getMinClassesSize(drwPkg) + (2 * padding));
        drwPkg.setDepth(drwPkg.getWidth() + getMinClassesSize(drwPkg) + (2 * padding));

        for (DrawableClass cls : drwPkg.getDrawableClasses()) {
            if (cls.getCls().getLinesOfCode().getValue() > maxLines)
                maxLines = cls.getCls().getLinesOfCode().getValue();
        }

        // sort the children packages by their size, in descending order
        drwPkg.sortChildren();
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
    private Bin pack(DrawablePackage drwPkg, Bin parentBin, List<Bin> siblingBins, List<Bin> parentOpenBins, int recDepth) {

        // the Bins available to pkg's children packages
        List<Bin> openBins = new ArrayList<>();
        List<Bin> childrenBins = new ArrayList<>();

        // the Bin occupied by pkg
        Bin localBin;

        // see if there is an open Bin in which pkg can fit into
        int minimumWaste = Integer.MAX_VALUE;
        Bin bestBin = null;

        for (Bin bin : parentOpenBins) {

            // if can fit in openBin
            if (bin.width() > drwPkg.getWidth() && bin.depth() > drwPkg.getDepth()) {

                Bin dummyParentBin = parentBin.copy();
                Bin dummyBinBin = new Bin(bin.getX1(), bin.getX1() + drwPkg.getWidth(), bin.getY1(), bin.getY1() + drwPkg.getWidth(), bin.getZ());
                dummyParentBin.mergeBin(dummyBinBin);
                int perimeter = ((dummyParentBin.getX2() - dummyParentBin.getX1()) * 2) + ((dummyParentBin.getY2() - dummyParentBin.getY1()) * 2);
                if (perimeter < minimumWaste) {
                    minimumWaste = perimeter;
                    bestBin = bin;
                }
            }
        }

        if (bestBin != null) {
            localBin = new Bin(bestBin.getX1(), bestBin.getX1(), bestBin.getY1(), bestBin.getY1(), bestBin.getZ());
        }

        // if no valid open Bin (e.g. first child of package), put local Bin either to the right or above of parentBin
        else {
//             decide in which direction to "grow" the representation to keep it as square as possible
            if (parentBin.depth() > parentBin.width()) {
                localBin = new Bin(parentBin.getX2(), parentBin.getX2(), parentBin.getY1(), parentBin.getY2(), parentBin.getZ());
            } else {
                localBin = new Bin(parentBin.getX1(), parentBin.getX2(), parentBin.getY2(), parentBin.getY2(), parentBin.getZ());
            }
        }


        // add pkg's classes to the total of all classes contained in pkg (recursive as well)
        drwPkg.getPkg().addClassTotal(drwPkg.getDrawableClasses().size());

        // pack the children of pkg before fitting pkg itself
        for (DrawablePackage child : drwPkg.getDrawablePackages()) {
            Bin childBin = pack(child, localBin, childrenBins, openBins, recDepth + 1);
            siblingBins.add(childBin);

            // add the classes of the children to the total number of classes of pkg
            drwPkg.getPkg().addClassTotal(child.getPkg().getClassTotal());

            // add the size of the child package to the size of pkg
            localBin.mergeBin(childBin);
        }

        if (drwPkg.getPkg().getClassTotal() == 0) {
            drwPkg.setWidth(0);
            return new Bin(0, 0, 0, 0, 0);
        }

        // FIXME make classes into rectangles and/or put them in bins
        // TODO to put them in bins: try to put them in a bin, and check for overlaps with all the other children

        // bin that will contain the classes of the current package
        Bin classesBin = new Bin(0, 0, 0, 0, 0);
        int minClassesSize = getMinClassesSize(drwPkg);

        minimumWaste = Integer.MAX_VALUE;
        bestBin = null;

        for (Bin bin : openBins) {

            // if can fit in openBin
            if (bin.width() > minClassesSize && bin.depth() > minClassesSize) {

                Bin dummyLocalBin = localBin.copy();
                Bin dummyBinBin = new Bin(bin.getX1(), bin.getX1() + minClassesSize, bin.getY1(), bin.getY1() + minClassesSize, bin.getZ());
                dummyLocalBin.mergeBin(dummyBinBin);
                int perimeter = ((dummyLocalBin.getX2() - dummyLocalBin.getX1()) * 2) + ((dummyLocalBin.getY2() - dummyLocalBin.getY1()) * 2);
                if (perimeter < minimumWaste) {
                    minimumWaste = perimeter;
                    bestBin = bin;
                }
            }
        }

        if (bestBin != null) {
            classesBin = new Bin(bestBin.getX1(), bestBin.getX1() + minClassesSize, bestBin.getY1(), bestBin.getY1() + minClassesSize, bestBin.getZ());
        }

        // if no valid open Bin (e.g. first child of package), put local Bin either to the right or above of parentBin
        else {
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
        }


        // update localBin with extra size of classesBin
        localBin.mergeBin(classesBin);

        // shift localBin by padding, draw it, and shift it back (we don't want to draw on the padding)
        addPaddingAndFit(localBin, drwPkg, padding, recDepth, true);

        // set the height of the package, depending on the depth of the recursion of the current package
        drwPkg.setZ(recDepth);

        // set color of package depending on depth of recursion
        drwPkg.setColor(RGBtoInt(100 + (120 * recDepth / maxDepth), 100 + (120 * recDepth / maxDepth), 100 + (120 * recDepth / maxDepth)));

        // shift classesBin by padding, draw it, and shift it back (we don't want to draw on the padding)
        addPaddingAndFit(classesBin, drwPkg, padding, recDepth, false);

        // free spots on left and right of local bin
        Bin openBinTop = new Bin(localBin.getX1(), Integer.MAX_VALUE, localBin.getY2(), Integer.MAX_VALUE, localBin.getZ());
        parentOpenBins.add(openBinTop);
        Bin openBinRight = new Bin(localBin.getX2(), Integer.MAX_VALUE, localBin.getY1(), Integer.MAX_VALUE, localBin.getZ());
        parentOpenBins.add(openBinRight);

        siblingBins.add(localBin);
        // check if any sibling overlaps an open bin, and if that happens 'cut' the bin in 4 new valid bins
        for (Bin sibling : siblingBins) {

            Iterator<Bin> it = parentOpenBins.iterator();
            List<Bin> temp = new ArrayList<>();
            while (it.hasNext()) {
                Bin bin = it.next();

                // if a sibling overlaps an open bin (=> bin is invalid)
                if (bin.overlap(sibling)) {
                    it.remove();

                    // top open bin
                    if (bin.getY2() > sibling.getY2()) {
                        temp.add(new Bin(bin.getX1(), bin.getX2(), sibling.getY2(), bin.getY2(), sibling.getZ()));
                    }
                    // bottom open bin
                    if (bin.getY1() < sibling.getY1()) {
                        temp.add(new Bin(bin.getX1(), bin.getX2(), bin.getY1(), sibling.getY1(), sibling.getZ()));
                    }

                    // right open bin
                    if (bin.getX2() > sibling.getX2()) {
                        temp.add(new Bin(sibling.getX2(), bin.getX2(), bin.getY1(), bin.getY2(), sibling.getZ()));
                    }
                    // left open bin
                    if (bin.getX1() < sibling.getX1()) {
                        temp.add(new Bin(bin.getX1(), sibling.getX1(), bin.getY1(), bin.getY2(), sibling.getZ()));
                    }
                }
            }

            // add all the new bins to the list of open bins
            parentOpenBins.addAll(temp);
        }

        return localBin;
    }


    /**
     * adds bottom-left padding to the bin before we find the position for the pkg/classes.
     * first we shift to clear space for the padding, then we fit in the bin, then we shift back so that the other packages
     * don't get put on top of the padding of this package.
     * then we add padding to the top right of the bin.
     *
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
        int classesPerWidth = (int) Math.ceil(Math.sqrt(totalClasses)) + 1;

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