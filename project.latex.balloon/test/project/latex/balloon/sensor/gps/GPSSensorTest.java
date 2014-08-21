/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialPortException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author will
 */
public class GPSSensorTest {

    String sentence = "";

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
    public void testThrowsIfNo$InSerialOutput() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        when(serial.read()).thenReturn('1');
        when(serial.availableBytes()).thenReturn(1);
        GPSSensor mGps = new GPSSensor(serial,"GPGGA");
        mGps.getNmeaSentence("GPGGA");
        
    }
    
    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfOnlyOne$InSerialOutput() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        when(serial.read()).thenReturn('$').thenReturn('1');
        when(serial.availableBytes()).thenReturn(1);
        GPSSensor mGps = new GPSSensor(serial,"GPGGA");
        mGps.getNmeaSentence("GPGGA");
        
    }
    
    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfGPSHardwareDoesntSupportSentence() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        when(serial.read()).thenReturn('1').thenReturn('2').thenReturn('$')
                .thenReturn('G').thenReturn('P').thenReturn('R')
                .thenReturn('M').thenReturn('C').thenReturn('$')
                .thenReturn('1');
        when(serial.availableBytes()).thenReturn(1);
        GPSSensor mGps = new GPSSensor(serial,"GPGGA");
        mGps.getNmeaSentence("GPGGA");
        
    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfNoSerialDataAvailable() throws Exception {
         // Mock dependencies.
        Serial serial = mock(Serial.class);
        when(serial.availableBytes()).thenReturn(0);
        GPSSensor mGps = new GPSSensor(serial,"GPGGA");
        mGps.getNmeaSentence("GPGGA");
    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfSerialWontOpen() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        doThrow(new SerialPortException()).when(serial).open(anyString(),anyInt());
        GPSSensor mGps = new GPSSensor(serial,"GPGGA");
        mGps.getNmeaSentence("GPGGA");
    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfSerialWontRead() throws Exception {
        Serial serial = mock(Serial.class);
        when(serial.availableBytes()).thenReturn(1);
        doThrow(new IllegalStateException()).when(serial).read();
        GPSSensor mGps = new GPSSensor(serial,"GPGGA");
        mGps.getNmeaSentence("GPGGA");
    }

    @Test
    public void testShouldGetSentenceIfSupported() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        when(serial.read()).thenReturn('1').thenReturn('2').thenReturn('$').thenReturn('G').thenReturn('P')
                .thenReturn('R').thenReturn('M').thenReturn('C').thenReturn('$');
        when(serial.availableBytes()).thenReturn(1);
        
        GPSSensor mGps = new GPSSensor(serial,"GPRMC");
        String expected = "$GPRMC";
        String result = mGps.getNmeaSentence("GPRMC");
        assert(expected.equals(result));   
    }

}
