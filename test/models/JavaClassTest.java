package models;

import models.metrics.LinesOfCode;
import models.metrics.NumberOfAttributes;
import models.metrics.NumberOfMethods;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaClassTest {

    JavaClass testClass;

    @Before
    public void setUp() throws Exception {
        testClass = new JavaClass("somefile.java", "test class", new NumberOfMethods(10), new NumberOfAttributes(5), new LinesOfCode(7));
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("test class", testClass.getName());
    }

    @Test
    public void testSetLinesOfCode() throws Exception {
        assertEquals(new Integer(7), testClass.getLinesOfCode().getValue());
        testClass.setLinesOfCode(new LinesOfCode(16));
        assertEquals(new Integer(16), testClass.getLinesOfCode().getValue());
    }

    @Test
    public void testGetLinesOfCode() throws Exception {
        assertEquals(new Integer(7), testClass.getLinesOfCode().getValue());
    }

    @Test
    public void testGetMethods() throws Exception {
        assertEquals(new Integer(10), testClass.getMethods().getValue());
    }

    @Test
    public void testGetAttributes() throws Exception {
        assertTrue(testClass.getAttributes() instanceof NumberOfAttributes);
        assertEquals(new Integer(5), testClass.getAttributes().getValue());
    }

}