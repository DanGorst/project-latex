/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import project.latex.balloon.consumer.DataModelConsumer;
import project.latex.balloon.sensor.SensorController;
import project.latex.balloon.sensor.SensorReadFailedException;
import project.latex.balloon.writer.ChecksumGenerator;
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
    private ControllerRunner mockControllerRunner;
    private ChecksumGenerator mockChecksumGenerator;

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
        this.mockControllerRunner = mock(ControllerRunner.class);
        this.mockChecksumGenerator = mock(ChecksumGenerator.class);
    }

    private BalloonController createDefaultController() throws IOException {
        List<SensorController> sensors = new ArrayList<>();
        sensors.add(this.mockSensorController);
        List<DataModelConsumer> dataModelConsumers = new ArrayList<>();
        dataModelConsumers.add(this.mockDataModelConsumer);
        List<DataWriter> dataWriters = new ArrayList<>();
        
        BalloonController controller = new BalloonController();
        controller.setConverter(new DataModelConverter(mockChecksumGenerator));
        controller.setSensors(sensors);
        controller.setDataWriters(dataWriters);
        controller.setDataModelConsumers(dataModelConsumers);
        controller.setSentenceIdGenerator(mockSentenceIdGenerator);
        controller.setTimeKey("time");
        controller.setDateKey("date");
        controller.setPayloadNameKey("payload_name");
        controller.setSentenceIdKey("sentence_id");
        controller.setControllerRunner(mockControllerRunner);
        
        return controller;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfRunnerIsNull() throws IOException {
        BalloonController controller = createDefaultController();
        controller.setControllerRunner(null);
        controller.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfNoTimeDataKeyIsSpecified() throws IOException {
        BalloonController controller = createDefaultController();
        controller.setTimeKey(null);
        when(mockControllerRunner.shouldKeepRunning()).thenReturn(true, false);
        controller.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfNoPayloadNameKeyIsSpecified() throws IOException {
        BalloonController controller = createDefaultController();
        controller.setPayloadNameKey(null);
        when(mockControllerRunner.shouldKeepRunning()).thenReturn(true, false);
        controller.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunThrowsIfNoSentenceIdKeyIsSpecified() throws IOException {
        BalloonController controller = createDefaultController();
        controller.setSentenceIdKey(null);
        when(mockControllerRunner.shouldKeepRunning()).thenReturn(true, false);
        controller.run();
    }

    @Test
    public void testRunSucceedsIfValidPropertiesArePassed() throws IOException, SensorReadFailedException {
        BalloonController controller = createDefaultController();

        when(mockControllerRunner.shouldKeepRunning()).thenReturn(true, false);

        Map<String, Object> mockSensorData = new HashMap<>();
        mockSensorData.put("altitude", 0.1234);
        when(mockSensorController.getCurrentData()).thenReturn(mockSensorData);
        when(mockSentenceIdGenerator.generateId()).thenReturn("2");

        controller.run();

        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("payload_name", "$$latex");
        expectedData.put("sentence_id", "2");
        expectedData.put("altitude", 0.1234);
        verify(mockControllerRunner).controllerFinishedRunLoop(argThat(fieldsEqualTo(expectedData)));
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

        when(mockControllerRunner.shouldKeepRunning()).thenReturn(true, false);

        Map<String, Object> mockSensorData = new HashMap<>();
        mockSensorData.put("altitude", 0.1234);
        when(mockSensorController.getCurrentData()).thenReturn(mockSensorData);
        when(mockSentenceIdGenerator.generateId()).thenReturn("2");

        controller.run();

        verify(mockDataModelConsumer).consumeDataModel(anyMap());
    }
}
