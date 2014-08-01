/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private static final Properties properties = new Properties();
    private static final Logger logger = Logger.getLogger(BalloonController.class);
    
    private final List<String> transmittedTelemetryKeys = new ArrayList<>();
    private DataModelConverter converter;
    private final String payloadName = "latex";
    
    // Sensors to determine the current state of the balloon
    private List<SensorController> sensors;
    private List<DataWriter> dataWriters;
    
    // Camera
    CameraSensorController cameraSensor;
    CameraDataWriter cameraWriter;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
        loadProperties();
        BalloonController balloonController = new BalloonController();
        balloonController.initialise();
        balloonController.run();
    }
    
    private void loadTransmittedDataKeys()   {
        try {
            JsonReader reader = new JsonReader(new FileReader("../../telemetryKeys.json"));
            reader.beginObject();
            while (reader.hasNext())    {
                String name = reader.nextName();
                reader.beginArray();
                while (reader.hasNext())    {
                    this.transmittedTelemetryKeys.add(reader.nextString());
                }
                reader.endArray();
            }
            reader.endObject();
            reader.close();
        } catch (FileNotFoundException ex) {
            logger.error("Unable to find telemetry keys file", ex);
        } catch (IOException ex) {
            logger.error("Unable to read JSON", ex);
        }
    }
    
    private static void loadProperties()    {
        try {
            InputStream input = new FileInputStream("config.properties");
            properties.load(input);
        } catch (FileNotFoundException ex) {
            logger.error("Unable to find properties file", ex);
        } catch (IOException ex)    {
            logger.error("Unable to load properties", ex);
        }
    }
    
    private void initialise()   {
        ConsoleAppender ca = new ConsoleAppender();
        ca.setWriter(new OutputStreamWriter(System.out));
        ca.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
        logger.addAppender(ca);
        logger.info("Project Latex Balloon Controller, version 0.1");
        
        loadTransmittedDataKeys();
        converter = new DataModelConverter();
        
        // Initialise our sensors and data writers
        this.sensors = new ArrayList<>();
        this.sensors.add(new DummySensorController(properties.getProperty("altitude.key")));
        
        this.dataWriters = new ArrayList<>();
        this.dataWriters.add(new ConsoleDataWriter());
        
        // We create a new folder for each flight that the balloon makes. All of our sensor data for the 
        // flight is then put into that folder
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        Date date = new Date();
        String baseUrl = "data" + File.separator + "Flight starting - " + dateFormat.format(date);
        File dataFolder = new File(baseUrl);
        if (dataFolder.mkdirs()) {
            this.dataWriters.add(new FileDataWriter(baseUrl, transmittedTelemetryKeys, converter));
        } else  {
            logger.info("Unable to create directory to contain sensor data logs");
        }
        // Until we have radio comms working, we use http instead
        String receiverUrl = properties.getProperty("receiver.url");
        this.dataWriters.add(new HttpDataWriter(transmittedTelemetryKeys, converter, receiverUrl));
        
        // Now initialise our camera systems
        try {
            String imageDirectory = properties.getProperty("cameraDir");
            this.cameraSensor = new CameraController(new File(imageDirectory));
        } catch (IllegalArgumentException e)   {
            logger.error("Unable to create camera controller. No images will be detected from the camera.", e);
        }
        
        try {
            this.cameraWriter = new CameraFileWriter(dataFolder);
        } catch (IllegalArgumentException e)    {
            logger.error("Unable to create camera file writer. No images will be saved in the flight directory");
        }
    }

    
    private void run() {
        while (true) {
            // Build up a model of the current balloon state from the sensors
            Map<String, Object> data = new HashMap<>();
            
            // For now we put the date into the same format as the Icarus test data, 
            // as this means we don't need to change the receiver to be able to handle both
            // sets of data
            DateFormat format = new SimpleDateFormat("HH:mm:ss");
            data.put(properties.getProperty("time.key"), format.format(new Date()));
            
            data.put(properties.getProperty("payloadName.key"), "$$latex");
            data.put(properties.getProperty("sentenceId.key"), 0);
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
            List<String> imageFiles = this.cameraSensor.getImageFileNames();
            try {
                this.cameraWriter.writeImageFiles(imageFiles);
            } catch (DataWriteFailedException ex) {
                logger.error("Failed to write image files", ex);
            }
            
            try {
                // Sleep this thread so we're not loading the CPU too much from this process
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                logger.error(ex);
            }
        }
    }
}
