/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.io.File;
import java.util.List;
import project.latex.balloon.DataFolderResource;

/**
 *
 * @author dgorst
 */
public class CameraFileWriter implements CameraDataWriter {
    
    private File savedImagesDirectory;
    final static String imagesDirectoryName = "images";
    
    public CameraFileWriter()  {
    }
    
    public CameraFileWriter(DataFolderResource dataFolderResource)    {
        if (dataFolderResource == null) {
            throw new IllegalArgumentException("Null data folder resource");
        }
        setSavedImagesDirectoryFromBaseFolder(dataFolderResource.getDataFolder());
    }
    
    private void setSavedImagesDirectoryFromBaseFolder(File baseFolder) {
        if (baseFolder == null || !baseFolder.isDirectory())    {
            throw new IllegalArgumentException("Base folder is not a directory");
        }
        this.savedImagesDirectory = new File(baseFolder.getPath() + File.separator + imagesDirectoryName);
        if (!this.savedImagesDirectory.exists())   {
            this.savedImagesDirectory.mkdir();
        }
    }
    
    public File getSavedImagesDirectory()  {
        return this.savedImagesDirectory;
    }
    
    @Override
    public void writeImageFiles(List<String> imageFiles) throws DataWriteFailedException   {
        if (imageFiles == null) {
            throw new IllegalArgumentException("Null list of images passed to camera file writer");
        }
        for (String imagePath : imageFiles) {
            File imageFile = new File(imagePath);
            File movedFile = new File(this.savedImagesDirectory.getPath() + File.separator + imageFile.getName());
            if (!imageFile.renameTo(movedFile)) {
                throw new DataWriteFailedException("Failed to move file " + imageFile.getPath());
            }
        }
    }
}
