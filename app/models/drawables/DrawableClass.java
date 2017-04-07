package models.drawables;


import models.JavaClass;

/**
 * class that wraps a JavaClass to add information like position and color for the visualization
 */
public class DrawableClass extends Drawable {

    /**
     * the center of the position of the drawable in the visualization, and its z "height"
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

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public JavaClass getCls() {
        return cls;
    }

    public void setCls(JavaClass cls) {
        this.cls = cls;
    }
}
