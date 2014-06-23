/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
        
        // TODO - Initialise the altimeter and GPS controllers here
    }
    
    private void run()  {
        while (true)    {
            for (SensorController controller : this.sensors)    {
                SensorData currentSensorData = controller.getCurrentData();
                
                for (DataWriter dataWriter : this.dataWriters)  {
                    dataWriter.writeData(currentSensorData);
                }
            }
        }
    }
}
