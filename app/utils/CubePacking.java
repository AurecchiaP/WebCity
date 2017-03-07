package utils;


import java.util.ArrayList;


import static utils.RGB.RGBtoInt;


public class CubePacking {

    public int count = 0;

    public CubePacking(JavaPackage pkg) {
        Bin mainBin = new Bin(0, 0, 0, 0, 0);
        Bin localBin = new Bin(0, 0, 0, 0, 0);
        int recDepth = 0;
        pack(pkg, mainBin, localBin, recDepth);
    }

    private Bin pack(JavaPackage pkg, Bin mainBin, Bin parentBin, int recDepth) {
        Bin localBin;
        if (parentBin.depth() > parentBin.width()) {
            localBin = new Bin(parentBin.x2, parentBin.x2, parentBin.y1, parentBin.y2, parentBin.z);
        } else {
            localBin = new Bin(parentBin.x1, parentBin.x2, parentBin.y2, parentBin.y2, parentBin.z);
        }
        Bin classesBin = new Bin(0, 0, 0, 0, 0);

        // TODO parentBin is total size up to that point; localBin is size of this and of children
        if (count < 5) {
            printBin(pkg.getName(), localBin);
        }
        for (JavaPackage child : pkg.getChildren()) {
            Bin temp = pack(child, mainBin, localBin, recDepth + 1);
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
            classesBin.y2 = localBin.y2 + getMinSize(pkg);
            classesBin.x1 = localBin.x1;
            classesBin.x2 = localBin.x1 + getMinSize(pkg);
        }

        // FIXME this fitInBit should actually be drawing the classes; for now it's a package as a placeholder
        //        fitInBin(localBin, pkg);
        if (count < 5) {

            printBin("main bin ", mainBin);
            printBin("before classes merge ", localBin);
            printBin("classes bin ", classesBin);
            localBin.mergeBin(classesBin);
            printBin("after classes merge ", localBin);
        }

        // makes localBin into a square
        if (localBin.depth() > localBin.width()) {
            localBin.x2 += localBin.depth() - localBin.width();
        } else if (localBin.width() > localBin.depth()) {
            localBin.y2 += localBin.width() - localBin.depth();
        }
        // FIXME from 55 to 65 it breaks
        fitInBin(localBin, pkg);

        // space between neighboor packages
        localBin.x2 += 30;
        localBin.y2 += 30;

        // FIXME find better height for packages
        // FIXME when changing this, also change height in recDraw
        pkg.z = recDepth * 50;
        pkg.color = RGBtoInt(27 * recDepth, 100, 100);

        return localBin;
    }

    public void printBin(String name, Bin bin) {
        System.out.println("x1:" + bin.x1 + " x2:" + bin.x2 + " y1:" + bin.y1 + " y2:" + bin.y2 + " " + name);
    }

    private int getMinSize(JavaPackage pkg) {
        ArrayList<JavaClass> classes = pkg.getClasses();
        if (classes.size() == 0) {
            return 0;
        }

        // TODO maybe move the sorting directly in JavaClass
        // TODO this is DESCENDING sort
        classes.sort((c1, c2) -> {
            if (c1.getMethods() == c2.getMethods())
                return 0;
            return c1.getMethods() > c2.getMethods() ? -1 : 1;
        });


        // TODO bins are squares, so I take maximum class size times the number of classes/2,
        // TODO which is the minimum side of a square needed to fit in the classes
        return classes.get(0).getMethods() * classes.size() / 2;
    }

    private void fitInBin(Bin bin, JavaPackage pkg) {
        // TODO for now just center package; later, position every class in package
        pkg.cx = bin.x1 + bin.width() / 2;
        pkg.cy = bin.y1 + bin.depth() / 2;
        pkg.z = bin.z;
        pkg.w = bin.depth();
    }

    private class Bin {
        public int x1;
        public int x2;
        public int y1;
        public int y2;
        public int z;

        public Bin(int x1, int x2, int y1, int y2, int z) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.z = z;
        }

        int width() {
            return x2 - x1;
        }

        int depth() {
            return y2 - y1;
        }

        public void mergeBin(Bin other) {

            // this is empty bin


//            if (this.x2 < other.x1 || other.x2 < this.x1 ||
//                    this.y2 < other.y1 || other.y2 < this.y1) {
//                this.x1 = other.x1;
//                this.x2 = other.x2;
//                this.y1 = other.y1;
//                this.y2 = other.y2;
//                this.z = other.z;
//                return;
//            }
            if (this.width() == 0 || this.depth() == 0) {
                this.x1 = other.x1;
                this.x2 = other.x2;
                this.y1 = other.y1;
                this.y2 = other.y2;
                this.z = other.z;
                return;
            }
            if (other.x2 > this.x2) {
                this.x2 = other.x2;
            }

            if (other.x1 < this.x1) {
                this.x1 = other.x1;
            }

            if (other.y2 > this.y2) {
                this.y2 = other.y2;
            }

            if (other.y1 < this.y1) {
                this.y1 = other.y1;
            }

            if (other.z > this.z) {
                this.z = other.z;
            }
        }
    }
}
