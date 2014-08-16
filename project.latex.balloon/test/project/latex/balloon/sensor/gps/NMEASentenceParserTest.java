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
public class NMEASentenceParserTest {
    
    public NMEASentenceParserTest() {
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
     * Test of parse method, of class NMEASentenceParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        String NMEASentence = "";
        HashMap<String, Object> expResult = null;
        HashMap<String, Object> result = NMEASentenceParser.parse(NMEASentence);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseGPGGA method, of class NMEASentenceParser.
     */
    @Test
    public void testParseGPGGA() throws Exception {
        System.out.println("parseGPGGA");
        String[] GPGGATokens = null;
        HashMap<String, Object> expResult = null;
        HashMap<String, Object> result = NMEASentenceParser.parseGPGGA(GPGGATokens);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseGPRMC method, of class NMEASentenceParser.
     */
    @Test
    public void testParseGPRMC() throws Exception {
        System.out.println("parseGPRMC");
        String[] GPRMCTokens = null;
        HashMap<String, Object> expResult = null;
        HashMap<String, Object> result = NMEASentenceParser.parseGPRMC(GPRMCTokens);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of latitudeToDecimal method, of class NMEASentenceParser.
     */
    @Test
    public void testLatitudeToDecimal() {
        System.out.println("latitudeToDecimal");
        String latitude = "";
        String bearing = "";
        double expResult = 0.0;
        double result = NMEASentenceParser.latitudeToDecimal(latitude, bearing);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of longitudeToDecimal method, of class NMEASentenceParser.
     */
    @Test
    public void testLongitudeToDecimal() {
        System.out.println("longitudeToDecimal");
        String longitude = "";
        String bearing = "";
        double expResult = 0.0;
        double result = NMEASentenceParser.longitudeToDecimal(longitude, bearing);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
