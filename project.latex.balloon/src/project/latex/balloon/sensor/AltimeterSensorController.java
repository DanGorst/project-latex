/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

import project.latex.balloon.sensor.gps.SensorReadFailedException;

/**
 *
 * @author dgorst
 */
public interface AltimeterSensorController {
    double getHeight() throws SensorReadFailedException;
}
