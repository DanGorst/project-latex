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
import project.latex.balloon.TransmittedDataKeysResource;

/**
 *
 * @author Dan
 */
public class SerialDataWriter implements DataWriter {

    private static final Logger logger = Logger.getLogger(SerialDataWriter.class);

    private final DataModelConverter converter;
    private final List<String> dataKeys;
    private final Serial serial;

    public SerialDataWriter(TransmittedDataKeysResource transmittedDataKeysResource, 
            DataModelConverter converter, Serial serial, int baudRate) {
        this.converter = converter;
        this.serial = serial;
        this.dataKeys = transmittedDataKeysResource.getTransmittedDataKeys();

        // open the default serial port provided on the GPIO header
        serial.open(Serial.DEFAULT_COM_PORT, baudRate);

        // Add a serial data listener to allow us to echo out any data written
        serial.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                logger.info("Serial data received: " + event.getData());
            }
        });
    }

    public SerialDataWriter(TransmittedDataKeysResource transmittedDataKeysResource, DataModelConverter converter,
            int baudRate) {
        this(transmittedDataKeysResource, converter, SerialFactory.createInstance(), baudRate);
    }

    @Override
    public void writeData(Map<String, Object> dataModel) {
        String csvString = convertDataToCsvString(dataModel);
        serial.writeln(csvString);
    }

    public String convertDataToCsvString(Map<String, Object> dataModel) {
        if (dataModel == null) {
            throw new IllegalArgumentException("Cannot write null data object");
        }

        return this.converter.convertDataToCsvString(dataKeys, dataModel);
    }

    public void writeString(String data) {
        serial.write(data);
    }
}
