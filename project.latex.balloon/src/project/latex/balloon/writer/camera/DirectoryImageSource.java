/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer.camera;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author dgorst
 */
public class DirectoryImageSource implements ImageSource {

    private File imagesDirectory;

    public DirectoryImageSource(String imagesDirectoryPath) {
        if (imagesDirectoryPath == null) {
            throw new IllegalArgumentException("Images directory path cannot be null");
        }
        this.imagesDirectory = new File(imagesDirectoryPath);
        if (imagesDirectory == null || !imagesDirectory.isDirectory()) {
            throw new IllegalArgumentException("Invalid images directory specified: " + imagesDirectoryPath);
        }
    }

    @Override
    public List<File> getAvailableImages() {
        List<File> availableImages = new ArrayList<>();
        File[] imagesArray = imagesDirectory.listFiles();
        if (imagesArray != null) {
            Collections.addAll(availableImages, imagesArray);
        }
        return availableImages;
    }

}
