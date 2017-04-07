package models.drawables;


import models.JavaPackage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * class that wraps a JavaPackage to add information like position and color for the visualization
 */
public class DrawablePackage extends Drawable {

    /**
     * the center of the position of the drawable in the visualization, and its z "height"
     */
    private int cx;
    private int cy;
    private int z;

    private int width;
    private int depth;
    private boolean visible;
    private int color;
    /**
     * the JavaPackage that this drawable refers to
     */
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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
        return Collections.unmodifiableList(drawablePackages);
    }

    public void setDrawablePackages(List<DrawablePackage> drawablePackages) {
        this.drawablePackages.clear();
        this.drawablePackages.addAll(drawablePackages);
    }

    public List<DrawableClass> getDrawableClasses() {
        return Collections.unmodifiableList(drawableClasses);
    }

    public void setDrawableClasses(List<DrawableClass> drawableClasses) {
        this.drawableClasses.clear();
        this.drawableClasses.addAll(drawableClasses);
    }

    public void addDrawablePackage(DrawablePackage drw) {
        this.drawablePackages.add(drw);
    }


    public void addDrawableClass(DrawableClass cls) {
        this.drawableClasses.add(cls);
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
