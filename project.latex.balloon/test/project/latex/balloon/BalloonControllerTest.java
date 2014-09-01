/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import project.latex.balloon.sensor.SensorController;
import project.latex.balloon.sensor.SensorReadFailedException;
import project.latex.writer.DataWriter;

/**
 *
 * @author dgorst
 */
public class BalloonControllerTest {
    
    private Properties properties;
    private File testImagesDir;
    
    public BalloonControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("logger.properties");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.properties = new Properties();
        String testImagesDirPath = "test/images";
        testImagesDir = new File(testImagesDirPath);
        testImagesDir.mkdir();
        this.properties.setProperty("cameraDir", testImagesDirPath);
    }
    
    @After
    public void tearDown() throws IOException {
        this.properties = null;
        TestFileDeleteHelper.delete(testImagesDir);
    }

    /**
     * Test of loadTransmittedDataKeys method, of class BalloonController.
     * @throws java.lang.Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadTransmittedDataKeysThrowsIfFilePathIsNull() throws Exception {
        BalloonController.loadTransmittedDataKeys(null);
    }
    
    @Test(expected = FileNotFoundException.class)
    public void testLoadTransmittedDataKeysThrowsIfFilePathIsInvalid() throws Exception {
        BalloonController.loadTransmittedDataKeys("invalid");
    }
    
    @Test
    public void testLoadTransmittedDataKeysSucceedsWithValidFile() throws IOException  {
        List<String> actual = BalloonController.loadTransmittedDataKeys("test/testKeys.json");
        List<String> expected = new ArrayList<>();
        expected.add("first");
        expected.add("second");
        expected.add("last");
        assertEquals(expected, actual);
    }

    /**
     * Test of createBalloonController method, of class BalloonController.
     * @throws java.lang.Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateBalloonControllerThrowsWithNullKeysFilePath() throws Exception {
        BalloonController.createBalloonController(null, this.properties);
    }
    
    @Test(expected = FileNotFoundException.class)
    public void testCreateBalloonControllerThrowsWithInvalidKeysFilePath() throws IOException  {
        BalloonController.createBalloonController("invalid", this.properties);
    }
    
    @Test
    public void testCreateBalloonControllerSucceedsWithValidKeysFilePath() throws IOException  {
        BalloonController controller = BalloonController.createBalloonController("test/testKeys.json", this.properties);
        assertNotNull(controller);
        assertNotNull(controller.getCameraSensor());
        assertNotNull(controller.getCameraWriter());
        assertNotNull(controller.getConverter());
        assertNotNull(controller.getDataWriters());
        assertNotNull(controller.getSensors());
        assertEquals("$$latex", controller.getPayloadName());
        
        List<String> expectedKeys = new ArrayList<>();
        expectedKeys.add("first");
        expectedKeys.add("second");
        expectedKeys.add("last");
        assertEquals(expectedKeys, controller.getTransmittedTelemetryKeys());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfRunnerIsNull() throws IOException   {
        BalloonController controller = BalloonController.createBalloonController("test/testKeys.json", this.properties);
        controller.run(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfNoTimeDataKeyIsSpecified() throws IOException   {
        BalloonController controller = BalloonController.createBalloonController("test/testKeys.json", this.properties);
        ControllerRunner runner = mock(ControllerRunner.class);
        when(runner.shouldKeepRunning()).thenReturn(true);
        controller.run(runner);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfNoPayloadNameKeyIsSpecified() throws IOException    {
        this.properties.setProperty("time.key", "time");
        BalloonController controller = BalloonController.createBalloonController("test/testKeys.json", this.properties);
        ControllerRunner runner = mock(ControllerRunner.class);
        when(runner.shouldKeepRunning()).thenReturn(true);
        controller.run(runner);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfNoSentenceIdKeyIsSpecified() throws IOException {
        this.properties.setProperty("time.key", "time");
        this.properties.setProperty("payloadName.key", "payload_name");
        BalloonController controller = BalloonController.createBalloonController("test/testKeys.json", this.properties);
        ControllerRunner runner = mock(ControllerRunner.class);
        when(runner.shouldKeepRunning()).thenReturn(true);
        controller.run(runner);
    }
    
    @Test
    public void testRunSucceedsIfValidPropertiesArePassed() throws IOException, SensorReadFailedException {
        this.properties.setProperty("time.key", "time");
        this.properties.setProperty("date.key", "date");
        this.properties.setProperty("payloadName.key", "payload_name");
        this.properties.setProperty("sentenceId.key", "sentence_id");
        this.properties.setProperty("altitude.key", "altitude");
        
        List<SensorController> sensors = new ArrayList<>();
        SensorController mockSensor = mock(SensorController.class);
        sensors.add(mockSensor);
        
        BalloonController controller = new BalloonController(null, null, sensors, new ArrayList<DataWriter>(), null, null, properties);
        
        ControllerRunner runner = mock(ControllerRunner.class);
        when(runner.shouldKeepRunning()).thenReturn(true, false);
        
        Map<String, Object> mockSensorData = new HashMap<>();
        mockSensorData.put("altitude", 0.1234);
        when(mockSensor.getCurrentData()).thenReturn(mockSensorData);
        
        controller.run(runner);
        
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("payload_name", "$$latex");
        expectedData.put("sentence_id", 0);
        expectedData.put("altitude", 0.1234);
        verify(runner).controllerFinishedRunLoop(argThat(fieldsEqualTo(expectedData)));   
    }
    
    // Custom argument matcher to verify our model data, without caring about the time, which we won't know exactly
    private ArgumentMatcher<Map> fieldsEqualTo(final Map<String, Object> expected) {

        return new ArgumentMatcher<Map>() {
            @Override
            public boolean matches(Object argument) {
                Map<String, Object> actual = (Map<String, Object>) argument;
                Double actualAltitude = (Double) actual.get("altitude");
                Double expectedAltitude = (Double) expected.get("altitude");
                double altitudeDiff = Math.abs(actualAltitude - expectedAltitude);
                return (actual.get("payload_name").equals(expected.get("payload_name"))
                        && actual.get("sentence_id").equals(expected.get("sentence_id"))
                        && altitudeDiff <= 0.000001);
            }
        };
    }
}
