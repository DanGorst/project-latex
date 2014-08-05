/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import project.latex.balloon.sensor.CameraController;
import project.latex.balloon.sensor.CameraSensorController;
import project.latex.balloon.sensor.DummySensorController;
import project.latex.balloon.sensor.SensorController;
import project.latex.balloon.writer.CameraFileWriter;
import project.latex.balloon.writer.DataModelConverter;
import project.latex.balloon.writer.FileDataWriter;
import project.latex.balloon.writer.HttpDataWriter;
import project.latex.writer.CameraDataWriter;
import project.latex.writer.ConsoleDataWriter;
import project.latex.writer.DataWriteFailedException;
import project.latex.writer.DataWriter;

/**
 *
 * @author Dan
 */
public class BalloonController {

    private static final Logger logger = Logger.getLogger(BalloonController.class);
    
    private final List<String> transmittedTelemetryKeys;
    private final DataModelConverter converter;
    private final String payloadName = "$$latex";
    
    // Sensors to determine the current state of the balloon
    private final List<SensorController> sensors;
    private final List<DataWriter> dataWriters;
    
    // Camera
    private final CameraSensorController cameraSensor;
    private final CameraDataWriter cameraWriter;
    
    private final Properties properties;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
        try {
            Properties properties = loadProperties("config.properties");
            BalloonController balloonController = createBalloonController("../../telemetryKeys.json", properties);
            balloonController.run(new DefaultControllerRunner());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
    
    private static Properties loadProperties(String propertiesFilePath) throws IOException {
        Properties properties = new Properties();
        InputStream input = new FileInputStream(propertiesFilePath);
        properties.load(input);
        return properties;
    }
    
    static List<String> loadTransmittedDataKeys(String filePath) throws IOException   {
        if (filePath == null)   {
            throw new IllegalArgumentException("Cannot load keys from null file");
        }
        
        JsonReader reader = null;
        try {
            List<String> dataKeys = new ArrayList<>();
            reader = new JsonReader(new FileReader(filePath));
            reader.beginObject();
            while (reader.hasNext())    {
                String name = reader.nextName();
                reader.beginArray();
                while (reader.hasNext())    {
                    dataKeys.add(reader.nextString());
                }
                reader.endArray();
            }
            reader.endObject();
            reader.close();
            
            return dataKeys;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    static BalloonController createBalloonController(String telemetryKeysFilePath, Properties properties) throws IOException   {
        ConsoleAppender ca = new ConsoleAppender();
        ca.setWriter(new OutputStreamWriter(System.out));
        ca.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
        logger.addAppender(ca);
        logger.info("Project Latex Balloon Controller, version 0.1");
        
        List<String> transmittedDataKeys = loadTransmittedDataKeys(telemetryKeysFilePath);
        DataModelConverter converter = new DataModelConverter();
        
        // Initialise our sensors and data writers
        List<SensorController> sensors = new ArrayList<>();
        sensors.add(new DummySensorController(properties.getProperty("altitude.key")));
        
        List<DataWriter> dataWriters = new ArrayList<>();
        dataWriters.add(new ConsoleDataWriter());
        
        // We create a new folder for each flight that the balloon makes. All of our sensor data for the 
        // flight is then put into that folder
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        Date date = new Date();
        String baseUrl = "data" + File.separator + "Flight starting - " + dateFormat.format(date);
        File dataFolder = new File(baseUrl);
        if (dataFolder.mkdirs()) {
            dataWriters.add(new FileDataWriter(baseUrl, transmittedDataKeys, converter));
        } else  {
            logger.info("Unable to create directory to contain sensor data logs");
        }
        // Until we have radio comms working, we use http instead
        String receiverUrl = properties.getProperty("receiver.url");
        dataWriters.add(new HttpDataWriter(transmittedDataKeys, converter, receiverUrl));
        
        // Now initialise our camera systems
        CameraSensorController cameraSensor = null;
        try {
            String imageDirectory = properties.getProperty("cameraDir");
            cameraSensor = new CameraController(new File(imageDirectory));   
        } catch (IllegalArgumentException e)   {
            logger.error("Unable to create camera controller. No images will be detected from the camera.", e);
        }
        
        CameraDataWriter cameraWriter = null;
        try {
            cameraWriter = new CameraFileWriter(dataFolder);
        } catch (IllegalArgumentException e)    {
            logger.error("Unable to create camera file writer. No images will be saved in the flight directory");
        }
        return new BalloonController(transmittedDataKeys, converter, sensors, dataWriters, cameraSensor, cameraWriter, properties);
    }
    
    public BalloonController(List<String> transmittedTelemetryKeys, 
            DataModelConverter converter,
            List<SensorController> sensors,
            List<DataWriter> dataWriters,
            CameraSensorController cameraSensor,
            CameraDataWriter cameraWriter,
            Properties properties)  {
        this.transmittedTelemetryKeys = transmittedTelemetryKeys;
        this.converter = converter;
        this.sensors = sensors;
        this.dataWriters = dataWriters;
        this.cameraSensor = cameraSensor;
        this.cameraWriter = cameraWriter;
        this.properties = properties;
    }

    public List<String> getTransmittedTelemetryKeys() {
        return transmittedTelemetryKeys;
    }

    public DataModelConverter getConverter() {
        return converter;
    }

    public String getPayloadName() {
        return payloadName;
    }

    public List<SensorController> getSensors() {
        return sensors;
    }

    public List<DataWriter> getDataWriters() {
        return dataWriters;
    }

    public CameraSensorController getCameraSensor() {
        return cameraSensor;
    }

    public CameraDataWriter getCameraWriter() {
        return cameraWriter;
    }
    
    void run(ControllerRunner runner) {
        if (runner == null) {
            throw new IllegalArgumentException("Cannot run with null ControllerRunner");
        }
        
        String timeKey = properties.getProperty("time.key");
        if (timeKey == null)    {
            throw new IllegalArgumentException("Null time key specified");
        }
        String payloadNameKey = properties.getProperty("payloadName.key");
        if (payloadNameKey == null) {
            throw new IllegalArgumentException("Null payload name key specified");
        }
        String sentenceIdKey = properties.getProperty("sentenceId.key");
        if (sentenceIdKey == null)  {
            throw new IllegalArgumentException("Null sentence id key specified");
        }
        
        while (runner.shouldKeepRunning()) {
            // Build up a model of the current balloon state from the sensors
            Map<String, Object> data = new HashMap<>();
            
            // For now we put the date into the same format as the Icarus test data, 
            // as this means we don't need to change the receiver to be able to handle both
            // sets of data
            DateFormat format = new SimpleDateFormat("HH:mm:ss");
            data.put(timeKey, format.format(new Date()));
            
            data.put(payloadNameKey, this.payloadName);
            data.put(sentenceIdKey, 0);
            for (SensorController controller : this.sensors) {
                Map<String, Object> sensorData = controller.getCurrentData();
                for (String key : sensorData.keySet()) {
                    data.put(key, sensorData.get(key));
                }
            }

            // Write the model
            for (DataWriter dataWriter : this.dataWriters) {
                try {
                    dataWriter.writeData(data);
                } // If we get some kind of exception, let's catch it here rather than just crashing the app
                catch (Exception e) {
                    logger.error(e);
                }
            }

            // Find any new camera images and write them out
            if (this.cameraSensor != null) {
                List<String> imageFiles = this.cameraSensor.getImageFileNames();
                try {
                    this.cameraWriter.writeImageFiles(imageFiles);
                } catch (DataWriteFailedException ex) {
                    logger.error("Failed to write image files", ex);
                }
            }
            
            runner.controllerFinishedRunLoop(data);
        }
    }
}
