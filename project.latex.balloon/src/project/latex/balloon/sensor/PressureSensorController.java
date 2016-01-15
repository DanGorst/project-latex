/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 * 
 * Class for interfacing with BMP085 atmospheric pressure sensor. The class calls a Python script
 * to get the temperature value from the sensor, as driver software for the sensor is
 * written in Python.
 * 
 */
public class PressureSensorController implements SensorController {

    private static final Logger logger = Logger.getLogger(PressureSensorController.class);
    private final String pressureKey;
     
    public PressureSensorController(String pressureKey) {
        this.pressureKey = pressureKey;
    }
    
    @Override
    public Map<String, Object> getCurrentData() throws SensorReadFailedException {
        logger.info(String.format("Getting %s sensor reading", pressureKey));
        
        Process pressureSensorScript;
        InputStream inputStream = null;
        try
        {
            // Run the encode script.
            pressureSensorScript = Runtime.getRuntime().exec("python scripts/pressureSensor.py");
            
            // Wait for script to terminate.
            pressureSensorScript.waitFor();
                         
            // Get the output from the script.
            inputStream = pressureSensorScript.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String output = bufferedReader.readLine();
            logger.info(String.format("%s sensor read output: %s", pressureKey, output)); 
            
            try {
                // Pressure in millibars.
                double pressureReading = Double.parseDouble(output);
                Map<String, Object> pressure = new HashMap();
                pressure.put(pressureKey, pressureReading);
                return pressure;
            } catch (NumberFormatException e) {
                throw new SensorReadFailedException(String.format("%s", output));
            }                                
        }
        catch (IOException | InterruptedException e) 
        {
            throw new SensorReadFailedException("Could not run pressureSensor.py");
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {}
        }
    }
}
