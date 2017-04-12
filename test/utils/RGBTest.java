package utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RGBTest  {

    @Test
    public void testRGBtoInt() throws Exception {
        int r = 255;
        int g = 120;
        int b = 0;
        assertEquals(16742400, utils.RGB.RGBtoInt(r, g, b));
    }
}