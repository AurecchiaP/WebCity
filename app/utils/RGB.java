package utils;

public abstract class RGB {
    /**
     * Utility method that transforms a given rgb color to HEX
     *
     * @param r red hue, 0 to 255
     * @param g red hue, 0 to 255
     * @param b blue hue, 0 to 255
     * @return int that represents the given rbg color in HEX format
     */
    public static int RGBtoInt(int r, int g, int b) {
        return ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
    }
}
