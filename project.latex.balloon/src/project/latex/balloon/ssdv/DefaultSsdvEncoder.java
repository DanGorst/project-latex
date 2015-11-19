/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 */
public class DefaultSsdvEncoder implements SsdvEncoder {
    
    private static final Logger logger = Logger.getLogger(LastModifiedSsdvEncoderController.class);
    
    // Encodes images to ssdv format using a 3rd party tool, found here:
    // https://github.com/fsphil/ssdv
    @Override
    public void encode(String callSign, int imageId, File inputImage, String outputImagePath) {
        Process encodeScript;
        try
        {   logger.info(String.format(
                    "./ssdv -e -c %s -i %d %s %s",
                    callSign, imageId, inputImage.getPath(), outputImagePath));
            // Run the encode script.
            encodeScript = Runtime.getRuntime().exec(String.format(
                    "./ssdv -e -c %s -i %d %s %s",
                    callSign, imageId, inputImage.getPath(), outputImagePath));
            // Checks exit status of the script.
            if (encodeScript.waitFor() != 0)
            {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(encodeScript.getErrorStream()));
                
                String errorMessage = "";
                String line;

                logger.warn(String.format("Errorstream from encoding image %s:", inputImage.getName()));
                while ((line = bufferedReader.readLine()) != null) {
                    errorMessage += line + "\n";
                }                   
                logger.warn(errorMessage);
                bufferedReader.close();
            }
        }
        catch (IOException | InterruptedException e) 
        {
            logger.error(String.format("Could not encode image file %s", inputImage.getPath()));
        }
    }
}
