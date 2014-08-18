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
public class GPSSensorController implements SensorController {

    private static final Logger logger = Logger.getLogger(GPSSensorController.class);
    private GPSSensor gps;
    private ArrayList<String> keys = new ArrayList<String>();
    
    public GPSSensorController(GPSSensor gps, String... keys) {
        for (String key : keys) {
            this.keys.add(key);
        }
        this.gps = gps;
    }

    @Override
    public HashMap<String, Object> getCurrentData() throws SensorReadFailedException {
        HashMap<String, Object> requestedData = new HashMap<>();
        HashMap<String, Object> allData = new HashMap<>();
        
        // Get and parse all data from all supported sentence types 
        for (String sentence : gps.getSupportedNmeaSentences()) {
            allData.putAll(NMEASentenceParser.parse(gps.getNmeaSentence(sentence)));
        }

        for (String key : keys) {
            if (allData.get(key) == null) {
                logger.error("Invalid key: " + key + " was given");
            } else {
                requestedData.put(key,allData.get(key));
            }
        }
        
        return requestedData;
    }
}