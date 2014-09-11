/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import project.latex.balloon.sensor.SensorReadFailedException;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialPortException;
import static org.junit.Assert.assertTrue;
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
public class UBloxGPSSensorTest {

    String sentence = "";

    /**
     * Test of getNmeaSentence method, of class GPSSensor.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfTooMuchDataInSerialBuffer() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        when(serial.read()).thenReturn('1');
        when(serial.availableBytes()).thenReturn(1);
        UBloxGPSSensor gps = new UBloxGPSSensor(serial);
        gps.getPolledSentence();

    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfNoSerialDataAvailable() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        when(serial.availableBytes()).thenReturn(0);
        UBloxGPSSensor gps = new UBloxGPSSensor(serial);
        gps.getPolledSentence();
    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfSerialWontOpen() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        doThrow(new SerialPortException()).when(serial).open(anyString(), anyInt());
        UBloxGPSSensor gps = new UBloxGPSSensor(serial);
        gps.getPolledSentence();
    }

    @Test(expected = SensorReadFailedException.class)
    public void testThrowsIfSerialWontRead() throws Exception {
        Serial serial = mock(Serial.class);
        when(serial.availableBytes()).thenReturn(1);
        doThrow(new IllegalStateException()).when(serial).read();
        UBloxGPSSensor gps = new UBloxGPSSensor(serial);
        gps.getPolledSentence();
    }

    @Test
    public void testShouldGetSentenceFromSerialBuffer() throws Exception {
        // Mock dependencies.
        Serial serial = mock(Serial.class);
        when(serial.read()).thenReturn('$').thenReturn('P').thenReturn('U')
                .thenReturn('B').thenReturn('X');
        when(serial.availableBytes()).thenReturn(1).thenReturn(1).thenReturn(1).
                thenReturn(1).thenReturn(1).thenReturn(1).thenReturn(0);

        UBloxGPSSensor gps = new UBloxGPSSensor(serial);
        String expected = "$PUBX";
        String result = gps.getPolledSentence();
        assertTrue(expected.equals(result));
    }

}
