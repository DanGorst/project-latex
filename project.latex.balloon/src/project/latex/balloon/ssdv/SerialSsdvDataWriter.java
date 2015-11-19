/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 */
public class SerialSsdvDataWriter implements SsdvDataWriter {
    
    private static final Logger logger = Logger.getLogger(SerialSsdvDataWriter.class);
    private final int baudRate;
    private Serial serial;
    
    // This constructor is for testing only.
    public SerialSsdvDataWriter(int baudRate, Serial serial) {
        this.baudRate = baudRate;
        this.serial = serial;
    }
    
    public SerialSsdvDataWriter(int baudRate) {
        this(baudRate, SerialFactory.createInstance());
    }
    
   
    @Override
    public void writePacket(String encodedImageFilePath, int packetToSend) {
        if (serial.isClosed()) {
            serial.open(Serial.DEFAULT_COM_PORT, baudRate);
        }
        runSendPacketScript(encodedImageFilePath, packetToSend);
        serial.close();
    }
    
    // Runs a python script to send the encoded image packet.
    private void runSendPacketScript(String encodedImageFilePath, int packetToSend) {
        logger.info(String.format("Starting ssdvPacketTransmit.py for packet %d of image '%s'", 
                packetToSend, encodedImageFilePath));
        
        Process imageSendScript;
        try
        {
            // Run the encode script.
            imageSendScript = Runtime.getRuntime().exec(
                    String.format("python scripts/ssdvPacketTransmit.py %s %d %d", 
                            encodedImageFilePath, packetToSend, baudRate));
            // Checks exit status of the script.
            if (imageSendScript.waitFor() != 0)
            {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(imageSendScript.getErrorStream()));
                
                String errorMessage = "";
                String line;

                logger.warn(String.format("Errorstream from imageTransmit.py"));
                while ((line = bufferedReader.readLine()) != null) {
                    errorMessage += line + "\n";
                }                   
                logger.warn(errorMessage);
                bufferedReader.close();
            }
        }
        catch (IOException | InterruptedException e) 
        {
            logger.error(String.format("Could not run imageTransmit.py"));
        }
    }      
 
}
