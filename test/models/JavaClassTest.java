package models;

import junit.framework.TestCase;
import models.metrics.LinesOfCode;
import models.metrics.NumberOfAttributes;
import models.metrics.NumberOfMethods;

public class JavaClassTest extends TestCase {

    JavaClass testClass;

    public void setUp() throws Exception {
        testClass = new JavaClass("somefile.java", "test class", new NumberOfMethods(10), new NumberOfAttributes(5), new LinesOfCode(7));
        super.setUp();
    }

    public void testGetName() throws Exception {
        assertEquals("test class", testClass.getName());
    }

    public void testSetLinesOfCode() throws Exception {
        assertEquals(new Integer(7), testClass.getLinesOfCode().getValue());
        testClass.setLinesOfCode(new LinesOfCode(16));
        assertEquals(new Integer(16), testClass.getLinesOfCode().getValue());
    }

    public void testGetLinesOfCode() throws Exception {
        assertEquals(new Integer(7), testClass.getLinesOfCode().getValue());
    }

    public void testGetMethods() throws Exception {
        assertEquals(new Integer(10), testClass.getMethods().getValue());
    }

    public void testGetAttributes() throws Exception {
        assertTrue(testClass.getAttributes() instanceof NumberOfAttributes);
        assertEquals(new Integer(5), testClass.getAttributes().getValue());
    }

}