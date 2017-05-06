package utils;


import models.drawables.DrawableClass;
import models.drawables.DrawablePackage;

import java.util.*;

import static utils.RGB.RGBtoInt;


public class RectanglePacking {


    private int maxDepth = 0;
    private int maxLines = 0;
    private String version;
    private static final int padding = 25;
    private Map<String, DrawablePackage> drwPackages;


    /**
     * starts the calculations to find out the sizes and positions for each package and class contained in pkg
     *
     * @param drwPkg the root JavaPackage of the repository we want to visualize
     */
    public RectanglePacking(DrawablePackage drwPkg, DrawablePackage maxDrw, String version) {
        this.version = version;

        // will contain a reference to all the drwPackages contained
        drwPackages = new HashMap<>();

        // traverse recursively the drwPackages to find out the maximum size needed for the visualization and the
        // maximum depth of recursion
        int recDepth = 0;

        getPackageHeuristicMaxWidth(drwPkg, recDepth);

        // traverse again, this time to find the positions for drwPackages and classes, using recDepth to set the color
        recDepth = 0;

        pack(drwPkg, maxDrw, new Bin(0, 0, 0, 0, 0), new ArrayList<>(), new ArrayList<>(), recDepth);
    }

    /**
     * recursively iterate from pkg, to find out the sizes of its children drwPackages to find out the space needed to
     * draw the package pkg (it's a heuristic, we stack them in a line, which gives a value that is bigger than
     * the ideal size; we don't know the size of a package before we do the rectangle packing)
     *
     * @param drwPkg the JavaPackage of which we want to find out the size
     * @param depth  the current depth of recursion
     * @return the maximum edge size that pkg will occupy
     */
    private int getPackageHeuristicMaxWidth(DrawablePackage drwPkg, int depth) {

        // store maximum depth of drwPackages reached
        if (depth > maxDepth) maxDepth = depth;

        // recursively iterate on child drwPackages
        for (DrawablePackage child : drwPkg.getDrawablePackages()) {
            drwPkg.setWidth(drwPkg.getWidth() + getPackageHeuristicMaxWidth(child, depth + 1));
        }

        // store the width
        drwPkg.setWidth(drwPkg.getWidth() + getMinClassesSize(drwPkg) + (2 * padding));
        drwPkg.setDepth(drwPkg.getWidth() + getMinClassesSize(drwPkg) + (2 * padding));

        for (DrawableClass cls : drwPkg.getDrawableClasses()) {
            if (cls.getCls().getLinesOfCode().getValue() > maxLines)
                maxLines = cls.getCls().getLinesOfCode().getValue();
        }

        // sort the children drwPackages by their size, in descending order
        drwPkg.sortChildren();
        return drwPkg.getWidth();
    }

    /**
     * takes care of positioning the JavaPackage pkg and its children and classes
     *
     * @param drwPkg         the current package we're trying to position/fit
     * @param maxDrw         the package containing the maximum sizes of all versions of the packages
     * @param parentBin      the bin/space occupied by the parent package of pkg
     * @param parentOpenBins the available bins/free spaces where we could try to fit pkg
     * @param recDepth       the current depth of recursion
     * @return the bin/size occupied by pkg
     */
    private Bin pack(DrawablePackage drwPkg,
                     DrawablePackage maxDrw,
                     Bin parentBin,
                     List<Bin> siblingBins,
                     List<Bin> parentOpenBins,
                     int recDepth) {
        drwPackages.put(drwPkg.getPkg().getName(), drwPkg);

        // the Bins available to pkg's children drwPackages
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
                Bin dummyBinBin = new Bin(
                        bin.getX1(),
                        bin.getX1() + drwPkg.getWidth(),
                        bin.getY1(),
                        bin.getY1() + drwPkg.getWidth(),
                        bin.getZ());
                dummyParentBin.mergeBin(dummyBinBin);
                int perimeter =
                        ((dummyParentBin.getX2() - dummyParentBin.getX1()) * 2)
                                +
                                ((dummyParentBin.getY2() - dummyParentBin.getY1()) * 2);

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
                localBin = new Bin(
                        parentBin.getX2(),
                        parentBin.getX2(),
                        parentBin.getY1(),
                        parentBin.getY2(),
                        parentBin.getZ());
            } else {
                localBin = new Bin(
                        parentBin.getX1(),
                        parentBin.getX2(),
                        parentBin.getY2(),
                        parentBin.getY2(),
                        parentBin.getZ());
            }
        }

        // add pkg's classes to the total of all classes contained in pkg (recursive as well)
        if (maxDrw == null) drwPkg.getPkg().addClassTotal(drwPkg.getDrawableClasses().size());

        // recursively pack the children of pkg before fitting pkg itself
        for (DrawablePackage child : drwPkg.getDrawablePackages()) {

            Bin childBin = pack(child, maxDrw, localBin, childrenBins, openBins, recDepth + 1);

            siblingBins.add(childBin);

            // add the classes of the children to the total number of classes of pkg
            if (maxDrw == null) drwPkg.getPkg().addClassTotal(child.getPkg().getClassTotal());

            // add the size of the child package to the size of pkg
            localBin.mergeBin(childBin);
        }

        if (drwPkg.getPkg().getClassTotal() == 0) {
            drwPkg.setWidth(0);
            return new Bin(
                    parentBin.getX1(),
                    parentBin.getX1(),
                    parentBin.getY1(),
                    parentBin.getY1(),
                    parentBin.getZ());
        }

        // bin that will contain the classes of the current package
        Bin classesBin = new Bin(0, 0, 0, 0, 0);
        int minClassesSize = getMinClassesSize(drwPkg);

        minimumWaste = Integer.MAX_VALUE;
        bestBin = null;

        for (Bin bin : openBins) {

            // if can fit in openBin
            if (bin.width() > minClassesSize && bin.depth() > minClassesSize) {

                Bin dummyLocalBin = localBin.copy();
                Bin dummyBinBin = new Bin(
                        bin.getX1(),
                        bin.getX1() + minClassesSize,
                        bin.getY1(),
                        bin.getY1() + minClassesSize,
                        bin.getZ());

                dummyLocalBin.mergeBin(dummyBinBin);
                int perimeter =
                        ((dummyLocalBin.getX2() - dummyLocalBin.getX1()) * 2)
                                +
                                ((dummyLocalBin.getY2() - dummyLocalBin.getY1()) * 2);

                if (perimeter < minimumWaste) {
                    minimumWaste = perimeter;
                    bestBin = bin;
                }
            }
        }

        if (bestBin != null) {
            classesBin = new Bin(
                    bestBin.getX1(),
                    bestBin.getX1() + minClassesSize,
                    bestBin.getY1(),
                    bestBin.getY1() + minClassesSize,
                    bestBin.getZ());
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
        drwPkg.setColor(RGBtoInt(
                100 + (120 * recDepth / maxDepth),
                100 + (120 * recDepth / maxDepth),
                100 + (120 * recDepth / maxDepth)));

        // shift classesBin by padding, draw it, and shift it back (we don't want to draw on the padding)
        addPaddingAndFit(classesBin, drwPkg, padding, recDepth, false);

        // free spots on left and right of local bin
        Bin openBinTop = new Bin(
                localBin.getX1(),
                Integer.MAX_VALUE,
                localBin.getY2(),
                Integer.MAX_VALUE,
                localBin.getZ());

        parentOpenBins.add(openBinTop);

        Bin openBinRight = new Bin(
                localBin.getX2(),
                Integer.MAX_VALUE,
                localBin.getY1(),
                Integer.MAX_VALUE,
                localBin.getZ());

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
     * first we shift to clear space for the padding, then we fit in the bin, then we shift back so that the other
     * drwPackages don't get put on top of the padding of this package.
     * then we add padding to the top right of the bin.
     *
     * @param bin         the bin inside which we want to put the object
     * @param drwPkg      the package that we want to position (or its classes)
     * @param padding     the padding to be added on each side
     * @param recDepth    the depth of the recursion of current package
     * @param drawPackage true if we want to draw the package, false for its classes
     */
    private void addPaddingAndFit(Bin bin, DrawablePackage drwPkg, int padding, int recDepth, boolean drawPackage) {
        // add padding on bottom left of package;
        // multiplied by depth of recursion to account space for the surrounding drwPackages
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
            drwPkg.setClassesBin(bin.copy());
        }

        bin.setX1(bin.getX1() - pad);
        bin.setY1(bin.getY1() - pad);
        bin.setX2(bin.getX2() - pad);
        bin.setY2(bin.getY2() - pad);

        // add padding to right/top of drwPackages
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
    public static int getMinClassesSize(DrawablePackage drwPkg) {
        List<DrawableClass> classes = drwPkg.getDrawableClasses();

        // if there are no classes, return 0
        if (classes.size() == 0) {
            return 0;
        }

        // sort the classes by their number of methods, in descending order
        drwPkg.sortClasses();

        // heuristic; assume every class is as big as the biggest class to find a lower bound for the size of the
        // package

        int size = 0;
        for (int i = 0; i < (int) Math.ceil(Math.sqrt(classes.size())); ++i) {
            size += (classes.get(i).getCls().getAttributes().getValue() * 3) + 5 + padding * 2;
        }
        return size;
//                ((classes.get(0).getCls().getAttributes().getValue() * 3) + 5 + padding * 2) // the biggest class
//                *
//                ((int) Math.ceil(Math.sqrt(classes.size()))); // an upper bound on the number of classes
    }


    /**
     * sets the position of JavaPackage pkg to be contained inside the bin
     *
     * @param bin    the Bin in which the JavaPackage pkg will be put
     * @param drwPkg the JavaPackage that will be put in bin
     */
    public void fitPackage(Bin bin, DrawablePackage drwPkg) {
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
    public void fitClasses(Bin bin, DrawablePackage drwPkg) {

        List<DrawableClass> classes = drwPkg.getDrawableClasses();
        int totalClasses = classes.size();

        // if there are no classes, return
        if (totalClasses == 0) return;

        // height between each line of classes
        int lineHeight = (classes.get(0).getCls().getAttributes().getValue() * 3) + 5;

        // positions of each class
        int xPos = bin.getX1();
        int yPos = bin.getY1() + padding;
        int classSize;

        // find the positions of the classes
        for (int i = 0; i < classes.size(); i++) {
            DrawableClass cls = classes.get(i);
            classSize = (cls.getCls().getAttributes().getValue() * 3) + 5;

            // if this X line is full, set X back to beginning and increase Y
            if (xPos + classSize + (padding * 2) > bin.getX2()) {
                xPos = bin.getX1();
                yPos += lineHeight + padding;
                lineHeight = (cls.getCls().getAttributes().getValue() * 3) + 5;

            }
            xPos += (classSize / 2) + padding;
            cls.setCx(xPos);
            xPos += (classSize / 2) + padding;
            cls.setCy(yPos + (classSize / 2));
            cls.setZ(drwPkg.getZ());

            cls.setColor(RGBtoInt(
                    255 - (200 * cls.getCls().getLinesOfCode().getValue() / maxLines),
                    255 - (200 * cls.getCls().getLinesOfCode().getValue() / maxLines),
                    255));
        }
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the map of packages contained in this rectangle packing
     */
    public Map<String, DrawablePackage> getDrwPackages() {
        return Collections.unmodifiableMap(drwPackages);
    }
}