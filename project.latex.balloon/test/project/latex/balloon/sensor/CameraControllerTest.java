/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import project.latex.balloon.writer.CameraDataWriter;

/**
 *
 * @author dgorst
 */
public class CameraControllerTest {

    private File mockImagesDirectory;

    private CameraDataWriter cameraDataWriter;
    
    private final String sensorName = "Camera";

    @Before
    public void setUp() {
        this.mockImagesDirectory = mock(File.class);
        this.cameraDataWriter = mock(CameraDataWriter.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testControllerThrowsIfGivenNullImagesDir() {
        File directory = null;
        CameraController cameraController = new CameraController(directory, cameraDataWriter, sensorName);
    }
    
    // TODO Add unit tests for new functionality
}
