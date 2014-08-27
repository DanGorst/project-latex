/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import project.latex.writer.DataWriter;

/**
 *
 * @author dgorst
 */
public class FileDataWriter implements DataWriter {
    
    private final Logger logger;
    private final DataModelConverter converter;
    final static String fileName = "dataModel.csv";
    private final List<String> dataKeys;
    
    public FileDataWriter(List<String> dataKeys, DataModelConverter converter, Logger logger, FileAppender fileAppender)   {
        this.logger = logger;
        this.logger.addAppender(fileAppender);
        this.converter = converter;
        this.dataKeys = dataKeys;
        writeHeaders();
    }
    
    public FileDataWriter(File dataFolder, List<String> dataKeys, DataModelConverter converter)   {
        this(dataKeys, converter, Logger.getLogger(FileDataWriter.class), createFileAppender(dataFolder));
    }
    
    private static FileAppender createFileAppender(File dataFolder)    {
        String filePath = fileName;
        if (dataFolder != null)  {
            filePath = dataFolder.getPath() + File.separator + fileName;
        }
        
        FileAppender fileAppender = new FileAppender();
        fileAppender.setName("FileLogger");
        fileAppender.setFile(filePath);
        fileAppender.setLayout(new PatternLayout("%m%n"));
        fileAppender.setThreshold(Level.INFO);
        fileAppender.setAppend(true);
        fileAppender.activateOptions();
        return fileAppender;
    }
    
    private void writeHeaders() {
        logger.info(this.converter.convertDataKeysToCsvString(this.dataKeys));
    }
    
    @Override
    public void writeData(Map<String, Object> data) {
        logger.info(this.converter.convertDataToCsvString(this.dataKeys, data));
    }
}
