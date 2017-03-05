package utils;


import java.util.ArrayList;


import static utils.RGB.RGBtoInt;


public class CubePacking {

    private Bin usedBin;
    private Bin openBin;
    public int count = 0;

    public CubePacking(JavaPackage pkg) {
//        this.usedBin = new Bin(0,0,0,0,0,0);
//        this.openBin = new Bin(0,0,0,0,0,0);
        Bin mainBin = new Bin(0, 0, 0, 0, 0, 0);
        int recDepth = 0;
        pack(pkg, mainBin, recDepth);
    }

    private Bin pack(JavaPackage pkg, Bin parentBin, int recDepth) {

        Bin localBin = new Bin(0, 0, 0, 0, 0, 0);
        Bin classesBin = new Bin(0, 0, 0, 0, 0, 0);

        // TODO parentBin is total size up to that point; localBin is size of this and of children

        for (JavaPackage child : pkg.getChildren()) {
            Bin temp = pack(child, parentBin, recDepth + 1);
            localBin.mergeBin(temp);
            parentBin.mergeBin(temp);
//            localBin.mergeBin( pack(child, parentBin) );
        }


//        if (count >= 4)  return null;
//        count++;
        if (count < 20) {
//            printBin(parentBin);
            if (parentBin.depth() > parentBin.width()) {
                classesBin.x1 = parentBin.x2;
                classesBin.x2 = parentBin.x2 + getMinSize(pkg);
                classesBin.y1 = parentBin.y1;
                classesBin.y2 = parentBin.y1 + getMinSize(pkg);
            } else {
                classesBin.x1 = parentBin.x2;
                classesBin.x2 = parentBin.x2 + getMinSize(pkg);
                classesBin.y1 = parentBin.y1;
                classesBin.y2 = parentBin.y1 + getMinSize(pkg);
            }

            // FIXME this fitInBit should actually be drawing the classes; for now it's a package for as a placeholder
            //        fitInBin(localBin, pkg);
            localBin.mergeBin(classesBin);
//            printBin(localBin);
            if (localBin.depth() > localBin.width()) {
                localBin.x2 += localBin.depth() - localBin.width();
            } else if (localBin.width() > localBin.depth()) {
                localBin.y2 += localBin.width() - localBin.depth();
            }

            fitInBin(localBin, pkg);
            localBin.x2 += 10;
            localBin.y2 += 10;
        }

        pkg.z = recDepth * 10;
        pkg.color = RGBtoInt(30 * recDepth, 100, 100);
//        System.out.println(pkg.getName());
        count++;

        return localBin;
    }

    public void printBin(Bin bin) {
        System.out.println(" x1:" + bin.x1 + " x2:" + bin.x2 + " y1:" + bin.y1 + " y2:" + bin.y2 + " ");
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
        System.out.println(pkg.cx + " " + pkg.cy + " " + pkg.w);
    }

    private class Bin {
        public int x1;
        public int x2;
        public int y1;
        public int y2;
        public int z;

        public Bin(int x1, int x2, int y1, int y2, int z1, int z2) {
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
            if (this.x1 == this.x2) {
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
//
//            if (openBin.x1 > this.x2) {
//                this.x2 = openBin.x2;
//                this.y2 = max(openBin.y2, this.y2);
//            }
//            else {
//                this.y2 = openBin.y2;
//                this.x2 = max(openBin.x2, this.x2);
//            }
        }
    }
}
