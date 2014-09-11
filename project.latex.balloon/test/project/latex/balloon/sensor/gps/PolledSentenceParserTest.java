/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import project.latex.balloon.sensor.SensorReadFailedException;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author will
 */
public class PolledSentenceParserTest {
    
    @Test (expected = SensorReadFailedException.class)
    public void testThrowsIfChecksumValueIsIncorrect() throws Exception {
        String polledSentence = "$PUBX,00,144024.00,0000.00000,N,00000.00000,E,"
                + "0.000,NF,5303315,3750010,0.000,0.00,0.000,,99.99,99.99,99.99,0,0,0*28";
        HashMap<String, String> result = PolledSentenceParser.parse(polledSentence);
    }
    
    @Test (expected = SensorReadFailedException.class)
    public void testThrowsIfChecksumFormatIsIncorrect() throws Exception {
        String polledSentence = "$PUBX,00,144024.00,0000.00000,N,00000.00000,E,"
                + "0.000,NF,5303315,3750010,0.000,0.00,0.000,,99.99,99.99,99.99,0,0,*29";
        HashMap<String, String> result = PolledSentenceParser.parse(polledSentence);
    }
    

    @Test (expected = SensorReadFailedException.class)
    public void testThrowsIfPolledSentenceFormatIsIncorrect() throws Exception {
        String polledSentence = "$Incorrect,00,144024.00,0000.00000,N,00000.00000,E,0.000,"
                + "NF,5303315,3750010,0.000,0.00,0.000,,99.99,99.99,99.99,0,0,0*29";
        HashMap<String, String> result = PolledSentenceParser.parse(polledSentence);
    }
    
    @Test (expected = SensorReadFailedException.class)
    public void testThrowsIfIncorrectTimeFormat() throws Exception {
        String polledSentence = "$PUBX,00,1440,0000.00000,N,00000.00000,E,0.000,"
                + "NF,5303315,3750010,0.000,0.00,0.000,,99.99,99.99,99.99,0,0,0*21";
        HashMap<String, String> result = PolledSentenceParser.parse(polledSentence);
    }
    
    @Test
    public void testLatitudeNLongitudeEShouldReturnPositive() throws Exception {
        String polledSentence = "$PUBX,00,144102.00,5055.10887,N,00218.80747,E,"
                + "112.473,,81,269,14.417,57.67,0.002,,5.98,1.00,4.45,3,0,0*21";
        String expectedLat = "50.918";
        String expectedLong = "2.314";

        HashMap<String, String> result = PolledSentenceParser.parse(polledSentence);
        String latitude = result.get("latitude");
        String longitude = result.get("longitude");

        assertEquals(Double.parseDouble(expectedLat), Double.parseDouble(latitude), 0.001);
        assertEquals(Double.parseDouble(expectedLong), Double.parseDouble(longitude), 0.001);
    }

    @Test
    public void testLatitudeSLongitudeWShouldReturnNegative() throws Exception {
        String polledSentence = "$PUBX,00,144102.00,5055.10887,S,00218.80747,W,"
                + "112.473,,81,269,14.417,57.67,0.002,,5.98,1.00,4.45,3,0,0*2E";
        String expectedLat = "-50.918";
        String expectedLong = "-2.314";

        HashMap<String, String> result = PolledSentenceParser.parse(polledSentence);
        String latitude = result.get("latitude");
        String longitude = result.get("longitude");

        assertEquals(Double.parseDouble(expectedLat), Double.parseDouble(latitude), 0.001);
        assertEquals(Double.parseDouble(expectedLong), Double.parseDouble(longitude), 0.001);

    }
    
    @Test
    public void testShouldReturnNoDataIfNoGPSFix() throws Exception {
        String polledSentence = "$PUBX,00,144024.00,0000.00000,N,00000.00000,E,0.000,"
                + "NF,5303315,3750010,0.000,0.00,0.000,,99.99,99.99,99.99,0,0,0*29";
        HashMap<String, String> result = PolledSentenceParser.parse(polledSentence);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testShouldReturnCorrectValuesForValidSentence() throws Exception {
        String polledSentence = "$PUBX,00,144102.00,5055.10887,N,00218.80747,W,"
                + "112.473,,81,269,14.417,57.67,0.002,,5.98,1.00,4.45,3,0,0*33";

        String expectedTime = "14:41:02";
        String expectedLat = "50.918";
        String expectedLong = "-2.314";
        String expectedAlt = "112.473";
        String expectedSpeed = "14.417";
        String expectedHeading = "57.67";

        HashMap<String, String> result = PolledSentenceParser.parse(polledSentence);
        String time = result.get("time");
        String latitude = result.get("latitude");
        String longitude = result.get("longitude");
        String altitude = result.get("altitude");
        String speed = result.get("speed");
        String heading = result.get("heading");
        
        assertEquals(expectedTime, time);
        assertEquals(Double.parseDouble(expectedLat), Double.parseDouble(latitude), 0.001);
        assertEquals(Double.parseDouble(expectedLong), Double.parseDouble(longitude), 0.001);
        assertEquals(Double.parseDouble(expectedAlt), Double.parseDouble(altitude), 0.1);
        assertEquals(Double.parseDouble(expectedSpeed), Double.parseDouble(speed), 0.001);
        assertEquals(Double.parseDouble(expectedHeading), Double.parseDouble(heading), 0.1);
    }
}
