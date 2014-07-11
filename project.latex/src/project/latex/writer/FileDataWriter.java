/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.writer;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import project.latex.SensorData;

/**
 *
 * @author dgorst
 */
public class FileDataWriter implements DataWriter {
    private final String baseUrl;
    private final Gson gson = new Gson();
    private final SensorLoggerService loggerService;
    
    public FileDataWriter(String baseUrl, SensorLoggerService loggerService)   {
        this.baseUrl = baseUrl;
        this.loggerService = loggerService;
    }
    
    @Override
    public void writeData(SensorData data) {
        Logger sensorLogger = this.loggerService.getLoggerForSensor(data.getSensorName());
        sensorLogger.info(data.getDate() + "," + gson.toJson(data.getData()));
    }
}
