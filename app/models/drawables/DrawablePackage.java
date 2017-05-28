package models.drawables;


import models.JavaPackage;
import utils.Bin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Class that wraps a JavaPackage to add information like position and color for the visualization
 */
public class DrawablePackage extends Drawable {

    /**
     * The center of the position of the drawable in the visualization, and its z "height"
     */
    private int cx;
    private int cy;
    private int z;

    private int width;
    private int depth;
    private boolean visible;
    private int color;
    /**
     * The JavaPackage that this drawable refers to
     */
    private JavaPackage pkg;
    private List<DrawablePackage> drawablePackages;
    private List<DrawableClass> drawableClasses;
    private Bin classesBin;

    public DrawablePackage(int cx, int cy, int z, int color, JavaPackage pkg) {
        this.cx = cx;
        this.cy = cy;
        this.z = z;
        this.color = color;
        this.pkg = pkg;
        this.drawablePackages = new ArrayList<>();
        this.drawableClasses = new ArrayList<>();
    }


    /**
     * Copy constructor
     */
    public DrawablePackage(DrawablePackage drwPkg) {
        this.cx = drwPkg.getCx();
        this.cy = drwPkg.getCy();
        this.z = drwPkg.getZ();
        this.color = drwPkg.getColor();
        this.pkg = drwPkg.getPkg();
        this.drawablePackages = new ArrayList<>();
        drwPkg.getDrawablePackages().forEach(childDrwPkg -> {
            this.drawablePackages.add(new DrawablePackage(childDrwPkg));
        });
        this.drawableClasses = new ArrayList<>();
        drwPkg.getDrawableClasses().forEach(childDrwCls -> {
            this.drawableClasses.add(new DrawableClass(childDrwCls));
        });
    }

    /**
     * @return get the x position of the center of this DrawablePackage
     */
    public int getCx() {
        return cx;
    }

    /**
     * @param cx set the x position of the center of this DrawablePackage
     */
    public void setCx(int cx) {
        this.cx = cx;
    }

    /**
     * @return get the y position of the center of this DrawablePackage
     */
    public int getCy() {
        return cy;
    }

    /**
     * @param cy set the y position of the center of this DrawablePackage
     */
    public void setCy(int cy) {
        this.cy = cy;
    }

    /**
     * @return the width of this DrawablePackage
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width tobe set to this DrawablePackage
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the depth of this DrawablePackage
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @param depth the depth tobe set to this DrawablePackage
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @return boolean value telling us if this DrawablePackage has to be visible or not
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the boolean value telling us if this DrawablePackage has to be visible or not
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return get the x position of this DrawablePackage
     */
    public int getZ() {
        return z;
    }

    /**
     * @param z set the z position of this DrawablePackage
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * @return int representing the color of this DrawablePackage
     */
    public int getColor() {
        return color;
    }

    /**
     * @param color int representing the color of this DrawablePackage
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * @return the JavaPackage that this DrawablePackage represents
     */
    public JavaPackage getPkg() {
        return pkg;
    }

    /**
     * @param pkg the JavaPackage that this DrawablePackage represents
     */
    public void setPkg(JavaPackage pkg) {
        this.pkg = pkg;
    }

    /**
     * @return the list of DrawablePackage contained in this DrawablePackage
     */
    public List<DrawablePackage> getDrawablePackages() {
        return Collections.unmodifiableList(drawablePackages);
    }

    /**
     * @param drawablePackages the list of DrawablePackage to be contained in this DrawablePackage
     */
    public void setDrawablePackages(List<DrawablePackage> drawablePackages) {
        this.drawablePackages.clear();
        this.drawablePackages.addAll(drawablePackages);
    }

    /**
     * @return the list of DrawableClass contained in this DrawablePackage
     */
    public List<DrawableClass> getDrawableClasses() {
        return Collections.unmodifiableList(drawableClasses);
    }

    /**
     * @param drawableClasses the list of DrawableClass to be contained in this DrawablePackage
     */
    public void setDrawableClasses(List<DrawableClass> drawableClasses) {
        this.drawableClasses.clear();
        this.drawableClasses.addAll(drawableClasses);
    }

    /**
     * @param drw the DrawablePackage to be added to the list of DrawablePackage contained in this DrawablePackage
     */
    public void addDrawablePackage(DrawablePackage drw) {
        this.drawablePackages.add(drw);
    }


    /**
     * @param cls the DrawableClass to be added to the list of DrawableClass contained in this DrawablePackage
     */
    public void addDrawableClass(DrawableClass cls) {
        this.drawableClasses.add(cls);
    }

    /**
     * @return the Bin in which the list of DrawableClass of this DrawablePackage will be put
     */
    public Bin getClassesBin() {
        return classesBin;
    }

    /**
     * @param classesBin the Bin in which the list of DrawableClass of this DrawablePackage will be put
     */
    public void setClassesBin(Bin classesBin) {
        this.classesBin = classesBin;
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
