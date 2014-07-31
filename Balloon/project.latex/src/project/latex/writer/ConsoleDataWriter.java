/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.writer;

import java.io.OutputStreamWriter;
import java.util.Map;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author Dan
 */
public class ConsoleDataWriter implements DataWriter {
 
    private static final Logger logger = Logger.getLogger(ConsoleDataWriter.class);
    
    public ConsoleDataWriter()  {
        ConsoleAppender ca = new ConsoleAppender();
        ca.setWriter(new OutputStreamWriter(System.out));
        ca.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
        logger.addAppender(ca);
    }
    
    @Override
    public void writeData(Map<String, Object> dataModel) throws DataWriteFailedException {
        logger.info(dataModel);
    }
    
}
