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
 * 
 * Controller for the encoding and transmission of SSDV (slow scan digital video) 
 * format images.
 */
public class SsdvController  {
    
    private static final Logger logger = Logger.getLogger(SsdvController.class);
    // Total number of 256 byte packets in encodedImage.
    private int totalPacketsInImage = 0;
    // The next packet to send, with 0 being the first 256 byte packet.
    private int nextPacket = 0;
    private File encodedImage;
    SsdvEncoderController encoder;
    SsdvDataWriter writer;
      
    public SsdvController(SsdvEncoderController encoder, SsdvDataWriter writer) {
        this.encoder = encoder;
        this.writer = writer;
    }
    
    // Sends the next packet of the current ssdv image, or the first packet of
    // the next image, if the next image is available.
    public void sendNextPacket() {
        // Get the latest encoded image if there are no more packets to send.
        if (nextPacket >= totalPacketsInImage) {
            encodedImage = encoder.getEncodedImageFile();
            if (encodedImage != null) {
                // New encoded image available, so set total packets to the total
                // number of packets in the image.
                totalPacketsInImage = (int) (encodedImage.length()/256);
            } else {
                // No image available, so set total packets to 0 so software 
                // does not attempt to send anything.
                totalPacketsInImage = 0;
            }
            nextPacket = 0;
        }  
        // Send a packet if available.
        if (nextPacket < totalPacketsInImage) {
            writer.writePacket(encodedImage.getAbsolutePath(), nextPacket);
            nextPacket += 1;
        }
    }
}
