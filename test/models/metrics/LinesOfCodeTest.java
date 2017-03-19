package models.metrics;

import junit.framework.TestCase;

public class LinesOfCodeTest extends TestCase {
    private LinesOfCode loc;

    public void setUp() throws Exception {
        loc = new LinesOfCode(100);
        super.setUp();
    }

    public void testGetValue() throws Exception {
        assertEquals(new Integer(100), loc.getValue());
    }

    public void testSetValue() throws Exception {
        loc.setValue(30);
        assertEquals(new Integer(30), loc.getValue());
    }
}