/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dan
 */
public class CameraController implements CameraSensorController {

    private final File imagesDirectory;
    final static String sensorName = "Camera";
    
    public CameraController(String imagesDirectoryString)   {
        this(new File(imagesDirectoryString));
    }
    
    public CameraController(File imagesDirectory)   {
        if (imagesDirectory == null || !(imagesDirectory.isDirectory()))  {
            throw new IllegalArgumentException("File is not a directory");
        }
        this.imagesDirectory = imagesDirectory;
    }
    
    @Override
    public String getSensorName() {
        return sensorName;
    }

    @Override
    public List<String> getImageFileNames() {
        List<String> files = new ArrayList<>();
        for (File file : this.imagesDirectory.listFiles())  {
            if (!file.isDirectory())    {
                files.add(file.getPath());
            }
        }
        return files;
    }
}
