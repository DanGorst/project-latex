/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 */
public class SsdvControllerImpl implements SsdvController {
    
    private static final Logger logger = Logger.getLogger(SsdvControllerImpl.class);
    FileInputStream encodedImageInputStream;
    SsdvEncoderController encoder;
    SsdvDataWriter writer;
      
    public SsdvControllerImpl(SsdvEncoderController encoder, SsdvDataWriter writer) {
        this.encoder = encoder;
        this.writer = writer;
    }
    
    @Override
    public void sendNextPacket() {
        byte[] packet = new byte[256];
        try {
            // Get the latest encoded image if there is no packet to send.
            if (encodedImageInputStream == null || encodedImageInputStream.available() <= 0) {
                File latestImageEncoded = encoder.getEncodedImageFile();
                if (latestImageEncoded != null) {
                    encodedImageInputStream = new FileInputStream(latestImageEncoded);
                }
            }  
            // Send a packet if available.
            if (encodedImageInputStream != null && encodedImageInputStream.available() > 0) {
                encodedImageInputStream.read(packet);
                writer.writeData(packet);
            }
        } catch (IOException e) {
            logger.error("IO error while reading from encoded image file: " + e);
        }
    }
}
