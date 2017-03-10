package utils;

/**
 * Class that represents a rectangle, defined by the 4 points x1, x2, y1, y2 and height z
 */
class Bin {
    public int x1;
    public int x2;
    public int y1;
    public int y2;
    public int z;

    Bin(int x1, int x2, int y1, int y2, int z) {
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
