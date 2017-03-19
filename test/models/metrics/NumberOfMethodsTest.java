package models.metrics;

import junit.framework.TestCase;

public class NumberOfMethodsTest extends TestCase {
    private NumberOfMethods nom;

    public void setUp() throws Exception {
        nom = new NumberOfMethods(100);
        super.setUp();
    }

    public void testGetValue() throws Exception {
        assertEquals(new Integer(100), nom.getValue());
    }

    public void testSetValue() throws Exception {
        nom.setValue(30);
        assertEquals(new Integer(30), nom.getValue());
    }
}