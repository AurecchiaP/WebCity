package models.drawables;


// TODO make it nice, don't return lists and stuff
import models.JavaPackage;

import java.util.ArrayList;
import java.util.List;

public class DrawablePackage extends Drawable {

    private int cx;
    private int cy;
    private int width;
    private int depth;
    private int z;
    private int color;
    private JavaPackage pkg;
    private List<DrawablePackage> drawablePackages;
    private List<DrawableClass> drawableClasses;

    public DrawablePackage(int cx, int cy, int z, int color, JavaPackage pkg) {
        this.cx = cx;
        this.cy = cy;
        this.z = z;
        this.color = color;
        this.pkg = pkg;
        this.drawablePackages = new ArrayList<>();
        this.drawableClasses = new ArrayList<>();
    }

    public int getCx() {
        return cx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public int getCy() {
        return cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public JavaPackage getPkg() {
        return pkg;
    }

    public void setPkg(JavaPackage pkg) {
        this.pkg = pkg;
    }

    public List<DrawablePackage> getDrawablePackages() {
        return drawablePackages;
    }

    public void setDrawablePackages(List<DrawablePackage> drawablePackages) {
        this.drawablePackages = drawablePackages;
    }

    public List<DrawableClass> getDrawableClasses() {
        return drawableClasses;
    }

    public void setDrawableClasses(List<DrawableClass> drawableClasses) {
        this.drawableClasses = drawableClasses;
    }

    /**
     * sorts the child packages by their size, in descending order
     */
    public void sortChildren() {
        this.drawablePackages.sort((c1, c2) -> {
            if (c1.width == c2.width)
                return 0;
            return c1.width > c2.width ? -1 : 1;
        });
    }


    /**
     * sorts the classes by their number of attributes, in descending order
     */
    public void sortClasses() {
        this.drawableClasses.sort((c1, c2) -> {
            if (c1.getCls().getAttributes().getValue().equals(c2.getCls().getAttributes().getValue()))
                return 0;
            return c1.getCls().getAttributes().getValue() > c2.getCls().getAttributes().getValue() ? -1 : 1;
        });
    }


}
