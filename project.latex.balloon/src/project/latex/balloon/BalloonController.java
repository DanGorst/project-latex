/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import project.latex.sensor.SensorController;
import project.latex.writer.DataWriter;
import java.util.ArrayList;
import java.util.List;
import project.latex.SensorData;
import project.latex.sensor.DummySensorController;
import project.latex.writer.ConsoleDataWriter;

/**
 *
 * @author Dan
 */
public class BalloonController {

    private List<SensorController> sensors;
    private List<DataWriter> dataWriters;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Project Latex Balloon Controller version 0.1");
        
        BalloonController balloonController = new BalloonController();
        balloonController.initialise();
        balloonController.run();
    }
    
    private void initialise()   {
        this.sensors = new ArrayList<>();
        this.sensors.add(new DummySensorController());
        
        this.dataWriters = new ArrayList<>();
        this.dataWriters.add(new ConsoleDataWriter());
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
