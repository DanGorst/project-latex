/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import com.pi4j.io.serial.Serial;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author will
 */
public class GPSSensorTest {

    String sentence = "";

    public GPSSensorTest() {
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
     * Test of getNmeaSentence method, of class GPSSensor.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfArgUnsupportedByObject() throws Exception {
        GPSSensor mGps = new GPSSensor("GPGGA");
        mGps.getNmeaSentence("GPRMC");
    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfArgUnsupprtedByHardware() throws Exception {
        
    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfNoSerialDataAvailable() {

    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfSerialWontOpen() {

    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfSerialWontRead() {

    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfSerialWontClose() {

    }

    @Test
    public void testShouldGetSentenceIfSupported() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        when(serial.read()).thenReturn('$').thenReturn('G').thenReturn('P')
                .thenReturn('R').thenReturn('M').thenReturn('C').thenReturn('\n');
        
        GPSSensor mGps = new GPSSensor("GPRMC");
        String expected = "$GPRMC";
        System.out.println(expected);
        String result = mGps.getNmeaSentence("GPRMC");
        System.out.println(result);
        assert(expected.equals(result));
        
    }

}
