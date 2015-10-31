/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import static java.lang.Math.ceil;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 */
public class SerialSsdvDataWriter implements SsdvDataWriter {
    
    private static final Logger logger = Logger.getLogger(SerialSsdvDataWriter.class);
    
    private Serial serial;
    //Array of 16 byte data chunks to send.
    private ArrayList<byte[]> dataChunks;  
    // Pi hardware serial buffer fits 128 bits or 16 bytes.
    private final int bufferSize = 16;
    // Delay between sending data chunks to serial port.
    private final int delayInMilliseconds;
    
    private final Thread worker;
    
    // This constructor is for testing only.
    public SerialSsdvDataWriter(int baudRate, Serial serial) {
        // Delay should be enough time for buffer to send 16 bytes at the given baudRate. 
        // Give an extra 50% of the time that should be required to make sure there is enough time to send the data.     
        this.delayInMilliseconds = (int) ceil(1.50 * ((bufferSize*8.0)/baudRate)*1000);
        this.serial = serial;
        this.dataChunks = new ArrayList<>();
        
        // open the default serial port provided on the GPIO header
        serial.open(Serial.DEFAULT_COM_PORT, baudRate);

        // Add a serial data listener to allow us to echo out any data written
        serial.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                logger.info("Serial SSDV data chunk received.");
            }
        });
        
        worker = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    // Write the next chunk of data if it is available
                    writeNextChunk();
                    try {
                        // Wait for the specified delay before writing the next chunk
                        Thread.sleep(delayInMilliseconds);
                    } catch (InterruptedException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        });
    }
    
    public SerialSsdvDataWriter(int baudRate) {
        this(baudRate, SerialFactory.createInstance());
    }
    
    public void start() {
        logger.info("Chunked serial data writer started");
        worker.start();
    }
    
    @Override
    public void writeData(byte[] packet) {
        dataChunks.addAll(breakIntoChunks(packet));
    }
    
    private static ArrayList<byte[]> breakIntoChunks(byte[] data) {       
        ArrayList<byte[]> chunks = new ArrayList<>();
        for (int i = 0; i < data.length; i += 16) {
            chunks.add(Arrays.copyOfRange(data, i, i + 16));
        }
        return chunks;
    }
    
    private void writeNextChunk() {
        if (!dataChunks.isEmpty()) {
            logger.info("Writing next chunk");
            serial.write(dataChunks.remove(0));
        }
    }  
}
