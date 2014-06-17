/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.ground;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import project.latex.ground.writer.DataModelWriter;
import project.latex.ground.writer.WebServiceDataModelWriter;

/**
 *
 * @author dgorst
 */
public class GroundStationController {
    
    private final BalloonDataModel balloonDataModel = new BalloonDataModel();
    
    private final List<DataModelWriter> dataWriters = new ArrayList<>();
    
    private void initialise()   {
        this.dataWriters.add(new WebServiceDataModelWriter("http://localhost:8080/ProjectLatexWebService/"));
    
        balloonDataModel.setHeight(0.0f);
        balloonDataModel.setLatitude(0.0f);
        balloonDataModel.setLongitude(0.0f);
    }
    
    private void run()  {
        System.out.println("Project Latex Ground Station, version 0.1");
        
        while (true)    {
            // TODO: temp test code - update the balloon height
            this.balloonDataModel.setHeight(this.balloonDataModel.getHeight() + 5);
            
            for (DataModelWriter writer : this.dataWriters) {
                writer.writeDataModel(balloonDataModel);
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GroundStationController.class.getName()).log(Level.SEVERE, null, ex);
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
