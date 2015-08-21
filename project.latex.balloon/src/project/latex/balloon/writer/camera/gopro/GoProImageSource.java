/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer.camera.gopro;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import project.latex.balloon.writer.camera.ImageSource;

/**
 *
 * @author dgorst
 */
public class GoProImageSource implements ImageSource {

    private final ImageFinder imageFinder;
    
    private final File imagesDirectory;

    public GoProImageSource(ImageFinder imageFinder, String imagesDirectoryPath) {
        this.imageFinder = imageFinder;
        this.imagesDirectory = new File(imagesDirectoryPath);
    }
    
    @Override
    public List<File> getAvailableImages() {
        List<String> availableImageUrls = imageFinder.getAvailableImages();
        
        // Add any new images to our directory
        // TODO
        
        // Now return the contents of the directory
        List<File> availableImages = new ArrayList<>();
        Collections.addAll(availableImages, imagesDirectory.listFiles());
        return availableImages;
    }

}
