/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import project.latex.balloon.writer.DataWriteFailedException;
import project.latex.balloon.writer.camera.CameraDataWriter;
import project.latex.balloon.writer.camera.ImageSource;

/**
 *
 * @author dgorst
 */
public class CameraControllerTest {

    private ImageSource mockImageSource;

    private CameraDataWriter cameraDataWriter;

    private final String sensorName = "Camera";

    private CameraController cameraController;

    @Before
    public void setUp() {
        this.mockImageSource = mock(ImageSource.class);
        this.cameraDataWriter = mock(CameraDataWriter.class);

        cameraController = new CameraController(mockImageSource, cameraDataWriter, sensorName);
    }

    @Test
    public void testHandleNewImagesNoImagesAvailable() throws DataWriteFailedException {
        List<File> availableFiles = new ArrayList<>();
        when(mockImageSource.getAvailableImages()).thenReturn(availableFiles);

        cameraController.handleNewImages();
        
        verify(cameraDataWriter, never()).writeImageFiles(availableFiles);
    }

    @Test
    public void testHandleNewImagesSendsImagesToWriterInDescendingModifiedOrder() throws DataWriteFailedException {
        File mockFile1 = mock(File.class);
        when(mockFile1.lastModified()).thenReturn(10L);

        File mockFile2 = mock(File.class);
        when(mockFile2.lastModified()).thenReturn(20L);

        List<File> availableFiles = new ArrayList<>();
        availableFiles.add(mockFile1);
        availableFiles.add(mockFile2);
        when(mockImageSource.getAvailableImages()).thenReturn(availableFiles);
        
        cameraController.handleNewImages();

        List<File> expectedFiles = new ArrayList<>();
        expectedFiles.add(mockFile2);
        expectedFiles.add(mockFile1);
        verify(cameraDataWriter).writeImageFiles(expectedFiles);
    }
}
