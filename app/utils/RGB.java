package utils;

/**
 * Created by paolo on 3/5/17.
 */
public class RGB {
    public static int RGBtoInt(int r, int g, int b) {
        return ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
    }
}
