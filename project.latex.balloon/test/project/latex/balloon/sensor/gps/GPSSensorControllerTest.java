/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyString;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 *
 * @author will
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({NMEASentenceParser.class})
public class GPSSensorControllerTest {
    private final Properties properties;
            
    public GPSSensorControllerTest() throws Exception {
        properties = new Properties();
        InputStream input = new FileInputStream("config.properties");
        properties.load(input);
    }

    /**
     * Test of getCurrentData method, of class GPSSensorController.
     * @throws java.lang.Exception
     */

    @Test
    public void testShouldReturnAllKeysWhenKeysAreSubsetOfParsedSentence() throws Exception {
        HashMap<String, String> parsedSentence = new HashMap<>();
        parsedSentence.put("time", "1");
        parsedSentence.put("latitude", "2");
        parsedSentence.put("longitude", "3");
        parsedSentence.put("altitude", "4");
        parsedSentence.put("speed", "5");  

        // Mock an instance of GPSSensor.
        GPSSensor ublox = mock(GPSSensor.class);
        HashSet<String> supportedNmeaSentences = new HashSet<>();
        supportedNmeaSentences.add("GPGGA");
        supportedNmeaSentences.add("GPRMC");
        when(ublox.getNmeaSentence(anyString())).thenReturn("");
        when(ublox.getSupportedNmeaSentences()).thenReturn(supportedNmeaSentences);
        // Mock static class NMEASentenceParser.
        mockStatic(NMEASentenceParser.class);
        when(NMEASentenceParser.parse(anyString())).thenReturn(parsedSentence);
        
        // Inject mocked dependencies into the class to be tested.
        GPSSensorController mController = new GPSSensorController(ublox,
                properties.getProperty("time.key"),
                properties.getProperty("latitude.key"),
                properties.getProperty("longitude.key"),
                properties.getProperty("altitude.key"),
                properties.getProperty("speed.key"));
        
        HashMap<String, Object> result = mController.getCurrentData();
        assertTrue(!result.isEmpty());
        assertTrue(result.get("time").equals("1"));
        assertTrue(result.get("latitude").equals("2"));
        assertTrue(result.get("longitude").equals("3"));
        assertTrue(result.get("altitude").equals("4"));
        assertTrue(result.get("speed").equals("5"));            
    }
    
    
     @Test
    public void testShouldReturnKeysOnlyForParsedSentenceDataThatExists() throws Exception {
        HashMap<String, String> parsedSentence = new HashMap<>();
        parsedSentence.put("time", "1");
        parsedSentence.put("latitude", "2");
        parsedSentence.put("longitude", "3");
        parsedSentence.put("altitude", "4");
        parsedSentence.put("speed", "5");  

        // Mock an instance of GPSSensor.
        GPSSensor ublox = mock(GPSSensor.class);
        HashSet<String> supportedNmeaSentences = new HashSet<>();
        supportedNmeaSentences.add("GPGGA");
        supportedNmeaSentences.add("GPRMC");
        when(ublox.getNmeaSentence(anyString())).thenReturn("");
        when(ublox.getSupportedNmeaSentences()).thenReturn(supportedNmeaSentences);
        // Mock static class NMEASentenceParser.
        mockStatic(NMEASentenceParser.class);
        when(NMEASentenceParser.parse(anyString())).thenReturn(parsedSentence);
        
        // Inject mocked dependencies into the class to be tested.
        GPSSensorController mController = new GPSSensorController(ublox,
                properties.getProperty("time.key"),
                properties.getProperty("latitude.key"),
                properties.getProperty("longitude.key"),
                properties.getProperty("altitude.key"));
        
        HashMap<String, Object> result = mController.getCurrentData();
        assertTrue(!result.isEmpty());
        assertTrue(result.get("time").equals("1"));
        result.remove("time");
        assertTrue(result.get("latitude").equals("2"));
        result.remove("latitude");
        assertTrue(result.get("longitude").equals("3"));
        result.remove("longitude");
        assertTrue(result.get("altitude").equals("4"));
        result.remove("altitude");
        assertTrue(result.isEmpty());        
    }
    
    @Test
    public void testShouldReturnNoKeysWhenNoDataInParsedSentence() throws Exception {
        HashMap<String, String> parsedSentence = new HashMap<>();

        // Mock an instance of GPSSensor.
        GPSSensor ublox = mock(GPSSensor.class);
        HashSet<String> supportedNmeaSentences = new HashSet<>();
        supportedNmeaSentences.add("GPGGA");
        supportedNmeaSentences.add("GPRMC");
        when(ublox.getNmeaSentence(anyString())).thenReturn("");
        when(ublox.getSupportedNmeaSentences()).thenReturn(supportedNmeaSentences);
        // Mock static class NMEASentenceParser.
        mockStatic(NMEASentenceParser.class);
        when(NMEASentenceParser.parse(anyString())).thenReturn(parsedSentence);
        
        // Inject mocked dependencies into the class to be tested.
        GPSSensorController mController = new GPSSensorController(ublox,
                properties.getProperty("time.key"),
                properties.getProperty("latitude.key"),
                properties.getProperty("longitude.key"),
                properties.getProperty("altitude.key"),
                properties.getProperty("speed.key"));
        
        HashMap<String, Object> result = mController.getCurrentData();
        assertTrue(result.isEmpty());           
    }
    
    @Test
    public void testShouldReturnNoKeysWhenNoKeysAreGiven() throws Exception {
        HashMap<String, String> parsedSentence = new HashMap<>();
        parsedSentence.put("time", "1");
        parsedSentence.put("latitude", "2");
        parsedSentence.put("longitude", "3");
        parsedSentence.put("altitude", "4");
        parsedSentence.put("speed", "5");  

        // Mock an instance of GPSSensor.
        GPSSensor ublox = mock(GPSSensor.class);
        HashSet<String> supportedNmeaSentences = new HashSet<>();
        supportedNmeaSentences.add("GPGGA");
        supportedNmeaSentences.add("GPRMC");
        when(ublox.getNmeaSentence(anyString())).thenReturn("");
        when(ublox.getSupportedNmeaSentences()).thenReturn(supportedNmeaSentences);
        // Mock static class NMEASentenceParser.
        mockStatic(NMEASentenceParser.class);
        when(NMEASentenceParser.parse(anyString())).thenReturn(parsedSentence);
        
        // Inject mocked dependencies into the class to be tested.
        GPSSensorController mController = new GPSSensorController(ublox);
        
        HashMap<String, Object> result = mController.getCurrentData();
        assertTrue(result.isEmpty());         
    }
}
