/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import project.latex.writer.DataWriteFailedException;

/**
 *
 * @author dgorst
 */
public class SensorFileLoggerService {
    private final Map<String, Logger> loggers = new HashMap<>();
    
    public void setLoggerForSensor(String sensorName, String baseUrl) throws DataWriteFailedException    {
        if (sensorName == null) {
            throw new DataWriteFailedException("Sensor name is null");
        }
        
        if (baseUrl == null)    {
            throw new DataWriteFailedException("Base url is null");
        }
        
        Logger sensorLogger = Logger.getLogger(sensorName);
        // Each of our sensors will log to their own file
        sensorLogger.addAppender(createFileAppender(baseUrl + File.separator + sensorName + ".csv"));
        loggers.put(sensorName, sensorLogger);
    }
    
    public Logger getLoggerForSensor(String sensorName)   {
        return loggers.get(sensorName);
    }
    
    private FileAppender createFileAppender(String fileName)    {
        FileAppender fileAppender = new FileAppender();
        fileAppender.setName("SensorLogger");
        fileAppender.setFile(fileName);
        fileAppender.setLayout(new PatternLayout("%m%n"));
        fileAppender.setThreshold(Level.INFO);
        fileAppender.setAppend(true);
        fileAppender.activateOptions();
        return fileAppender;
    }
}
