/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

/**
 *
 * @author will
 */
public class SensorReadFailedException extends Exception {
    
    public SensorReadFailedException(String message) {
        super(message);
    }   
}
