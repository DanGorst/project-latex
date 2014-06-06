/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.ground;

import java.util.HashMap;
import java.util.Map;
import project.latex.SensorData;

/**
 *
 * @author dgorst
 */
public class BalloonDataModel {
    private float height = 0;
    private float latitude = 0;
    private float longitude = 0;
    private Map<String, SensorData> currentSensorData;
    
    public BalloonDataModel()   {
        this.currentSensorData = new HashMap<>();
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    
    public void setSensorData(String sensorName, SensorData sensorData)    {
        this.currentSensorData.put(sensorName, sensorData);
    }
}
