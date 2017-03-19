package models.metrics;

import junit.framework.TestCase;

public class NumberOfAttributesTest extends TestCase {
    private NumberOfAttributes noa;

    public void setUp() throws Exception {
        noa = new NumberOfAttributes(100);
        super.setUp();
    }

    public void testGetValue() throws Exception {
        assertEquals(new Integer(100), noa.getValue());
    }

    public void testSetValue() throws Exception {
        noa.setValue(30);
        assertEquals(new Integer(30), noa.getValue());
    }
}