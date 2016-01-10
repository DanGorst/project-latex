/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import project.latex.balloon.ssdv.SerialSsdvDataWriter;

/**
 *
 * @author will
 * 
 * Class for interfacing with DHT11 temperature sensor. The class calls a Python script
 * to get the temperature value from the sensor, as driver software for the sensor is
 * written in Python.
 * 
 */
public class TemperatureSensorController implements SensorController {

    private static final Logger logger = Logger.getLogger(TemperatureSensorController.class);
    private final String temperatureKey;
    private final int pin;
    // Name used by Python script for the DHT11 sensor.
    private final String sensorType = "11";
    
    public TemperatureSensorController(String temperatureKey, int pin) {
        this.temperatureKey = temperatureKey;
        this.pin = pin;
    }
    
    @Override
    public Map<String, Object> getCurrentData() throws SensorReadFailedException {
        logger.info(String.format("Getting %s sensor reading", temperatureKey));
        
        Process temperatureSensorScript;
        try
        {
            // Run the encode script.
            temperatureSensorScript = Runtime.getRuntime().exec(
                    String.format("python scripts/temperatureSensor.py %s %d", sensorType, pin));
            
            // Wait for script to terminate.
            temperatureSensorScript.waitFor();
            
            // Get the output from the script.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(temperatureSensorScript.getInputStream()));
            String output = bufferedReader.readLine();
            logger.info(String.format("%s sensor read output: %s", temperatureKey, output)); 
            
            try {
                // Temperature in Celcius.
                double temperatureReading = Double.parseDouble(output);
                Map<String, Object> temperature = new HashMap();
                temperature.put(temperatureKey, temperatureReading);
                return temperature;
            } catch (NumberFormatException e) {
                throw new SensorReadFailedException(String.format("%s", output));
            }                                
        }
        catch (IOException | InterruptedException e) 
        {
            throw new SensorReadFailedException("Could not run temperatureSensor.py");
        }
    }
    
}
