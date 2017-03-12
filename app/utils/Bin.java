package utils;

/**
 * Class that represents a rectangle, defined by the 5 points x1, x2, y1, y2 and z.
 * z needs no upper bound since we are not stacking objects of varying size on the z axis.
 */
class Bin {
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private int z;

    Bin(int x1, int x2, int y1, int y2, int z) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z = z;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    /**
     * @return the width of this bin
     */
    int width() {
        return x2 - x1;
    }

    /**
     * @return the depth of this bin
     */
    int depth() {
        return y2 - y1;
    }

    /**
     * It takes 2 Bin, this and other, and enlarges this to have an area that also covers the area of other.
     * If this has area 0, its size and position become the same as other.
     *
     * @param other the Bin that will be merged with this
     */
    void mergeBin(final Bin other) {

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