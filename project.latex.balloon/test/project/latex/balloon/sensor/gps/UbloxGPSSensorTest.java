/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor.gps;

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
public class UbloxGPSSensorTest {
    
    public UbloxGPSSensorTest() {
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
     * Test of getNMEASentence method, of class UbloxGPSSensor.
     */
    @Test
    public void testGetNMEASentence() throws Exception {
        System.out.println("getNMEASentence");
        String GPXXX = "";
        GPSSensor instance = new GPSSensor();
        String expResult = "";
        String result = instance.getNMEASentence(GPXXX);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
