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
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static project.latex.balloon.BalloonController.loadTransmittedDataKeys;
import project.latex.balloon.consumer.DataModelConsumer;
import project.latex.balloon.sensor.SensorController;
import project.latex.balloon.sensor.SensorReadFailedException;
import project.latex.balloon.writer.DataModelConverter;
import project.latex.balloon.writer.DataWriter;

/**
 *
 * @author dgorst
 */
public class BalloonControllerTest {

    private SensorController mockSensorController;
    private SentenceIdGenerator mockSentenceIdGenerator;
    private DataModelConsumer mockDataModelConsumer;

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
        this.mockSensorController = mock(SensorController.class);
        this.mockSentenceIdGenerator = mock(SentenceIdGenerator.class);
        this.mockDataModelConsumer = mock(DataModelConsumer.class);
    }

    private BalloonController createDefaultController() throws IOException {
        List<String> transmittedDataKeys = loadTransmittedDataKeys("test/testKeys.json");
        List<SensorController> sensors = new ArrayList<>();
        sensors.add(this.mockSensorController);
        List<DataModelConsumer> dataModelConsumers = new ArrayList<>();
        dataModelConsumers.add(this.mockDataModelConsumer);
        List<DataWriter> dataWriters = new ArrayList<>();
        
        BalloonController controller = new BalloonController();
        controller.setTransmittedTelemetryKeys(transmittedDataKeys);
        controller.setConverter(new DataModelConverter());
        controller.setSensors(sensors);
        controller.setDataWriters(dataWriters);
        controller.setDataModelConsumers(dataModelConsumers);
        controller.setSentenceIdGenerator(mockSentenceIdGenerator);
        controller.setTimeKey("time");
        controller.setDateKey("date");
        controller.setPayloadNameKey("payload_name");
        controller.setSentenceIdKey("sentence_id");
        
        return controller;
    }

    /**
     * Test of loadTransmittedDataKeys method, of class BalloonController.
     *
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
    public void testLoadTransmittedDataKeysSucceedsWithValidFile() throws IOException {
        List<String> actual = BalloonController.loadTransmittedDataKeys("test/testKeys.json");
        List<String> expected = new ArrayList<>();
        expected.add("first");
        expected.add("second");
        expected.add("last");
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfRunnerIsNull() throws IOException {
        BalloonController controller = createDefaultController();
        controller.run(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfNoTimeDataKeyIsSpecified() throws IOException {
        BalloonController controller = createDefaultController();
        controller.setTimeKey(null);
        ControllerRunner runner = mock(ControllerRunner.class);
        when(runner.shouldKeepRunning()).thenReturn(true, false);
        controller.run(runner);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfNoPayloadNameKeyIsSpecified() throws IOException {
        BalloonController controller = createDefaultController();
        controller.setPayloadNameKey(null);
        ControllerRunner runner = mock(ControllerRunner.class);
        when(runner.shouldKeepRunning()).thenReturn(true, false);
        controller.run(runner);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfNoSentenceIdKeyIsSpecified() throws IOException {
        BalloonController controller = createDefaultController();
        controller.setSentenceIdKey(null);
        ControllerRunner runner = mock(ControllerRunner.class);
        when(runner.shouldKeepRunning()).thenReturn(true, false);
        controller.run(runner);
    }

    @Test
    public void testRunSucceedsIfValidPropertiesArePassed() throws IOException, SensorReadFailedException {
        BalloonController controller = createDefaultController();

        ControllerRunner runner = mock(ControllerRunner.class);
        when(runner.shouldKeepRunning()).thenReturn(true, false);

        Map<String, Object> mockSensorData = new HashMap<>();
        mockSensorData.put("altitude", 0.1234);
        when(mockSensorController.getCurrentData()).thenReturn(mockSensorData);
        when(mockSentenceIdGenerator.generateId()).thenReturn("2");

        controller.run(runner);

        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("payload_name", "$$latex");
        expectedData.put("sentence_id", "2");
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

    @Test
    public void testThatDataModelConsumerIsCalledAfterModelIsPopulated() throws IOException, SensorReadFailedException {
        BalloonController controller = createDefaultController();

        ControllerRunner runner = mock(ControllerRunner.class);
        when(runner.shouldKeepRunning()).thenReturn(true, false);

        Map<String, Object> mockSensorData = new HashMap<>();
        mockSensorData.put("altitude", 0.1234);
        when(mockSensorController.getCurrentData()).thenReturn(mockSensorData);
        when(mockSentenceIdGenerator.generateId()).thenReturn("2");

        controller.run(runner);

        verify(mockDataModelConsumer).consumeDataModel(anyMap());
    }
}
