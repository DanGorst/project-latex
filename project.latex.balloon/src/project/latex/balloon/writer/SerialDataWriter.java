/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import project.latex.writer.DataWriter;

/**
 *
 * @author Dan
 */
public class SerialDataWriter implements DataWriter {

    private static final Logger logger = Logger.getLogger(SerialDataWriter.class);

    private final DataModelConverter converter;
    private final List<String> dataKeys;
    private final Serial serial;

    public SerialDataWriter(List<String> dataKeys, DataModelConverter converter, Serial serial) {
        this.converter = converter;
        this.dataKeys = dataKeys;
        this.serial = serial;
        
        // open the default serial port provided on the GPIO header
        serial.open(Serial.DEFAULT_COM_PORT, 38400);

        // Add a serial data listener to allow us to echo out any data written
        serial.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                logger.info(event.getData());
            }
        });
    }
    
    public SerialDataWriter(List<String> dataKeys, DataModelConverter converter) {
        this(dataKeys, converter, SerialFactory.createInstance());
    }

    @Override
    public void writeData(Map<String, Object> dataModel) {
        if (dataModel == null) {
            throw new IllegalArgumentException("Cannot write null data object");
        }

        String csvString = this.converter.convertDataToCsvString(dataKeys, dataModel);
        // Add a placeholder string to represent the checksum which is normally transmitted
        csvString += "*2F";
        serial.writeln(csvString);
    }
}
