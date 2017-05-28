package models.drawables;


import models.JavaClass;

/**
 * Class that wraps a JavaClass to add information like position and color for the visualization
 */
public class DrawableClass extends Drawable {

    /**
     * The center of the position of the drawable in the visualization, and its z "height"
     */
    private int cx;
    private int cy;
    private int z;

    private int width;
    private boolean visible;
    private int color;
    private JavaClass cls;

    public DrawableClass(int cx, int cy, int z, int color, JavaClass cls) {
        this.cx = cx;
        this.cy = cy;
        this.z = z;
        this.color = color;
        this.cls = cls;
    }

    /**
     * Copy constructor
     */
    public DrawableClass(DrawableClass drwCls) {
        this.cx = drwCls.getCx();
        this.cy = drwCls.getCy();
        this.z = drwCls.getZ();
        this.color = drwCls.getColor();
        this.cls = new JavaClass(drwCls.getCls());
    }

    /**
     * @return get the x position of the center of this DrawableClass
     */
    public int getCx() {
        return cx;
    }

    /**
     * @param cx set the x position of the center of this DrawableClass
     */
    public void setCx(int cx) {
        this.cx = cx;
    }

    /**
     * @return get the y position of the center of this DrawableClass
     */
    public int getCy() {
        return cy;
    }

    /**
     * @param cy set the y position of the center of this DrawableClass
     */
    public void setCy(int cy) {
        this.cy = cy;
    }

    /**
     * @return the width of this DrawableClass
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width tobe set to this DrawableClass
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return get the z position of this DrawableClass
     */
    public int getZ() {
        return z;
    }

    /**
     * @param z set the z position of this DrawableClass
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * @return boolean value telling us if this DrawableClass has to be visible or not
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the boolean value telling us if this DrawableClass has to be visible or not
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return int representing the color of this DrawableClass
     */
    public int getColor() {
        return color;
    }

    /**
     * @param color int representing the color of this DrawableClass
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * @return the JavaClass that this DrawableClass represents
     */
    public JavaClass getCls() {
        return cls;
    }

    /**
     * @param cls the JavaClass that this DrawableClass represents
     */
    public void setCls(JavaClass cls) {
        this.cls = cls;
    }
}
