/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.serial.SerialPortException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import project.latex.balloon.consumer.DataModelConsumer;
import project.latex.balloon.consumer.TransistorSwitch;
import project.latex.balloon.consumer.TransistorSwitchController;
import project.latex.balloon.sensor.CameraController;
import project.latex.balloon.sensor.CameraSensorController;
import project.latex.balloon.sensor.DummySensorController;
import project.latex.balloon.sensor.SensorController;
import project.latex.balloon.sensor.gps.GPSSensorController;
import project.latex.balloon.sensor.gps.UBloxGPSSensor;
import project.latex.balloon.writer.CameraFileWriter;
import project.latex.balloon.writer.DataModelConverter;
import project.latex.balloon.writer.FileDataWriter;
import project.latex.balloon.writer.HttpDataWriter;
import project.latex.balloon.writer.SerialDataWriter;
import project.latex.balloon.writer.CameraDataWriter;
import project.latex.balloon.writer.DataWriter;

/**
 *
 * @author Dan
 */
public class BalloonControllerFactory {

    private static final Logger logger = Logger.getLogger(BalloonControllerFactory.class);
    
    private final TransistorSwitch transistorSwitch;
    
    public BalloonControllerFactory()   {
        transistorSwitch = new TransistorSwitch(RaspiPin.GPIO_01);
    }
    
    public BalloonControllerFactory(TransistorSwitch transistorSwitch) {
        this.transistorSwitch = transistorSwitch;
    }

    List<SensorController> createSensorControllers(Properties properties) {
        List<SensorController> sensors = new ArrayList<>();
        sensors.add(new DummySensorController(properties.getProperty("tempExternal.key")));

        UBloxGPSSensor ublox = new UBloxGPSSensor();
        sensors.add(new GPSSensorController(ublox,
                properties.getProperty("time.key"),
                properties.getProperty("latitude.key"),
                properties.getProperty("longitude.key"),
                properties.getProperty("altitude.key"),
                properties.getProperty("heading.key"),
                properties.getProperty("speed.key")));

        return sensors;
    }
    
    List<DataModelConsumer> createDataModelConsumers(Properties properties) {
        List<DataModelConsumer> dataModelConsumers = new ArrayList<>();
        String armingHeightString = properties.getProperty("armingHeight");
        if (armingHeightString == null) {
            throw new IllegalArgumentException("No arming height specified");
        }
        Double armingHeight = Double.valueOf(armingHeightString);
        
        String switchingHeightString = properties.getProperty("switchingHeight");
        if (switchingHeightString == null) {
            throw new IllegalArgumentException("No switching height specified");
        }
        Double switchingHeight = Double.valueOf(switchingHeightString);
        
        String heightKey = properties.getProperty("altitude.key");
        if (heightKey == null) {
            throw new IllegalArgumentException("Null altitude id key specified");
        }
        
        dataModelConsumers.add(new TransistorSwitchController(transistorSwitch, armingHeight, 
                switchingHeight, heightKey));
        return dataModelConsumers;
    }

    List<DataWriter> createDataWriters(Properties properties,
            List<String> transmittedDataKeys,
            DataModelConverter converter,
            File dataFolder) {
        List<DataWriter> dataWriters = new ArrayList<>();
        dataWriters.add(new FileDataWriter(dataFolder, transmittedDataKeys, converter));

        try {
            dataWriters.add(new SerialDataWriter(transmittedDataKeys, converter));
        } catch (SerialPortException ex) {
            logger.error(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            logger.error("Using HTTP data writer instead");
            dataWriters.add(createHttpDataWriter(properties, converter, transmittedDataKeys));
        } catch (UnsatisfiedLinkError err) {
            logger.error(err);
            logger.error("Using HTTP data writer instead");
            dataWriters.add(createHttpDataWriter(properties, converter, transmittedDataKeys));
        }

        return dataWriters;
    }

    private HttpDataWriter createHttpDataWriter(Properties properties, DataModelConverter converter, List<String> transmittedDataKeys) {
        String receiverUrl = properties.getProperty("receiver.url");
        return new HttpDataWriter(transmittedDataKeys, converter, receiverUrl);
    }

    public BalloonController createBalloonController(Properties properties,
            List<String> transmittedDataKeys, File dataFolder) throws IOException {
        List<SensorController> sensors = createSensorControllers(properties);
        
        DataModelConverter converter = new DataModelConverter();
        
        List<DataWriter> dataWriters = createDataWriters(properties,
                transmittedDataKeys, converter, dataFolder);
        
        List<DataModelConsumer> dataModelConsumers = createDataModelConsumers(properties);
        
        return createBalloonController(transmittedDataKeys,
                converter, properties, sensors, dataWriters, dataModelConsumers, 
                dataFolder, new UUIDSentenceIDGenerator());
    }

    BalloonController createBalloonController(List<String> transmittedDataKeys,
            DataModelConverter converter,
            Properties properties,
            List<SensorController> sensors,
            List<DataWriter> dataWriters,
            List<DataModelConsumer> dataModelConsumers,
            File dataFolder,
            SentenceIdGenerator sentenceIdGenerator) throws IOException {

        if (transmittedDataKeys == null || transmittedDataKeys.isEmpty()) {
            throw new IllegalArgumentException("Cannot transmit data with no keys");
        }

        // Now initialise our camera systems
        CameraSensorController cameraSensor = null;
        try {
            String imageDirectory = properties.getProperty("cameraDir");
            cameraSensor = new CameraController(new File(imageDirectory));
        } catch (IllegalArgumentException e) {
            logger.error("Unable to create camera controller. No images will be detected from the camera.", e);
        }

        CameraDataWriter cameraWriter = null;
        try {
            cameraWriter = new CameraFileWriter(dataFolder);
        } catch (IllegalArgumentException e) {
            logger.error("Unable to create camera file writer. No images will be saved in the flight directory");
        }

        return new BalloonController(transmittedDataKeys, converter, sensors,
                dataWriters, dataModelConsumers, cameraSensor, cameraWriter, properties,
                sentenceIdGenerator);
    }
}
