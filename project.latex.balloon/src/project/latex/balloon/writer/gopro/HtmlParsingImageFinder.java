/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer.gopro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Dan
 */
public class HtmlParsingImageFinder implements ImageFinder {

    private static final Logger logger = Logger.getLogger(HtmlParsingImageFinder.class);

    static final String GO_PRO_IMAGES_URL = "http://10.5.5.9:8080/DCIM/100GOPRO/";

    List<String> getAvailableImagesFromDocument(Document doc) {
        List<String> imageUrls = new ArrayList<>();
        Elements imageLinks = doc.select("a[href$=.jpg]");
        for (Element element : imageLinks) {
            imageUrls.add(GO_PRO_IMAGES_URL + element.text());
        }
        return imageUrls;
    }

    @Override
    public List<String> getAvailableImages() {
        try {
            Document doc = Jsoup.connect(GO_PRO_IMAGES_URL).get();
            return getAvailableImagesFromDocument(doc);
        } catch (IOException ex) {
            logger.error("Unable to get images from camera: " + ex.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    public static void main(String... args) throws IOException {
        HtmlParsingImageFinder imageFinder = new HtmlParsingImageFinder();
        System.out.println(imageFinder.getAvailableImages());
    }
}
