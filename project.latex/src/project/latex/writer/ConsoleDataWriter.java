/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.writer;

import java.io.OutputStreamWriter;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import project.latex.SensorData;

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
    public void writeData(SensorData data) {
        logger.info(data.getSensorName());
        logger.info(data.getDate());
        logger.info(data.getData());
    }
    
}
