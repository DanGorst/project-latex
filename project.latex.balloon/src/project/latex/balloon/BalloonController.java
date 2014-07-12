/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import java.io.File;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import project.latex.SensorData;
import project.latex.balloon.sensor.AltimeterSensorController;
import project.latex.balloon.sensor.DummySensorController;
import project.latex.balloon.sensor.GPSSensorController;
import project.latex.balloon.sensor.SensorController;
import project.latex.writer.ConsoleDataWriter;
import project.latex.writer.DataWriter;
import project.latex.balloon.writer.FileDataWriter;
import project.latex.balloon.writer.SensorFileLoggerService;
import project.latex.writer.DataWriteFailedException;

/**
 *
 * @author Dan
 */
public class BalloonController {

    private List<SensorController> sensors;
    private List<DataWriter> dataWriters;
    
    private GPSSensorController gpsController;
    private AltimeterSensorController altimeterController;
    
    private static final Logger logger = Logger.getLogger(BalloonController.class);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        BalloonController balloonController = new BalloonController();
        balloonController.initialise();
        balloonController.run();
    }
    
    private void initialise()   {
        ConsoleAppender ca = new ConsoleAppender();
        ca.setWriter(new OutputStreamWriter(System.out));
        ca.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
        logger.addAppender(ca);
        logger.info("Project Latex Balloon Controller, version 0.1");
        
        this.sensors = new ArrayList<>();
        this.sensors.add(new DummySensorController());
        
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
                loggerService.setLoggerForSensor(sensor.getSensorName(), baseUrl);
            }
            
            this.dataWriters.add(new FileDataWriter(loggerService));
        } else  {
            logger.info("Unable to create directory to contain sensor data logs");
        }
        
        // TODO - Initialise the altimeter and GPS controllers here
    }
    
    private void run()  {
        while (true)    {
            for (SensorController controller : this.sensors)    {
                SensorData currentSensorData = controller.getCurrentData();
                
                for (DataWriter dataWriter : this.dataWriters)  {
                    try {
                        dataWriter.writeData(currentSensorData);
                    }
                    catch (DataWriteFailedException e)  {
                        logger.error(e);
                    }
                }
            }
        }
    }
}
