/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer.camera;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author dgorst
 */
public class DirectoryImageSourceTest {

    private DirectoryImageSource directoryImageSource;

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorThrowsExceptionIfDirectoryIsNull() {
        directoryImageSource = new DirectoryImageSource(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorThrowsExceptionIfDirectoryIsInvalid() {
        directoryImageSource = new DirectoryImageSource("invalidDirectory");
    }

    @Test
    public void testGetAvailableImagesFromEmptyDirectoryReturnsEmptyArray() {
        ClassLoader classLoader = getClass().getClassLoader();
        directoryImageSource = new DirectoryImageSource(classLoader.getResource(
                "project/latex/balloon/writer/camera/emptyImagesFolder").getFile());
        List<File> expectedImages = new ArrayList<>();
        assertEquals(expectedImages, directoryImageSource.getAvailableImages());
    }

    @Test
    public void testGetAvailableImagesFromDirectoryReturnsArrayOfFiles() {
        ClassLoader classLoader = getClass().getClassLoader();
        String testImagesFolderPath = classLoader.getResource(
                "project/latex/balloon/writer/camera/testImagesFolder").getFile();
        directoryImageSource = new DirectoryImageSource(testImagesFolderPath);
        List<File> expectedImages = new ArrayList<>();
        expectedImages.add(new File(testImagesFolderPath + File.separator + "testImage.png"));
        expectedImages.add(new File(testImagesFolderPath + File.separator + "testImage2.png"));
        assertEquals(expectedImages, directoryImageSource.getAvailableImages());
    }
}
