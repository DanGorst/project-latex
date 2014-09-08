/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import com.pi4j.io.serial.SerialPortException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import project.latex.balloon.sensor.CameraController;
import project.latex.balloon.sensor.CameraSensorController;
import project.latex.balloon.sensor.DummySensorController;
import project.latex.balloon.sensor.SensorController;
import project.latex.balloon.sensor.gps.GPSSensor;
import project.latex.balloon.sensor.gps.GPSSensorController;
import project.latex.balloon.writer.CameraFileWriter;
import project.latex.balloon.writer.DataModelConverter;
import project.latex.balloon.writer.FileDataWriter;
import project.latex.balloon.writer.HttpDataWriter;
import project.latex.balloon.writer.SerialDataWriter;
import project.latex.writer.CameraDataWriter;
import project.latex.writer.DataWriter;

/**
 *
 * @author Dan
 */
public class BalloonControllerFactory {
    private static final Logger logger = Logger.getLogger(BalloonControllerFactory.class);

    static List<SensorController> createSensorControllers(Properties properties) {
        List<SensorController> sensors = new ArrayList<>();
        sensors.add(new DummySensorController(properties.getProperty("tempExternal.key")));

        GPSSensor ublox = new GPSSensor("GPGGA", "GPRMC");
        sensors.add(new GPSSensorController(ublox,
                properties.getProperty("time.key"),
                properties.getProperty("latitude.key"),
                properties.getProperty("longitude.key"),
                properties.getProperty("altitude.key"),
                properties.getProperty("heading.key"),
                properties.getProperty("speed.key"),
                properties.getProperty("date.key")));
        
        return sensors;
    }

    static List<DataWriter> createDataWriters(Properties properties,
            List<String> transmittedDataKeys,
            DataModelConverter converter,
            File dataFolder) {
        List<DataWriter> dataWriters = new ArrayList<>();
        dataWriters.add(new FileDataWriter(dataFolder, transmittedDataKeys, converter));

//        try {
//            dataWriters.add(new SerialDataWriter(transmittedDataKeys, converter));
//        } catch (SerialPortException ex) {
//            logger.error(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
//            logger.error("Using HTTP data writer instead");
//            dataWriters.add(createHttpDataWriter(properties, converter, transmittedDataKeys));
//        } catch (UnsatisfiedLinkError err) {
//            logger.error(err);
//            logger.error("Using HTTP data writer instead");
//            dataWriters.add(createHttpDataWriter(properties, converter, transmittedDataKeys));
//        }
        dataWriters.add(createHttpDataWriter(properties, converter, transmittedDataKeys));

        return dataWriters;
    }

    private static HttpDataWriter createHttpDataWriter(Properties properties, DataModelConverter converter, List<String> transmittedDataKeys) {
        String receiverUrl = properties.getProperty("receiver.url");
        return new HttpDataWriter(transmittedDataKeys, converter, receiverUrl);
    }
    
    public static BalloonController createBalloonController(Properties properties,
            List<String> transmittedDataKeys, File dataFolder) throws IOException {
        List<SensorController> sensors = createSensorControllers(properties);
        DataModelConverter converter = new DataModelConverter();
        List<DataWriter> dataWriters = createDataWriters(properties,
                transmittedDataKeys, converter, dataFolder);
        return createBalloonController(transmittedDataKeys,
                converter, properties, sensors, dataWriters, dataFolder);
    }

    static BalloonController createBalloonController(List<String> transmittedDataKeys,
            DataModelConverter converter,
            Properties properties,
            List<SensorController> sensors,
            List<DataWriter> dataWriters,
            File dataFolder) throws IOException {

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
        return new BalloonController(transmittedDataKeys, converter, sensors, dataWriters, cameraSensor, cameraWriter, properties);
    }
}
