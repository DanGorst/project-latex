/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import project.latex.balloon.consumer.DataModelConsumer;
import project.latex.balloon.consumer.TransistorSwitch;
import project.latex.balloon.consumer.TransistorSwitchController;
import project.latex.balloon.sensor.DummySensorController;
import project.latex.balloon.sensor.SensorController;
import project.latex.balloon.sensor.gps.GPSSensorController;
import project.latex.balloon.writer.DataModelConverter;
import project.latex.balloon.writer.FileDataWriter;
import project.latex.balloon.writer.HttpDataWriter;
import project.latex.balloon.writer.DataWriter;

/**
 *
 * @author Dan
 */
public class BalloonControllerFactoryTest {

    private Properties properties;
    private File testImagesDir;
    private File testDataDir;
    private SensorController mockSensorController;
    private SentenceIdGenerator mockSentenceIdGenerator;
    private TransistorSwitch transistorSwitch;
    private BalloonControllerFactory balloonControllerFactory;

    public BalloonControllerFactoryTest() {
    }

    @Before
    public void setUp() {
        this.properties = new Properties();
        String testImagesDirPath = "test/images";
        testImagesDir = new File(testImagesDirPath);
        testImagesDir.mkdir();

        testDataDir = new File("test/data");
        testDataDir.mkdir();

        this.properties.setProperty("cameraDir", testImagesDirPath);

        this.mockSensorController = mock(SensorController.class);
        this.mockSentenceIdGenerator = mock(SentenceIdGenerator.class);
        
        this.transistorSwitch = mock(TransistorSwitch.class);
        
        this.balloonControllerFactory = new BalloonControllerFactory(this.transistorSwitch);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createSensorControllers method, of class
     * BalloonControllerFactory.
     */
    @Test
    public void testCreateSensorControllers() {
        List<SensorController> result = balloonControllerFactory.createSensorControllers(properties);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof DummySensorController);
        assertTrue(result.get(1) instanceof GPSSensorController);
    }

    /**
     * On the machines we are likely to be testing on, we won't have a
     * transmitter connected. In that case, it should fail to create a
     * SerialDataWriter and should fall back to using a HTTP connector instead
     *
     * @throws java.io.IOException
     */
    @Test
    public void testCreateDataWriters() throws IOException {
        List<String> transmittedDataKeys = BalloonController.loadTransmittedDataKeys("test/testKeys.json");
        DataModelConverter converter = new DataModelConverter();
        List<DataWriter> result = balloonControllerFactory.createDataWriters(properties,
                transmittedDataKeys, converter, null);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof FileDataWriter);
        assertTrue(result.get(1) instanceof HttpDataWriter);

        // As a result of creating the default file writer, we also create a csv file for it.
        // We delete that here so as not to pollute the project after running tests
        FileDataWriter fileWriter = (FileDataWriter) result.get(0);
        fileWriter.closeAndDeleteFile();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateDataConsumersThrowsWhenNoArmingHeightSet() {
        properties.setProperty("switchingHeight", "200");
        properties.setProperty("altitude.key", "altitude");
        balloonControllerFactory.createDataModelConsumers(properties);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateDataConsumersThrowsWhenNoSwitchingHeightSet() {
        properties.setProperty("armingHeight", "200");
        properties.setProperty("altitude.key", "altitude");
        balloonControllerFactory.createDataModelConsumers(properties);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateDataConsumersThrowsWhenNoHeightKeySet() {
        properties.setProperty("switchingHeight", "200");
        properties.setProperty("armingHeight", "500");
        balloonControllerFactory.createDataModelConsumers(properties);
    }
    
    @Test
    public void testCreateDataConsumersSucceedsWhenAllPrerequisitesAreMet() {
        properties.setProperty("armingHeight", "200");
        properties.setProperty("switchingHeight", "500");
        properties.setProperty("altitude.key", "altitude");
        List<DataModelConsumer> dataModelConsumers = balloonControllerFactory.createDataModelConsumers(properties);
        assertNotNull(dataModelConsumers);
        assertEquals(1, dataModelConsumers.size());
    }

    /**
     * Test of createBalloonController method, of class BalloonController.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateBalloonControllerThrowsWithNullKeys() throws Exception {
        balloonControllerFactory.createBalloonController(null, new DataModelConverter(), this.properties,
                new ArrayList<SensorController>(), new ArrayList<DataWriter>(),
                new ArrayList<DataModelConsumer>(), testDataDir, mockSentenceIdGenerator);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBalloonControllerThrowsWithEmptyKeysArray() throws IOException {
        balloonControllerFactory.createBalloonController(new ArrayList<String>(), new DataModelConverter(),
                this.properties, new ArrayList<SensorController>(), new ArrayList<DataWriter>(),
                new ArrayList<DataModelConsumer>(), testDataDir, mockSentenceIdGenerator);
    }

    @Test
    public void testCreateBalloonControllerSucceedsWithValidKeysFilePath() throws IOException {
        List<String> transmittedDataKeys = BalloonController.loadTransmittedDataKeys("test/testKeys.json");
        List<SensorController> sensors = new ArrayList<>();
        sensors.add(this.mockSensorController);
        BalloonController controller = balloonControllerFactory.createBalloonController(transmittedDataKeys,
                new DataModelConverter(), this.properties, sensors,
                new ArrayList<DataWriter>(), new ArrayList<DataModelConsumer>(),
                testDataDir, mockSentenceIdGenerator);

        assertNotNull(controller);
        assertNotNull(controller.getCameraSensor());
        assertNotNull(controller.getCameraWriter());
        assertNotNull(controller.getConverter());
        assertNotNull(controller.getDataWriters());
        assertNotNull(controller.getSensors());
        assertNotNull(controller.getDataModelConsumers());
        assertEquals("$$latex", controller.getPayloadName());

        List<String> expectedKeys = new ArrayList<>();
        expectedKeys.add("first");
        expectedKeys.add("second");
        expectedKeys.add("last");
        assertEquals(expectedKeys, controller.getTransmittedTelemetryKeys());
    }
}
