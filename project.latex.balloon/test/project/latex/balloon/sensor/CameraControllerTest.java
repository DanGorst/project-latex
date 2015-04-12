/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author dgorst
 */
public class CameraControllerTest {
    
    private File mockImagesDirectory;
    
    public CameraControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.mockImagesDirectory = mock(File.class);
    }
    
    @After
    public void tearDown() {
        this.mockImagesDirectory = null;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testControllerThrowsIfGivenNullImagesDir()  {
        File directory = null;
        CameraController cameraController = new CameraController(directory);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testControllerThrowsIfGivenFileRatherThanDirectory()  {
        when(this.mockImagesDirectory.isDirectory()).thenReturn(false);
        new CameraController(this.mockImagesDirectory);
    }

    /**
     * Test of getSensorName method, of class CameraController.
     */
    @Test
    public void testGetSensorName() {
        when(this.mockImagesDirectory.isDirectory()).thenReturn(true);
        CameraController instance = new CameraController(this.mockImagesDirectory);
        assertEquals(CameraController.sensorName, instance.getSensorName());
    }

    /**
     * Test of getCurrentData method, of class CameraController.
     */
    @Test
    public void testCurrentDataIsEmptyListWhenImagesDirIsEmpty() {
        when(this.mockImagesDirectory.isDirectory()).thenReturn(true);
        CameraController instance = new CameraController(this.mockImagesDirectory);
        
        when(this.mockImagesDirectory.listFiles()).thenReturn(new File[0]);
        List<String> actual = instance.getImageFileNames();
        assertEquals(new ArrayList<String>(), actual);
    }
    
    @Test
    public void testCurrentDataIsValidWhenImagesDirHasOneImage() {
        when(this.mockImagesDirectory.isDirectory()).thenReturn(true);
        CameraController instance = new CameraController(this.mockImagesDirectory);
        
        File[] images = new File[1];
        File image = mock(File.class);
        when(image.isDirectory()).thenReturn(false);
        String imagePath = "test.jpg";
        when(image.getPath()).thenReturn(imagePath);
        images[0] = image;
        when(this.mockImagesDirectory.listFiles()).thenReturn(images);
        List<String> actual = instance.getImageFileNames();
        
        List<String> expectedImages = new ArrayList<>();
        expectedImages.add(imagePath);
        assertEquals(expectedImages, actual);
    }
    
    @Test
    public void testCurrentDataIsValidWhenImagesDirHasTwoImages() {
        when(this.mockImagesDirectory.isDirectory()).thenReturn(true);
        CameraController instance = new CameraController(this.mockImagesDirectory);
        
        File[] images = new File[2];
        File image = mock(File.class);
        when(image.isDirectory()).thenReturn(false);
        String imagePath = "test.jpg";
        when(image.getPath()).thenReturn(imagePath);
        images[0] = image;
        
        String imagePath2 = "test2.jpg";
        File image2 = mock(File.class);
        when(image2.isDirectory()).thenReturn(false);
        when(image2.getPath()).thenReturn(imagePath2);
        images[1] = image2;
        
        when(this.mockImagesDirectory.listFiles()).thenReturn(images);
        List<String> actual = instance.getImageFileNames();
        
        List<String> expectedImages = new ArrayList<>();
        expectedImages.add(imagePath);
        expectedImages.add(imagePath2);
        assertEquals(expectedImages, actual);
    }
    
    @Test
    public void testControllerIgnoresDirectoriesInImagesDir() {
        when(this.mockImagesDirectory.isDirectory()).thenReturn(true);
        CameraController instance = new CameraController(this.mockImagesDirectory);
        
        File[] images = new File[2];
        File image = mock(File.class);
        when(image.isDirectory()).thenReturn(false);
        String imagePath = "test.jpg";
        when(image.getPath()).thenReturn(imagePath);
        images[0] = image;
        
        String testDirName = "test2";
        File testDir = mock(File.class);
        when(testDir.isDirectory()).thenReturn(true);
        when(testDir.getPath()).thenReturn(testDirName);
        images[1] = testDir;
        
        when(this.mockImagesDirectory.listFiles()).thenReturn(images);
        List<String> actual = instance.getImageFileNames();

        List<String> expectedImages = new ArrayList<>();
        expectedImages.add(imagePath);
        assertEquals(expectedImages, actual);
    }
}
