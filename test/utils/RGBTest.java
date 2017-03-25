package utils;

import junit.framework.TestCase;

public class RGBTest extends TestCase {
    public void testRGBtoInt() throws Exception {
        int r = 255;
        int g = 120;
        int b = 0;
        assertEquals(16742400, utils.RGB.RGBtoInt(r, g, b));
    }
}