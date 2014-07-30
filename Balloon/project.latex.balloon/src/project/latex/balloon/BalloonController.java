/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import project.latex.SensorData;
import project.latex.balloon.sensor.AltimeterSensorController;
import project.latex.balloon.sensor.CameraController;
import project.latex.balloon.sensor.CameraSensorController;
import project.latex.balloon.sensor.DummySensorController;
import project.latex.balloon.sensor.GPSSensorController;
import project.latex.balloon.sensor.SensorController;
import project.latex.balloon.writer.CameraFileWriter;
import project.latex.balloon.writer.FileDataWriter;
import project.latex.balloon.writer.SensorFileLoggerService;
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
    
    private List<SensorController> sensors;
    private List<DataWriter> dataWriters;
    
    private GPSSensorController gpsController;
    private AltimeterSensorController altimeterController;
    
    private static final Logger logger = Logger.getLogger(BalloonController.class);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
        loadProperties();
        BalloonController balloonController = new BalloonController();
        balloonController.initialise();
        balloonController.run();
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
        
        this.sensors = new ArrayList<>();
        this.sensors.add(new DummySensorController());
        try {
            String imageDirectory = properties.getProperty("cameraDir");
            this.sensors.add(new CameraController(new File(imageDirectory)));
        } catch (IllegalArgumentException e)   {
            logger.error("Unable to create camera controller. No images will be detected from the camera.", e);
        }
        
        this.dataWriters = new ArrayList<>();
        this.dataWriters.add(new ConsoleDataWriter());
        
        // We create a new folder for each flight that the balloon makes. All of our sensor data for the 
        // flight is then put into that folder
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        Date date = new Date();
        String baseUrl = "data" + File.separator + "Flight starting - " + dateFormat.format(date);
        File dataFolder = new File(baseUrl);
        if (dataFolder.mkdirs()) {
            SensorFileLoggerService loggerService = new SensorFileLoggerService();

            // We create a different logger for each sensor. The file data writer will then lookup these loggers as needed
            for (SensorController sensor : this.sensors) {
                try {
                    loggerService.setLoggerForSensor(sensor.getSensorName(), baseUrl);
                } catch (DataWriteFailedException ex) {
                    logger.error("Unable to create logger for sensor.", ex);
                }
            }
            
            this.dataWriters.add(new FileDataWriter(loggerService));
        } else  {
            logger.info("Unable to create directory to contain sensor data logs");
        }
        
        try {
            this.dataWriters.add(new CameraFileWriter(dataFolder));
        } catch (IllegalArgumentException e)    {
            logger.error("Unable to create camera file writer. No images will be saved in the flight directory");
        }
        
        // TODO - Initialise the altimeter and GPS controllers here
    }
    
    static boolean shouldWriterHandleDataFromSensor(DataWriter writer, SensorController controller)    {
        if (controller instanceof CameraSensorController)   {
            return (writer instanceof CameraDataWriter);
        }
        else    {
            return !(writer instanceof CameraDataWriter);
        }
    }
    
    private void run()  {
        while (true)    {
            for (SensorController controller : this.sensors)    {
                SensorData currentSensorData = controller.getCurrentData();
                
                for (DataWriter dataWriter : this.dataWriters)  {
                    try {
                        if (shouldWriterHandleDataFromSensor(dataWriter, controller)) {
                            dataWriter.writeData(currentSensorData);
                        }
                    }
                    catch (DataWriteFailedException e)  {
                        logger.error(e);
                    }
                    // If we get some kind of unknown exception, let's catch it here rather than just crashing the app
                    catch (Exception e)   {
                        logger.error(e);
                    }
                }
            }
        }
    }
}
