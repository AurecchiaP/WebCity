package utils;


import java.util.ArrayList;


public class CubePacking {

    private Bin usedBin;
    private Bin openBin;

    public CubePacking(JavaPackage pkg) {
        this.usedBin = new Bin(0,0,0,0,0,0);
        this.openBin = new Bin(0,0,0,0,0,0);
        pack(pkg);
    }

    private void pack(JavaPackage pkg) {
        for(JavaPackage child : pkg.getChildren()) {
            pack(child);
        }

        // FIXME it's to cover the base case, not pretty
        if (openBin.width() == 0) {
            openBin.x2 = getMinSize(pkg) + 10;
            openBin.y2 = getMinSize(pkg) + 10;
        }

        if (openBin.width() > getMinSize(pkg)) {
            fitInBin(openBin, pkg);
        }
        else {
            usedBin.mergeBin(openBin);
        }
    }


    private int getMinSize(JavaPackage pkg) {
        ArrayList<JavaClass> classes = pkg.getClasses();
        if(classes.size() == 0) return -1;

        // TODO maybe move the sorting directly in JavaClass
        // TODO this is DESCENDING sort
        classes.sort((c1, c2) -> {
            if (c1.getMethods() == c2.getMethods())
                return 0;
            return c1.getMethods() > c2.getMethods() ? -1 : 1;
        });


        // TODO bins are squares, so I take maximum class size times the number of classes/2,
        // TODO which is the minimum side of a square needed to fit in the classes

        return classes.get(0).getMethods() * classes.size()/2;
    }

    private void fitInBin(Bin bin, JavaPackage pkg) {
        // TODO for now just center package; later, position every class in package
        pkg.cx = bin.x1 + bin.width()/2;
        pkg.cy = bin.y1 + bin.depth()/2;
        pkg.w = bin.width() - 10;
    }

    private class Bin {
        public int x1;
        public int x2;
        public int y1;
        public int y2;
        public int z1;
        public int z2;

        public Bin(int x1, int x2, int y1, int y2, int z1, int z2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.z1 = z1;
            this.z2 = z2;
        }

        int width() {
            return x1 + x2;
        }

        int depth() {
            return y1 + y2;
        }

        public void mergeBin(Bin openBin){
            if (openBin.x1 > this.x2) {
                this.x2 = openBin.x2;
            }
            else {
                this.y2 = openBin.y2;
            }
        }
    }
}
