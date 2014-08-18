/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor.gps;

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
    public void testGetCurrentData() throws Exception {
        System.out.println("getCurrentData");
        GPSSensorController instance = null;
        HashMap<String, Object> expResult = null;
        HashMap<String, Object> result = instance.getCurrentData();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLatitude method, of class UbloxGPSSensorController.
     */
    @Test
    public void testGetLatitude() throws Exception {
        System.out.println("getLatitude");
        GPSSensorController instance = null;
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
    public void testGetLongitude() throws Exception {
        System.out.println("getLongitude");
        GPSSensorController instance = null;
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
    public void testGetHeight() throws Exception {
        System.out.println("getHeight");
        GPSSensorController instance = null;
        double expResult = 0.0;
        double result = instance.getHeight();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
