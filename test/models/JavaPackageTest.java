package models;

import models.metrics.LinesOfCode;
import models.metrics.NumberOfAttributes;
import models.metrics.NumberOfMethods;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class JavaPackageTest {

    JavaPackage testPackage;
    List<JavaClass> clss;


    @Before
    public void setUp() throws Exception {
        testPackage = new JavaPackage("test package");
        JavaClass testClass = new JavaClass("somefile.java", "test class", new NumberOfMethods(10), new NumberOfAttributes(5), new LinesOfCode(7));
        clss = new ArrayList<>();
        clss.add(testClass);
        testPackage.setClasses(clss);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("test package", testPackage.getName());
    }

    @Test
    public void testSetClasses() throws Exception {
        JavaClass testClass1 = new JavaClass("somefile1.java", "test class1", new NumberOfMethods(1), new NumberOfAttributes(2), new LinesOfCode(3));
        JavaClass testClass2 = new JavaClass("somefile1.java", "test class2", new NumberOfMethods(4), new NumberOfAttributes(5), new LinesOfCode(6));
        List<JavaClass> clss1 = new ArrayList<>();
        clss.add(testClass1);
        clss.add(testClass2);
        testPackage.setClasses(clss1);
        assertEquals(testPackage.getClasses(), clss1);
    }

    @Test
    public void testGetClasses() throws Exception {
        assertEquals(testPackage.getClasses(), clss);
    }

    @Test
    public void testSetChildPackages() throws Exception {
        JavaPackage testPackage1 = new JavaPackage("test package1");
        JavaPackage testPackage2 = new JavaPackage("test package2");
        testPackage.addChildPackage(testPackage1);
        testPackage.addChildPackage(testPackage2);
    }

    @Test
    public void testSortChildren() throws Exception {

    }

    @Test
    public void testSortClasses() throws Exception {

    }

    @Test
    public void testGetChildPackages() throws Exception {
        JavaPackage testPackage1 = new JavaPackage("test package1");
        JavaPackage testPackage2 = new JavaPackage("test package2");
        testPackage.addChildPackage(testPackage1);
        testPackage.addChildPackage(testPackage2);
        assertEquals(2, testPackage.getChildPackages().size());
    }

    @Test
    public void testAddChildPackage() throws Exception {
        JavaPackage testPackage1 = new JavaPackage("test package1");
        testPackage.addChildPackage(testPackage1);

    }

    @Test
    public void testAddClass() throws Exception {
        JavaClass testClass1 = new JavaClass("somefile1.java", "test class1", new NumberOfMethods(1), new NumberOfAttributes(2), new LinesOfCode(3));
        testPackage.addClass(testClass1);
    }

    @Test
    public void testAddClassTotal() throws Exception {
        testPackage.addClassTotal(10);
    }

    @Test
    public void testGetClassTotal() throws Exception {
        assertEquals(0, testPackage.getClassTotal());
        testPackage.addClassTotal(10);
        assertEquals(10, testPackage.getClassTotal());
        testPackage.addClassTotal(5);
        assertEquals(15, testPackage.getClassTotal());
    }

}