package project.latex.balloon.ssdv;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author will
 */
public class DirectorySsdvImageSourceTest {
    
    private DirectorySsdvImageSource directorySsdvImageSource;

    
    @Test
    public void testGetLastModifiedImageReturnsLastModifiedIfImagesAvailable() {
        ClassLoader classLoader = getClass().getClassLoader();
        String testImagesFolderPath = classLoader.getResource(
                "project/latex/balloon/writer/camera/testImagesFolder").getFile();
        directorySsdvImageSource = new DirectorySsdvImageSource(testImagesFolderPath);
        File testImage = new File(testImagesFolderPath + File.separator + "testImage.png");
        testImage.setLastModified(10000);
        File testImage2 = new File(testImagesFolderPath + File.separator + "testImage2.png");
        testImage2.setLastModified(0);
        File latest = directorySsdvImageSource.getLastModifiedImage();
        assertEquals("testImage.png", latest.getName());
        testImage2.setLastModified(20000);
        latest = directorySsdvImageSource.getLastModifiedImage();
        assertEquals("testImage2.png", latest.getName());      
    }
    
    @Test
    public void testGetLastModifiedImageReturnsNullIfNoImagesAvailable() {
        File imagesDirectory = new File(System.getProperty("java.io.tmpdir") + File.separator + "emptyFolder");
        if (!imagesDirectory.exists()) {
            imagesDirectory.mkdir();
        }
        directorySsdvImageSource = new DirectorySsdvImageSource(imagesDirectory.getAbsolutePath());
        File latest = directorySsdvImageSource.getLastModifiedImage();
        assertEquals(null, latest);
    }
    
    @Test
    public void testDeleteAllFilesInDirectoryDeletesAllFiles() {
        File imagesDirectory = mock(File.class);
        File[] images = new File[2];
        File image1 = mock(File.class);
        File image2 = mock(File.class);
        images[0] = image1;
        images[1] = image2;
        
        when(imagesDirectory.listFiles()).thenReturn(images);
        directorySsdvImageSource = new DirectorySsdvImageSource(imagesDirectory);
        directorySsdvImageSource.deleteAllFilesInDirectory();
        
        verify(image1).delete();
        verify(image2).delete();
    }
}
