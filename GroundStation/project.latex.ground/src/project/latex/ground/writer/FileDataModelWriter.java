/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.ground.writer;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import project.latex.ground.BalloonDataModel;

/**
 *
 * @author dgorst
 */
public class FileDataModelWriter implements DataModelWriter {

    // Calling getLogger with a name will always return a reference to the same logger object. For this reason,
    // we've made the loggers static - it makes it clearer that you're dealing with the same object rather than
    // a new instance
    private static final Logger dataModelLogger = Logger.getLogger("DataModelLogger");
    private static final Logger sensorLogger = Logger.getLogger("SensorLogger");
    
    public FileDataModelWriter()    {
        FileDataModelWriter.dataModelLogger.addAppender(this.createFileAppender("balloonDataModel.csv"));
        dataModelLogger.info("Starting data model logger");
        dataModelLogger.info("Lat (degrees),Lon (degrees),Height (m)");
        
        FileDataModelWriter.sensorLogger.addAppender(this.createFileAppender("sensor.csv"));
        sensorLogger.info("Starting sensor logger");
        sensorLogger.info("Temp (degrees)");
    }
    
    private FileAppender createFileAppender(String fileName)    {
        FileAppender fileAppender = new FileAppender();
        fileAppender.setName("FileLogger");
        fileAppender.setFile(fileName);
        fileAppender.setLayout(new PatternLayout("%d{dd MMM yyyy HH:mm:ss},%m%n"));
        fileAppender.setThreshold(Level.INFO);
        fileAppender.setAppend(true);
        fileAppender.activateOptions();
        return fileAppender;
    }
    
    @Override
    public void writeDataModel(BalloonDataModel dataModel) {
        dataModelLogger.info(dataModel.getLatitude() + "," + dataModel.getLongitude() + "," + dataModel.getHeight());
        // We don't actually have sensor data yet, so just write some dummy data
        sensorLogger.info("23.45");
    }
    
}
