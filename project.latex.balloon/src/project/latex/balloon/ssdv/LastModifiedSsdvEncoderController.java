/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

import java.io.File;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 */
public class LastModifiedSsdvEncoderController implements SsdvEncoderController {
    
    private static final Logger logger = Logger.getLogger(LastModifiedSsdvEncoderController.class);
    private final String callSign = "Latex";
    private int imageId = 0;
    
    private final SsdvImageSource piCamImageSource;    
    private final DirectorySsdvImageSource encodedImageSource;
    private final SsdvEncoder ssdvEncoder;
    
    public LastModifiedSsdvEncoderController(SsdvImageSource piCamImageSource, 
            DirectorySsdvImageSource encodedImageSource, SsdvEncoder ssdvEncoder) {
        this.piCamImageSource = piCamImageSource;
        this.encodedImageSource = encodedImageSource;
        this.ssdvEncoder = ssdvEncoder;
    }
    
    // Gets an encoded version of the last modified image file from the PiCam images folder.
    @Override
    public File getEncodedImageFile() {
        // There should only be one encoded image in the directory, so delete any
        // other files in this directory.
        encodedImageSource.deleteAllFilesInDirectory();
        // Get the last modified image and encode it, put the encoded
        // file in the encoded image directory.
        File lastModifiedImage = piCamImageSource.getLastModifiedImage();
        String outputImagePath = encodedImageSource.getImagesDirectory().getPath() + "/" + lastModifiedImage.getName();
        ssdvEncoder.encode(callSign, imageId, lastModifiedImage, outputImagePath);
        imageId = (imageId + 1) % 256;
        // Check to see if the encoded file is there in the directory.
        File[] encodedImageFolderContents = encodedImageSource.getImagesDirectory().listFiles();
        if (encodedImageFolderContents.length == 1) {
            return encodedImageFolderContents[0];
        } else {
            logger.error(String.format("Expected encoded image folder "
                    + "to contain a single encoded image, but %d files were found.", 
                    encodedImageFolderContents.length));
            return null;            
        }
    }
}
