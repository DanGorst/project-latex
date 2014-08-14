/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author will
 */
public class UbloxGPSSensorControllerTest {

    public UbloxGPSSensorControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getCurrentData method, of class UbloxGPSSensorController.
     */
    @Test
    public void testGetCurrentData() {
        System.out.println("getCurrentData");
        UbloxGPSSensorController instance = null;
        HashMap<String, Object> expResult = null;
        HashMap<String, Object> result = instance.getCurrentData();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of serialReadLine method, of class UbloxGPSSensorController.
     */
    @Test
    public void testSerialReadLine() {
        System.out.println("serialReadLine");
        UbloxGPSSensorController instance = null;
        String expResult = "";
        String result = instance.serialReadLine();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseNmeaCoordinateToDecimal method, of class
     * UbloxGPSSensorController.
     */
    @Test
    public void testValidNmeaCoordinateParsing() {
        // XXXX.XXX(...) format co-ordinate.
        String nmeaCoordinate = "1067.5847";
        String bearing = "N";
        UbloxGPSSensorController instance = new UbloxGPSSensorController("", "", "");
        double expResult = 11.1264;
        double result = instance.parseNmeaCoordinateToDecimal(nmeaCoordinate, bearing);
        assertEquals(expResult, result, 0.0001);
        // XXXXX.XXX(...) format co-ordinate and south bearing.
        nmeaCoordinate = "01067.5847";
        bearing = "S";
        expResult = -11.1264;
        result = instance.parseNmeaCoordinateToDecimal(nmeaCoordinate, bearing);
        assertEquals(expResult, result, 0.0001);
    }

    @Test
    public void testInvalidNmeaCoordinateParsing() {
        // Empty Strings as input.
        String nmeaCoordinate = "";
        String bearing = "";
        UbloxGPSSensorController instance = new UbloxGPSSensorController("", "", "");
        double expResult = -1;
        double result = instance.parseNmeaCoordinateToDecimal(nmeaCoordinate, bearing);
        assertEquals(expResult, result, 0.0001);
        // Incorrectly formated input.
        nmeaCoordinate = "010675.847";
        bearing = "S";
        expResult = -1;
        result = instance.parseNmeaCoordinateToDecimal(nmeaCoordinate, bearing);
        assertEquals(expResult, result, 0.0001);
        // Input contains invalid characters.
        nmeaCoordinate = "x10675.847";
        bearing = "S";
        expResult = -1;
        result = instance.parseNmeaCoordinateToDecimal(nmeaCoordinate, bearing);
        assertEquals(expResult, result, 0.0001);
        // Input contains no number after decimal.
        nmeaCoordinate = "10675.";
        bearing = "S";
        expResult = -1;
        result = instance.parseNmeaCoordinateToDecimal(nmeaCoordinate, bearing);
        assertEquals(expResult, result, 0.0001);
        // Input too short..
        nmeaCoordinate = "1067";
        bearing = "S";
        expResult = -1;
        result = instance.parseNmeaCoordinateToDecimal(nmeaCoordinate, bearing);
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of getLatitude method, of class UbloxGPSSensorController.
     */
    @Test
    public void testGetLatitude() {
        System.out.println("getLatitude");
        UbloxGPSSensorController instance = null;
        double expResult = 0.0;
        double result = instance.getLatitude();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLongitude method, of class UbloxGPSSensorController.
     */
    @Test
    public void testGetLongitude() {
        System.out.println("getLongitude");
        UbloxGPSSensorController instance = null;
        double expResult = 0.0;
        double result = instance.getLongitude();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHeight method, of class UbloxGPSSensorController.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        UbloxGPSSensorController instance = null;
        double expResult = 0.0;
        double result = instance.getHeight();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
