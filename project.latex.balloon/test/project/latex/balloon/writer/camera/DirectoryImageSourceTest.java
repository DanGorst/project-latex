/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer.camera;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
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
        File imagesDirectory = new File(System.getProperty("java.io.tmpdir") + File.separator + "emptyFolder");
        if (!imagesDirectory.exists()) {
            imagesDirectory.mkdir();
        }
        directoryImageSource = new DirectoryImageSource(imagesDirectory.getAbsolutePath());
        Set<File> expectedImages = new HashSet<>();
        assertEquals(expectedImages, directoryImageSource.getAvailableImages());
    }

    @Test
    public void testGetAvailableImagesFromDirectoryReturnsArrayOfFiles() {
        ClassLoader classLoader = getClass().getClassLoader();
        String testImagesFolderPath = classLoader.getResource(
                "project/latex/balloon/writer/camera/testImagesFolder").getFile();
        directoryImageSource = new DirectoryImageSource(testImagesFolderPath);
        Set<File> expectedImages = new HashSet<>();
        expectedImages.add(new File(testImagesFolderPath + File.separator + "testImage.png"));
        expectedImages.add(new File(testImagesFolderPath + File.separator + "testImage2.png"));
        assertEquals(expectedImages, directoryImageSource.getAvailableImages());
    }
    
}
