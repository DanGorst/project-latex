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
public class DirectorySsdvImageSource implements SsdvImageSource {
    
    private File imagesDirectory;

    public DirectorySsdvImageSource(File imagesDirectory) {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Images directory cannot be null");
        }
        this.imagesDirectory = imagesDirectory; 
    }
    
    public DirectorySsdvImageSource(String imagesDirectoryPath) {
        if (imagesDirectoryPath == null) {
            throw new IllegalArgumentException("Images directory path cannot be null");
        }
        this.imagesDirectory = new File(imagesDirectoryPath);
        if (imagesDirectory == null || !imagesDirectory.isDirectory()) {
            throw new IllegalArgumentException("Invalid images directory specified: " + imagesDirectoryPath);
        }
    }
    
    @Override
    public File getLastModifiedImage() {
      File[] imagesArray = imagesDirectory.listFiles();
      File latestImage = null;
      for (File image : imagesArray) {
          if (latestImage == null || image.lastModified() > latestImage.lastModified()) {
              latestImage = image;
          }
      }
      return latestImage;
    }
    
    public File getImagesDirectory() {
        return imagesDirectory;
    }
    
    public void deleteAllFilesInDirectory() {
        File[] imagesArray = imagesDirectory.listFiles();
        for (File image : imagesArray) {
            image.delete();
        }
    }
}
