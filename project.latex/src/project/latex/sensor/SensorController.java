/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.sensor;

import project.latex.SensorData;

/**
 *
 * @author Dan
 */
public interface SensorController {
    String getSensorName();
    
    SensorData getCurrentData();
}
