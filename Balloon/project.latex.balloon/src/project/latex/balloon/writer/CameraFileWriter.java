/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.io.File;
import java.util.List;
import java.util.Map;
import project.latex.SensorData;
import project.latex.balloon.sensor.CameraSensorController;
import project.latex.writer.CameraDataWriter;
import project.latex.writer.DataWriteFailedException;

/**
 *
 * @author dgorst
 */
public class CameraFileWriter implements CameraDataWriter {
    
    private final File savedImagesDirectory;
    final static String imagesDirectoryName = "images";
    
    public CameraFileWriter(File baseFolder)    {
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
    public void writeData(SensorData data) throws DataWriteFailedException {
        if (data == null)   {
            throw new IllegalArgumentException("Cannot write null data object");
        }
        Map<String, Object> dataMap = data.getData();
        Object imagesEntry = dataMap.get(CameraSensorController.dataKey);
        if (imagesEntry == null)    {
            throw new IllegalArgumentException("Data doesn't contain images");
        }
        
        List<String> imagePaths = (List<String>) imagesEntry;
        for (String imagePath : imagePaths) {
            File imageFile = new File(imagePath);
            File movedFile = new File(this.savedImagesDirectory.getPath() + File.separator + imageFile.getName());
            if (!imageFile.renameTo(movedFile)) {
                throw new DataWriteFailedException("Failed to move file " + imageFile.getPath());
            }
        }
    }
    
}
