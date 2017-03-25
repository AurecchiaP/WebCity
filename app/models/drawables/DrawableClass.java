package models.drawables;


import models.JavaClass;

public class DrawableClass extends Drawable {

    private int cx;
    private int cy;
    private int w;
    private int z;
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

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
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

    public JavaClass getCls() {
        return cls;
    }

    public void setCls(JavaClass cls) {
        this.cls = cls;
    }
}
