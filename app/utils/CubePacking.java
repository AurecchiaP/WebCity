package utils;


import models.JavaClass;
import models.JavaPackage;

import java.util.List;
import java.util.ArrayList;

import static utils.RGB.RGBtoInt;


public class CubePacking {

    private int maxDepth = 0;

    int count = 0;

    public CubePacking(JavaPackage pkg) {

        // not used
        Bin mainBin = new Bin(0, 0, 0, 0, 0);

        Bin localBin = new Bin(0, 0, 0, 0, 0);
        List<Bin> openBins = new ArrayList<>();
        int recDepth = 0;


        // FIXME sort by size to get better packing
        // FIXME have to iterate twice, ugly
        pkgWidth(pkg, recDepth);
        recDepth = 0;
        pack(pkg, mainBin, localBin, openBins, recDepth);
    }


    // FIXME it doesn't give exact ordering, but its close enough
    // FIXME (by doing += width, we assume we're stacking them in one direction, not considering we could put them also
    // FIXME on top)
    // FIXME is this heuristic good enough?
    private int pkgWidth(JavaPackage pkg, int depth) {
        if(depth > maxDepth) maxDepth = depth;

        for (JavaPackage child : pkg.getChildren()) {
            pkg.w += pkgWidth(child, depth + 1);
        }

        // FIXME add 200 for the paddings (for now 2*paddings)
        pkg.w += getMinSize(pkg) + 200;

        pkg.sortChildren();

        return pkg.w;
    }

    private Bin pack(JavaPackage pkg, Bin mainBin, Bin parentBin, List<Bin> parentOpenBins, int recDepth) {
        List<Bin> openBins = new ArrayList<>();
        Bin localBin = new Bin(0,0,0,0,0);
        boolean found = false;


        // FIXME try to put classesBin in openBins as well
        for(Bin bin : parentOpenBins) {
            if(bin.width() > pkg.w && bin.depth() > pkg.w) {
//                if( count < 4) {
                localBin = new Bin(bin.x1, bin.x1, bin.y1, bin.y1, bin.z);
                System.out.println(pkg.getName());
                System.out.println(pkg.w);
                printBin("parentBin", parentBin);
                printBin("openBin", bin);
                printBin("localBin", localBin);
                System.out.println("");
                found = true;
                parentOpenBins.remove(bin);
                count++;
                break;
//                }
            }
        }


        if(!found) {
            // decide in which direction to "grow" the representation
            if (parentBin.depth() > parentBin.width()) {
                localBin = new Bin(parentBin.x2, parentBin.x2, parentBin.y1, parentBin.y2, parentBin.z);
            } else {
                localBin = new Bin(parentBin.x1, parentBin.x2, parentBin.y2, parentBin.y2, parentBin.z);
            }
        }

        // bin that will contain the classes of the current package
        Bin classesBin = new Bin(0, 0, 0, 0, 0);


        for (JavaPackage child : pkg.getChildren()) {
            Bin temp = pack(child, mainBin, localBin, openBins, recDepth + 1);
            localBin.mergeBin(temp);
            mainBin.mergeBin(temp);
        }

        int minClassesSize = getMinSize(pkg);

        if (localBin.depth() > localBin.width()) {
            classesBin.x1 = localBin.x2;
            classesBin.x2 = localBin.x2 + minClassesSize;
            classesBin.y1 = localBin.y1;
            classesBin.y2 = localBin.y1 + minClassesSize;
        } else {
            classesBin.y1 = localBin.y2;
            classesBin.y2 = localBin.y2 + minClassesSize;
            classesBin.x1 = localBin.x1;
            classesBin.x2 = localBin.x1 + minClassesSize;
        }

        localBin.mergeBin(classesBin);

        // makes localBin into a square
        if (localBin.depth() > localBin.width()) {
            localBin.x2 += localBin.depth() - localBin.width();
        } else if (localBin.width() > localBin.depth()) {
            localBin.y2 += localBin.width() - localBin.depth();
        }
        fitInBin(localBin, pkg);

        // space between neighbor packages
        localBin.x2 += 100;
        localBin.y2 += 100;

        // FIXME find better height for packages
        // FIXME when changing this, also change height in recDraw
        pkg.z = recDepth * 50;

        //TODO interpolate between grey and red?
        pkg.color = RGBtoInt(255 * recDepth/maxDepth, 100, 100);

        if (pkg.getClasses().size() > 0) {
            fitClasses(classesBin, pkg);
        }

        Bin remainderBin = new Bin(localBin.x1, localBin.x2, localBin.y2, parentBin.y2 - localBin.y2, localBin.z);
        parentOpenBins.add(remainderBin);
        Bin remainderBin2 = new Bin(localBin.x2, parentBin.x2 - localBin.x2, localBin.y1, localBin.y2, localBin.z);
        parentOpenBins.add(remainderBin2);

        return localBin;
    }

    private void printBin(String name, Bin bin) {
        System.out.println("x1:" + bin.x1 + " x2:" + bin.x2 + " y1:" + bin.y1 + " y2:" + bin.y2 + " " + name);
    }

    private int getMinSize(JavaPackage pkg) {
        List<JavaClass> classes = pkg.getClasses();
        if (classes.size() == 0) {
            return 0;
        }

        pkg.sortClasses();


        // TODO bins are squares, so I take maximum class size times the number of classes/2,
        // TODO which is the minimum side of a square needed to fit in the classes

        // FIXME this isn't right, the second one should be right
        // FIXME find out why I have to scale it/ find the proper way to scale things
//        return classes.get(0).getMethods() * (classes.size()) / 2;
        return (int) Math.ceil(Math.sqrt(classes.get(0).getMethods() * (classes.size()))) * 40;
    }

    private void fitInBin(Bin bin, JavaPackage pkg) {
        pkg.cx = bin.x1 + bin.width() / 2;
        pkg.cy = bin.y1 + bin.depth() / 2;
        pkg.z = bin.z;
        pkg.w = bin.depth();
    }

    private void fitClasses(Bin bin, JavaPackage pkg) {
        List<JavaClass> classes = pkg.getClasses();
        int totalClasses = classes.size();
        int cubesPerWidth = (int) Math.ceil(Math.sqrt(totalClasses)) + 2;
        int gridSpacing = (bin.x2 - bin.x1) / (cubesPerWidth);
        for (int i = 0; i < totalClasses; ++i) {
            JavaClass cls = classes.get(i);
            int x = i % (cubesPerWidth - 1) + 1;
            int y = i / (cubesPerWidth - 1) + 1;
            cls.cx = bin.x1 + gridSpacing * x;
            cls.cy = bin.y1 + gridSpacing * y;
            cls.cz = pkg.z;
        }
    }

}
