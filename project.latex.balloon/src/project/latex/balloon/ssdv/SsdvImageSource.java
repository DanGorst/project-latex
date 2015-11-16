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
 * 
 * An image source, providing access to image files.
 */
public interface SsdvImageSource {
    
    File getLastModifiedImage();
}
