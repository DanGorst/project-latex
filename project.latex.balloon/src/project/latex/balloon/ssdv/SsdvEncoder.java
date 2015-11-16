/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

import java.io.File;

/**
 *
 * @author will
 */
public interface SsdvEncoder {
    
    // Encodes an image to ssdv format.
    void encode(String callSign, int imageId, File inputImage, String outputImagePath);
}
