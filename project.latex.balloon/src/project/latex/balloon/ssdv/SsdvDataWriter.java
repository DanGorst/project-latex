/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

/**
 *
 * @author will
 */
public interface SsdvDataWriter {
    
    // Send a particular packet of an encoded image at the given path.
    void writePacket(String encodedImageFilePath, int packetToSend);
}
