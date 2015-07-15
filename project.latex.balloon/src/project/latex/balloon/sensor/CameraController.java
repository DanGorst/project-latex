/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import project.latex.balloon.writer.CameraDataWriter;
import project.latex.balloon.writer.DataWriteFailedException;

/**
 *
 * @author Dan
 */
public class CameraController implements CameraSensorController {

    private static final Logger logger = Logger.getLogger(CameraController.class);

    private final File imagesDirectory;
    final static String sensorName = "Camera";

    private CameraDataWriter cameraDataWriter;

    public CameraController(String imagesDirectoryString, CameraDataWriter cameraDataWriter) {
        this(new File(imagesDirectoryString), cameraDataWriter);
    }

    public CameraController(File imagesDirectory, CameraDataWriter cameraDataWriter) {
        if (imagesDirectory == null || !(imagesDirectory.isDirectory())) {
            throw new IllegalArgumentException("File is not a directory");
        }
        this.imagesDirectory = imagesDirectory;
        this.cameraDataWriter = cameraDataWriter;
    }

    public String getSensorName() {
        return sensorName;
    }

    public List<String> getImageFileNames() {
        List<String> files = new ArrayList<>();
        for (File file : this.imagesDirectory.listFiles()) {
            if (!file.isDirectory()) {
                files.add(file.getPath());
            }
        }
        return files;
    }

    @Override
    public void handleNewImages() {
        List<String> imageFiles = getImageFileNames();
        try {
            this.cameraDataWriter.writeImageFiles(imageFiles);
        } catch (DataWriteFailedException ex) {
            logger.error("Failed to write image files", ex);
        }
    }
}
