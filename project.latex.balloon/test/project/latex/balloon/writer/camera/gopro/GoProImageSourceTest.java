/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer.camera.gopro;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static project.latex.balloon.TestFileDeleteHelper.delete;

/**
 *
 * @author dgorst
 */
public class GoProImageSourceTest {

    private GoProImageSource goProImageSource;

    private String imagesDirectoryPath;

    private ImageFinder imageFinder;

    @Before
    public void setUp() {
        imageFinder = mock(ImageFinder.class);

        imagesDirectoryPath = System.getProperty("java.io.tmpdir") + File.separator + "goPro";
        File imagesDirectory = new File(imagesDirectoryPath);
        if (!imagesDirectory.exists()) {
            imagesDirectory.mkdir();
        }

        goProImageSource = new GoProImageSource(imageFinder, imagesDirectoryPath);
    }

    @After
    public void tearDown() throws IOException {
        // Remove any images stored in the test folder
        File imagesDirectory = new File(imagesDirectoryPath);
        for (File image : imagesDirectory.listFiles()) {
            delete(image);
        }
    }

    @Test
    public void testGetAvailableImages() {
        List<String> availableImageUrls = new ArrayList<>();
        availableImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/Balloons-aj.svg/2000px-Balloons-aj.svg.png");
        availableImageUrls.add("https://upload.wikimedia.org/wikipedia/commons/d/d9/Balloon_free_image.jpg");
        when(imageFinder.getAvailableImages()).thenReturn(availableImageUrls);

        List<File> expectedFiles = new ArrayList<>();
        expectedFiles.add(new File(imagesDirectoryPath + File.separator + "2000px-Balloons-aj.svg.png"));
        expectedFiles.add(new File(imagesDirectoryPath + File.separator + "Balloon_free_image.jpg"));
        assertEquals(expectedFiles, goProImageSource.getAvailableImages());
    }

    @Test
    public void testGetImageName() throws URISyntaxException {
        String name = goProImageSource.getImageName("http://www.vogella.com/img/lars/LarsVogelArticle7.png");
        assertEquals("LarsVogelArticle7.png", name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetImageNameThrowsIfUrlIsNull() throws URISyntaxException {
        goProImageSource.getImageName(null);
    }
}
