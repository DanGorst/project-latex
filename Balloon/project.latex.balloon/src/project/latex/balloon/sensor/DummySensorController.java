/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

import java.util.Date;
import java.util.HashMap;
import project.latex.SensorData;

/**
 *
 * @author Dan
 */
public class DummySensorController implements SensorController {
    
    private final String sensorName = "Dummy Sensor";

    @Override
    public SensorData getCurrentData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Value", Math.random());
        return new SensorData(this.sensorName, new Date(), data);
    }

    @Override
    public String getSensorName() {
        return this.sensorName;
    }
    
}
