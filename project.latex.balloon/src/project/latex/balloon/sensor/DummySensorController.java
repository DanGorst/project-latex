/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dan
 */
public class DummySensorController implements SensorController {
    
    private final String dataKey;
    
    public DummySensorController(String dataKey)    {
        this.dataKey = dataKey;
    }

    @Override
    public Map<String, Object> getCurrentData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put(this.dataKey, Math.random());
        return data;
    }
}
