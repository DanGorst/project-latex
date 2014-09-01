/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import project.latex.balloon.sensor.SensorReadFailedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.log4j.Logger;
import project.latex.balloon.sensor.SensorController;

/**
 *
 * @author will
 */
public class GPSSensorController implements SensorController {

    private static final Logger logger = Logger.getLogger(GPSSensorController.class);
    private final GPSSensor gps;
    private final ArrayList<String> keys = new ArrayList<>();
    
    public GPSSensorController(GPSSensor gps, String... keys) {
        this.keys.addAll(Arrays.asList(keys));
        this.gps = gps;
    }

    @Override
    public HashMap<String, Object> getCurrentData() throws SensorReadFailedException {
        HashMap<String, Object> requestedData = new HashMap<>();
        HashMap<String, Object> allData = new HashMap<>();
        
        // Get and parse all data from all supported sentence types. 
        for (String sentence : gps.getSupportedNmeaSentences()) {
            allData.putAll(NMEASentenceParser.parse(gps.getNmeaSentence(sentence)));
        }
        
        // Match this controllers keys to the keys in allData and collect together 
        // the data corrsponding to matching keys.
        for (String key : keys) {
            if (allData.get(key) == null) {
                logger.error("Invalid key: " + key);
            } else {
                requestedData.put(key,allData.get(key));
            }
        }
        
        return requestedData;
    }
}