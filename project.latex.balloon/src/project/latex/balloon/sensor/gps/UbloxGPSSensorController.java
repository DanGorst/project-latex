/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

import java.util.HashMap;
import org.apache.log4j.Logger;
import project.latex.balloon.sensor.AltimeterSensorController;
import project.latex.balloon.sensor.GPSSensorController;

/**
 *
 * @author will
 */
public class UbloxGPSSensorController implements GPSSensorController, AltimeterSensorController {

    private static final Logger logger = Logger.getLogger(UbloxGPSSensorController.class);
    private UbloxGPSSensor gps;
    
    private String timeKey;
    private String dateKey;
    private String latitudeKey;
    private String longitudeKey;
    private String altitudeKey;
    private String speedKey;
    
    private String time;
    private String date;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speedInMPH;


    public UbloxGPSSensorController(String timeKey, String latitudeKey, String longitudeKey, String altitudeKey, String speedKey) {
        this.timeKey = timeKey;
        this.latitudeKey = latitudeKey;
        this.longitudeKey = longitudeKey;
        this.altitudeKey = altitudeKey;
        this.speedKey = speedKey;
        gps = new UbloxGPSSensor();
    }

    @Override
    public HashMap<String, Object> getCurrentData() throws SensorReadFailedException {
        HashMap<String, Object> data = new HashMap<>();

        updateSensorValues();
        data.put(latitudeKey, this.latitude);
        data.put(longitudeKey, this.longitude);
        data.put(altitudeKey, this.altitude);
        data.put(timeKey, this.time);
        data.put(speedKey, this.speedInMPH);

        return data;
    }

    private void updateSensorValues() throws SensorReadFailedException {
        HashMap<String, Object> GPGGAData = new HashMap<>();
        HashMap<String, Object> GPRMCData = new HashMap<>();

        // Get and parse a GPGGA sentence containing time, latitude, longitude, altitude.
        GPGGAData = NMEASentenceParser.parse(gps.getNMEASentence("GPGGA"));
        // Get and parse a GPRMC sentence containing date and speed.
        GPRMCData = NMEASentenceParser.parse(gps.getNMEASentence("GPRMC"));

        this.time = (String) GPGGAData.get("Time");
        this.latitude = (Double) GPGGAData.get("Latitude");
        this.longitude = (Double) GPGGAData.get("Longitude");
        this.altitude = (Double) GPGGAData.get("Altitude");
        this.date = (String) GPRMCData.get("Date");
        this.speedInMPH = (double) GPRMCData.get("Speed");
    }

    @Override
    public double getLatitude() throws SensorReadFailedException {
        updateSensorValues();
        return latitude;
    }

    @Override
    public double getLongitude() throws SensorReadFailedException {
        updateSensorValues();
        return longitude;
    }

    @Override
    public double getHeight() throws SensorReadFailedException {
        updateSensorValues();
        return altitude;
    }
}
