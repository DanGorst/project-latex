/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;
import project.latex.balloon.writer.camera.CameraDataWriter;
import project.latex.balloon.writer.DataWriteFailedException;
import project.latex.balloon.writer.camera.ImageSource;

/**
 *
 * @author Dan
 */
public class CameraController implements CameraSensorController {

    private static final Logger logger = Logger.getLogger(CameraController.class);

    private final ImageSource imageSource;
    private final String sensorName;

    private long mostRecentModifiedTime = 0;

    private final CameraDataWriter cameraDataWriter;

    public CameraController(ImageSource imageSource, CameraDataWriter cameraDataWriter, String sensorName) {
        this.imageSource = imageSource;
        this.cameraDataWriter = cameraDataWriter;
        this.sensorName = sensorName;
    }

    public String getSensorName() {
        return sensorName;
    }

    @Override
    public void handleNewImages() {
        List<File> imageFiles = imageSource.getAvailableImages();
        if (imageFiles.isEmpty()) {
            logger.debug("No images to handle");
            return;
        }

        // Sort the image files into descending date/time order
        Collections.sort(imageFiles, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return Long.compare(o2.lastModified(), o1.lastModified());
            }

        });

        if (mostRecentModifiedTime >= imageFiles.get(0).lastModified()) {
            logger.debug("Already handled all available images");
            return;
        }

        List<File> imagesToHandle = new ArrayList<>();
        for (File imageFile : imageFiles) {
            if (mostRecentModifiedTime >= imageFile.lastModified()) {
                break;
            }
            imagesToHandle.add(imageFile);
        }

        try {
            this.cameraDataWriter.writeImageFiles(imagesToHandle);

            // Update our time so we know which images we've handled already
            mostRecentModifiedTime = imageFiles.get(0).lastModified();
        } catch (DataWriteFailedException ex) {
            logger.error(ex.getMessage());
        }
    }
}
