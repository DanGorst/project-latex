/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.ground;

/**
 *
 * @author dgorst
 */
public class GroundStationController {
    
    private final BalloonDataModel balloonDataModel = new BalloonDataModel();
    
    private void run()  {
        System.out.println("Project Latex Ground Station, version 0.1");
        
        while (true)    {
            // TODO - Do stuff with the balloon data model
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GroundStationController groundStation = new GroundStationController();
        groundStation.run();
    }
    
}