package models;

// TODO to set up later; it will contain JavaPackage or JavaClass, so that those classes wont have to contain info
// TODO about position and such
public class Drawable {

    private int cx;
    private int cy;
    private int w;
    private int z;
    private int color;
    private Object obj;

    public Drawable(int cx, int cy, int w, int z, int color, Object obj) {
        this.cx = cx;
        this.cy = cy;
        this.z = z;
        this.w = w;
        this.color = color;
        obj = obj;
    }

}
