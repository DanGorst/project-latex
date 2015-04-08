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
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Dan
 */
public class SerialDataWriter implements DataWriter {

    private static final Logger logger = Logger.getLogger(SerialDataWriter.class);
    
    public static final int BAUD_RATE = 50;

    private final DataModelConverter converter;
    private List<String> dataKeys;
    private final Serial serial;

    public SerialDataWriter(DataModelConverter converter, Serial serial) {
        this.converter = converter;
        this.serial = serial;
        
        // open the default serial port provided on the GPIO header
        serial.open(Serial.DEFAULT_COM_PORT, BAUD_RATE);

        // Add a serial data listener to allow us to echo out any data written
        serial.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                logger.info("Serial data received: " + event.getData());
            }
        });
    }
    
    public SerialDataWriter(DataModelConverter converter) {
        this(converter, SerialFactory.createInstance());
    }

    public void setDataKeys(List<String> dataKeys) {
        this.dataKeys = dataKeys;
    }

    @Override
    public void writeData(Map<String, Object> dataModel) {
        if (dataModel == null) {
            throw new IllegalArgumentException("Cannot write null data object");
        }

        String csvString = this.converter.convertDataToCsvString(dataKeys, dataModel);
        serial.writeln(csvString);
    }
}
