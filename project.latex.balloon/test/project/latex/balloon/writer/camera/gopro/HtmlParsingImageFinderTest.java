/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer.camera.gopro;

import project.latex.balloon.writer.camera.gopro.HtmlParsingImageFinder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dan
 */
public class HtmlParsingImageFinderTest {

    private HtmlParsingImageFinder imageFinder;

    @Before
    public void setUp() {
        imageFinder = new HtmlParsingImageFinder();
    }

    @Test
    public void testGetAvailableImagesFromDocument() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File input = new File(classLoader.getResource(
                "project/latex/balloon/writer/camera/gopro/sampleImagesList.html").getFile());
        Document doc = Jsoup.parse(input, "UTF-8");
        List<String> imagePaths = imageFinder.getAvailableImagesFromDocument(doc);

        List<String> expectedPaths = new ArrayList<>();
        expectedPaths.add(HtmlParsingImageFinder.GO_PRO_IMAGES_URL + "GOPR0165.JPG");
        expectedPaths.add(HtmlParsingImageFinder.GO_PRO_IMAGES_URL + "GOPR0166.JPG");
        expectedPaths.add(HtmlParsingImageFinder.GO_PRO_IMAGES_URL + "GOPR0167.JPG");
        expectedPaths.add(HtmlParsingImageFinder.GO_PRO_IMAGES_URL + "G0010168.JPG");
        expectedPaths.add(HtmlParsingImageFinder.GO_PRO_IMAGES_URL + "G0010182.JPG");

        assertEquals(expectedPaths, imagePaths);
    }

    @Test
    public void testImageFinderReturnsEmptyListWhenNoImagesAvailable() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File input = new File(classLoader.getResource(
                "project/latex/balloon/writer/camera/gopro/emptyImagesList.html").getFile());
        Document doc = Jsoup.parse(input, "UTF-8");
        List<String> imagePaths = imageFinder.getAvailableImagesFromDocument(doc);
        List<String> expectedPaths = new ArrayList<>();
        assertEquals(expectedPaths, imagePaths);
    }
    
    /**
     * In this test environment, the camera isn't actually available so Jsoup will
     * return an error when we try and connect to it. By calling the public method
     * on the image finder, we're trying to connect to the camera WiFi.
     */
    @Test
    public void testImageFinderReturnsEmptyListWhenCameraNotAvailable() {
        List<String> imagePaths = imageFinder.getAvailableImages();
        List<String> expectedPaths = new ArrayList<>();
        assertEquals(expectedPaths, imagePaths);
    }
}
