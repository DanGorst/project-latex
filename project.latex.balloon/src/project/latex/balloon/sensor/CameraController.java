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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private final CameraDataWriter cameraDataWriter;
    
    private final Set<File> handledImages;

    public CameraController(ImageSource imageSource, CameraDataWriter cameraDataWriter, String sensorName) {
        this.imageSource = imageSource;
        this.cameraDataWriter = cameraDataWriter;
        this.sensorName = sensorName;
        
        this.handledImages = new HashSet<>();
    }

    public String getSensorName() {
        return sensorName;
    }

    public Set<File> getHandledImages() {
        return handledImages;
    }

    @Override
    public void handleNewImages() {
        Set<File> imageFiles = imageSource.getAvailableImages();
        if (imageFiles.isEmpty()) {
            logger.debug("No images to handle");
            return;
        }

        List<File> imagesToHandle = new ArrayList<>();
        imagesToHandle.addAll(imageFiles);
        // Now remove all of the images we've already handled
        imagesToHandle.removeAll(handledImages);
        
        // Sort the image files into descending date/time order
        Collections.sort(imagesToHandle, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return Long.compare(o2.lastModified(), o1.lastModified());
            }

        });

        try {
            this.cameraDataWriter.writeImageFiles(imagesToHandle);
            
            // Keep track of the images we've handled. If an exception is thrown
            // while writing, then none of the images are marked as such. This
            // could lead to us writing some images multiple times, if some
            // are written successfully and others not. For now, we're accepting
            // this for the sake of simplicity, but it might be good to
            // improve this behaviour
            handledImages.addAll(imagesToHandle);
        } catch (DataWriteFailedException ex) {
            logger.error(ex.getMessage());
        }
    }
}
