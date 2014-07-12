/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author Dan
 */
public class SensorData {
    private final String sensorName;
    private final Date date;
    private final Map<String, Object> data;
    
    public SensorData(String sensorName, Date date, Map<String, Object> data)   {
        this.sensorName = sensorName;
        this.date = date;
        this.data = data;
    }

    public String getSensorName() {
        return sensorName;
    }

    public Date getDate() {
        return date;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
