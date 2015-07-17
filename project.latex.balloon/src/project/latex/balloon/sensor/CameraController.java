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
import project.latex.balloon.writer.CameraDataWriter;
import project.latex.balloon.writer.DataWriteFailedException;

/**
 *
 * @author Dan
 */
public class CameraController implements CameraSensorController {

    private static final Logger logger = Logger.getLogger(CameraController.class);

    private final File imagesDirectory;
    private final String sensorName;

    private long mostRecentModifiedTime = 0;

    private CameraDataWriter cameraDataWriter;

    public CameraController(String imagesDirectoryString, CameraDataWriter cameraDataWriter, String sensorName) {
        this(new File(imagesDirectoryString), cameraDataWriter, sensorName);
    }

    public CameraController(File imagesDirectory, CameraDataWriter cameraDataWriter, String sensorName) {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Images directory cannot be null");
        }
        this.imagesDirectory = imagesDirectory;
        this.cameraDataWriter = cameraDataWriter;
        this.sensorName = sensorName;
    }

    public String getSensorName() {
        return sensorName;
    }

    @Override
    public void handleNewImages() {
        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            logger.debug("Cannot find images directory: " + imagesDirectory);
            return;
        }
        File[] imageFiles = imagesDirectory.listFiles();
        if (imageFiles.length == 0) {
            logger.debug("No images to handle");
            return;
        }

        List<File> sortedImageFiles = new ArrayList<>();
        Collections.addAll(sortedImageFiles, imageFiles);
        // Sort the image files into descending date/time order
        Collections.sort(sortedImageFiles, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return Long.compare(o2.lastModified(), o1.lastModified());
            }

        });

        if (mostRecentModifiedTime >= sortedImageFiles.get(0).lastModified()) {
            logger.debug("Already handled all available images");
            return;
        }

        List<File> imagesToHandle = new ArrayList<>();
        for (File imageFile : sortedImageFiles) {
            if (mostRecentModifiedTime >= imageFile.lastModified()) {
                break;
            }
            imagesToHandle.add(imageFile);
        }

        try {
            this.cameraDataWriter.writeImageFiles(imagesToHandle);

            // Update our time so we know which images we've handled already
            mostRecentModifiedTime = sortedImageFiles.get(0).lastModified();
        } catch (DataWriteFailedException ex) {
            logger.error(ex.getMessage());
        }
    }
}
