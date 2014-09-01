/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

import java.util.Map;

/**
 *
 * @author Dan
 */
public interface SensorController {
    Map<String, Object> getCurrentData() throws SensorReadFailedException;
}
