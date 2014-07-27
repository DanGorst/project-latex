/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.ground;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import project.latex.ground.reader.DataModelReader;
import project.latex.ground.reader.MockDataModelReader;
import project.latex.ground.writer.DataModelWriter;
import project.latex.ground.writer.FileDataModelWriter;
import project.latex.ground.writer.WebServiceDataModelWriter;

/**
 *
 * @author dgorst
 */
public class GroundStationController {
    
    private final BalloonDataModel balloonDataModel = new BalloonDataModel();
    
    private final List<DataModelWriter> dataWriters = new ArrayList<>();
    
    private final DataModelReader dataReader = new MockDataModelReader();
    
    private void initialise()   {
        this.dataWriters.add(new WebServiceDataModelWriter("http://localhost:8080/ProjectLatexWebService/"));
        this.dataWriters.add(new FileDataModelWriter());
    
        balloonDataModel.setHeight(0.0f);
        balloonDataModel.setLatitude(0.0f);
        balloonDataModel.setLongitude(0.0f);
        
        // Adds a console appender to the root appender. This means that any logging we do, including the file logging, is also 
        // logged on the console
        BasicConfigurator.configure();
    }
    
    private void updateDataModel(BalloonDataModel newData)  {
        this.balloonDataModel.setHeight(newData.getHeight());
        this.balloonDataModel.setLatitude(newData.getLatitude());
        this.balloonDataModel.setLongitude(newData.getLongitude());
    }
    
    private void run()  {
        Logger.getRootLogger().info("Project Latex Ground Station, version 0.1");
        
        while (true)    {
            updateDataModel(this.dataReader.readDataModel());
            
            for (DataModelWriter writer : this.dataWriters) {
                writer.writeDataModel(balloonDataModel);
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GroundStationController.class.getName()).log(Level.ERROR, null, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GroundStationController groundStation = new GroundStationController();
        groundStation.initialise();
        groundStation.run();
    }
    
}
