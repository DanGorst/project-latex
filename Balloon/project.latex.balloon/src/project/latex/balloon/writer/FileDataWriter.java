/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import project.latex.SensorData;
import project.latex.writer.DataWriteFailedException;
import project.latex.writer.DataWriter;

/**
 *
 * @author dgorst
 */
public class FileDataWriter implements DataWriter {
    private final SensorFileLoggerService loggerService;
    private final Map<Logger, List> headersForLogger;
    
    public FileDataWriter(SensorFileLoggerService loggerService)   {
        this.loggerService = loggerService;
        this.headersForLogger = new HashMap<>();
    }
    
    @Override
    public void writeData(SensorData data) throws DataWriteFailedException {
        if (data == null)   {
            throw new DataWriteFailedException("Data is null");
        }
        
        Logger sensorLogger = this.loggerService.getLoggerForSensor(data.getSensorName());
        if (sensorLogger == null)   {
            throw new DataWriteFailedException("Could not find logger for sensor with name: " + data.getSensorName());
        }
        
        // If we haven't written the headers for this logger yet, do it now
        if (!headersForLogger.containsKey(sensorLogger))    {
            Map<String, Object> dataMap = data.getData();
            List<String> orderedKeys = new ArrayList<>();
            for (String key : dataMap.keySet()) {
                orderedKeys.add(key);
            }
            Collections.sort(orderedKeys);
            StringBuilder headerStringBuilder = new StringBuilder("Date");
            for (String key : orderedKeys)  {
                headerStringBuilder.append(",").append(key);
            }
            sensorLogger.info(headerStringBuilder.toString());
            headersForLogger.put(sensorLogger, orderedKeys);
        }
        
        List<String> headers = headersForLogger.get(sensorLogger);
        StringBuilder dataStringBuilder = new StringBuilder(data.getDate().toString());
        Map<String, Object> dataMap = data.getData();
        for (String header : headers)   {
            dataStringBuilder.append(",").append(dataMap.get(header));
        }
        sensorLogger.info(dataStringBuilder.toString());
    }
}
