package utils;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BinTest {
    Bin bin;

    @Before
    public void setUp() throws Exception {
        bin = new Bin(0, 10, 0, 15, 5);
    }


    @Test
    public void testGetX1() throws Exception {
        assertEquals(0, bin.getX1());

    }

    @Test
    public void testSetX1() throws Exception {
        bin.setX1(100);
        assertEquals(100, bin.getX1());
    }

    @Test
    public void testGetX2() throws Exception {
        assertEquals(10, bin.getX2());
    }

    @Test
    public void testSetX2() throws Exception {
        bin.setX2(100);
        assertEquals(100, bin.getX2());
    }

    @Test
    public void testGetY1() throws Exception {
        assertEquals(0, bin.getY1());

    }

    @Test
    public void testSetY1() throws Exception {
        bin.setY1(100);
        assertEquals(100, bin.getY1());
    }

    @Test
    public void testGetY2() throws Exception {
        assertEquals(15, bin.getY2());
    }

    @Test
    public void testSetY2() throws Exception {
        bin.setY2(100);
        assertEquals(100, bin.getY2());
    }

    @Test
    public void testGetZ() throws Exception {
        assertEquals(5, bin.getZ());
    }

    @Test
    public void testSetZ() throws Exception {
        bin.setZ(100);
        assertEquals(100, bin.getZ());
    }

    @Test
    public void testWidth() throws Exception {
        assertEquals(10, bin.width());
        bin.setX1(20);
        bin.setX2(50);
        assertEquals(30, bin.width());
    }

    @Test
    public void testDepth() throws Exception {
        assertEquals(15, bin.depth());
        bin.setY1(20);
        bin.setY2(50);
        assertEquals(30, bin.depth());
    }

    @Test
    public void testMergeBin() throws Exception {

        // simple merge 1
        Bin bin1 = new Bin(0, 15, 10, 100, 10);
        bin.mergeBin(bin1);
        assertEquals(0, bin.getX1());
        assertEquals(15, bin.getX2());
        assertEquals(0, bin.getY1());
        assertEquals(100, bin.getY2());
        assertEquals(10, bin.getZ());

        // simple merge 2
        Bin bin2 = new Bin(-20, -10, -30, -15, 10);
        bin.mergeBin(bin2);
        assertEquals(-20, bin.getX1());
        assertEquals(15, bin.getX2());
        assertEquals(-30, bin.getY1());
        assertEquals(100, bin.getY2());

        // merge with 0 width bin
        Bin bin3 = new Bin(0, 0, 10, 100, 5);
        bin3.mergeBin(bin);
        assertEquals(bin3.getX1(), bin.getX1());
        assertEquals(bin3.getX2(), bin.getX2());
        assertEquals(bin3.getY1(), bin.getY1());
        assertEquals(bin3.getY2(), bin.getY2());
        assertEquals(bin3.getZ(), bin.getZ());
    }

}