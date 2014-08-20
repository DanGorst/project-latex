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

    @Test (expected = SensorReadFailedException.class)
    public void testShouldThrowIfZeroFixQualityInGppgaSentence() throws Exception {
        String NMEASentence = "$GPGGA,093509.00,5055.09099,N,00218.84655,W,0,04,6.50,101.6,M,48.2,M,,*4A";
        HashMap<String, String> result = NMEASentenceParser.parse(NMEASentence);
    }
    
    @Test (expected = SensorReadFailedException.class)
    public void testShouldThrowIfStatusVoidInGprmcSentence() throws Exception {
        String NMEASentence = "$GPRMC,093510.00,V,5055.09108,N,00218.84580,W,0.669,085.5,190814,,,A*6F";
        HashMap<String, String> result = NMEASentenceParser.parse(NMEASentence);
    }
    

    @Test
    public void testShouldReturnEmptyHashMapForUnsupportedSentence() throws Exception {
        String NMEASentence = "abcde,093509.00,5055.09099,N,00218.84655,W,1,04,6.50,101.6,M,48.2,M,,*4A";
        HashMap<String, String> result = NMEASentenceParser.parse(NMEASentence);
        assert (result.isEmpty());
    }

    @Test
    public void testShouldReturnAllValuesForCompleteGpggaSentence() throws Exception {
        String NMEASentence = "$GPGGA,093509.00,5055.09099,N,00218.84655,E,1,04,6.50,101.6,M,48.2,M,,*4A";
        String expectedTime = "09:35:09";
        String expectedLat = "50.918";
        String expectedLong = "2.314";
        String expectedAlt = "101.6";

        HashMap<String, String> result = NMEASentenceParser.parse(NMEASentence);
        String time = result.get("time");
        String latitude = result.get("latitude");
        String longitude = result.get("longitude");
        String altitude = result.get("altitude");

        assertEquals(expectedTime, time);
        assertEquals(Double.parseDouble(expectedLat), Double.parseDouble(latitude), 0.001);
        assertEquals(Double.parseDouble(expectedLong), Double.parseDouble(longitude), 0.001);
        assertEquals(Double.parseDouble(expectedAlt), Double.parseDouble(altitude), 0.1);
    }
    
    @Test
    public void testShouldReturnNotAvailableForIncompleteGpggaSentence() throws Exception {
        String NMEASentence = "$GPGGA,,,,,,,,,,,,,,";
        String expectedTime = "N/A";
        String expectedLat = "N/A";
        String expectedLong = "N/A";
        String expectedAlt = "N/A";

        HashMap<String, String> result = NMEASentenceParser.parse(NMEASentence);
        String time = result.get("time");
        String latitude = result.get("latitude");
        String longitude = result.get("longitude");
        String altitude = result.get("altitude");

        assertEquals(expectedTime, time);
        assertEquals(expectedLat, latitude);
        assertEquals(expectedLong, longitude);
        assertEquals(expectedAlt, altitude);
    }
    
    @Test
    public void testLatitudeNLongitudeEShouldReturnPositive() throws Exception {
        String NMEASentence = "$GPGGA,,5055.09099,N,00218.84655,E,1,,,,,,,,";
        String expectedTime = "09:35:09";
        String expectedLat = "50.918";
        String expectedLong = "2.314";
        String expectedAlt = "101.6";

        HashMap<String, String> result = NMEASentenceParser.parse(NMEASentence);
        String time = result.get("time");
        String latitude = result.get("latitude");
        String longitude = result.get("longitude");
        String altitude = result.get("altitude");

        assertEquals(Double.parseDouble(expectedLat), Double.parseDouble(latitude), 0.001);
        assertEquals(Double.parseDouble(expectedLong), Double.parseDouble(longitude), 0.001);
    }

    @Test
    public void testLatitudeSLongitudeWShouldReturnNegative() throws Exception {
        String NMEASentence = "$GPGGA,,5055.09099,S,00218.84655,W,1,,,,,,,,";
        String expectedTime = "09:35:09";
        String expectedLat = "-50.918";
        String expectedLong = "-2.314";
        String expectedAlt = "101.6";

        HashMap<String, String> result = NMEASentenceParser.parse(NMEASentence);
        String time = result.get("time");
        String latitude = result.get("latitude");
        String longitude = result.get("longitude");
        String altitude = result.get("altitude");

        assertEquals(Double.parseDouble(expectedLat), Double.parseDouble(latitude), 0.001);
        assertEquals(Double.parseDouble(expectedLong), Double.parseDouble(longitude), 0.001);

    }

    @Test
    public void testShouldReturnAllValuesForCompleteGprmcSentence() throws Exception {
        String NMEASentence = "$GPRMC,093510.00,A,5055.09108,N,00218.84580,W,0.669,085.5,190814,,,A*6F";
        String expectedDate = "190814";
        String expectedSpeed = "1.238";
        String expectedCourseOverGround = "085.5";

        HashMap<String, String> result = NMEASentenceParser.parse(NMEASentence);
        String date = result.get("date");
        String speed = result.get("speed");
        String courseOverGround = result.get("course over ground");

        assertEquals(expectedDate, date);
        assertEquals(Double.parseDouble(expectedSpeed), Double.parseDouble(speed), 0.001);
        assertEquals(Double.parseDouble(expectedCourseOverGround), Double.parseDouble(courseOverGround), 0.1);
    }

    @Test
    public void testShouldReturnNotAvailableForIncompleteGprmcSentence() throws Exception {
        String NMEASentence = "$GPRMC,,,,,,,,,,,,";
        String expectedDate = "N/A";
        String expectedSpeed = "N/A";
        String expectedCourseOverGround = "N/A";

        HashMap<String, String> result = NMEASentenceParser.parse(NMEASentence);
        String date = result.get("date");
        String speed = result.get("speed");
        String courseOverGround = result.get("course over ground");

        assertEquals(expectedDate, date);
        assertEquals(expectedSpeed, speed);
        assertEquals(expectedCourseOverGround, courseOverGround);
    }
}
