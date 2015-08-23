/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
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
        Set<File> availableFiles = new HashSet<>();
        when(mockImageSource.getAvailableImages()).thenReturn(availableFiles);

        cameraController.handleNewImages();

        verify(cameraDataWriter, never()).writeImageFiles((List<File>) any());
    }

    @Test
    public void testHandleNewImagesSendsImagesToWriterInDescendingModifiedOrder() throws DataWriteFailedException {
        File mockFile1 = mock(File.class);
        when(mockFile1.lastModified()).thenReturn(10L);

        File mockFile2 = mock(File.class);
        when(mockFile2.lastModified()).thenReturn(20L);

        Set<File> availableFiles = new HashSet<>();
        availableFiles.add(mockFile1);
        availableFiles.add(mockFile2);
        when(mockImageSource.getAvailableImages()).thenReturn(availableFiles);

        cameraController.handleNewImages();

        List<File> expectedFiles = new ArrayList<>();
        expectedFiles.add(mockFile2);
        expectedFiles.add(mockFile1);
        verify(cameraDataWriter).writeImageFiles(expectedFiles);
    }

    @Test
    public void testImagesAreMarkedAsHandledOnceTheyAreSuccessfullyWritten() throws DataWriteFailedException {
        File mockFile1 = mock(File.class);
        when(mockFile1.lastModified()).thenReturn(10L);

        File mockFile2 = mock(File.class);
        when(mockFile2.lastModified()).thenReturn(20L);

        Set<File> availableFiles = new HashSet<>();
        availableFiles.add(mockFile1);
        availableFiles.add(mockFile2);
        when(mockImageSource.getAvailableImages()).thenReturn(availableFiles);

        cameraController.handleNewImages();

        List<File> expectedFiles = new ArrayList<>();
        expectedFiles.add(mockFile2);
        expectedFiles.add(mockFile1);
        verify(cameraDataWriter).writeImageFiles(expectedFiles);
        
        Set<File> expectedHandledFiles = new HashSet<>();
        expectedHandledFiles.add(mockFile2);
        expectedHandledFiles.add(mockFile1);
        assertEquals(expectedHandledFiles, cameraController.getHandledImages());
    }

    @Test
    public void testImagesAreNotMarkedAsHandledIfThereIsAnErrorDuringWriting() throws DataWriteFailedException {
        File mockFile1 = mock(File.class);
        when(mockFile1.lastModified()).thenReturn(10L);

        File mockFile2 = mock(File.class);
        when(mockFile2.lastModified()).thenReturn(20L);

        Set<File> availableFiles = new HashSet<>();
        availableFiles.add(mockFile1);
        availableFiles.add(mockFile2);
        when(mockImageSource.getAvailableImages()).thenReturn(availableFiles);

        doThrow(new DataWriteFailedException("test")).when(cameraDataWriter).writeImageFiles((List<File>) any());

        cameraController.handleNewImages();
        
        Set<File> expectedHandledFiles = new HashSet<>();
        assertEquals(expectedHandledFiles, cameraController.getHandledImages());
    }
}
