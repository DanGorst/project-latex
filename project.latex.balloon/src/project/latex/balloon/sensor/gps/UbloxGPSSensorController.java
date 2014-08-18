/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import project.latex.balloon.sensor.SensorController;

/**
 *
 * @author will
 */
public class UbloxGPSSensorController implements SensorController {

    private static final Logger logger = Logger.getLogger(UbloxGPSSensorController.class);
    private UbloxGPSSensor gps;
    private ArrayList<String> keys = new ArrayList<String>();
    
    public UbloxGPSSensorController(UbloxGPSSensor gps, String... keys) {
        for (String key : keys) {
            this.keys.add(key);
        }
        this.gps = gps;
    }

    @Override
    public HashMap<String, Object> getCurrentData() throws SensorReadFailedException {
        HashMap<String, Object> requestedData = new HashMap<>();
        HashMap<String, Object> allData = new HashMap<>();

        // Get and parse a GPGGA sentence containing time, latitude, longitude, altitude.
        allData.putAll(NMEASentenceParser.parse(gps.getNMEASentence("GPGGA")));
        // Get and parse a GPRMC sentence containing date and speed.
        allData.putAll(NMEASentenceParser.parse(gps.getNMEASentence("GPRMC")));

        for (String key : keys) {
            requestedData.put(key,allData.get(key));
        }

        return requestedData;
    }
}