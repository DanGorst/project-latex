/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer.camera.gopro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import project.latex.balloon.writer.camera.ImageSource;

/**
 *
 * @author dgorst
 */
public class GoProImageSource implements ImageSource {

    private static final Logger logger = Logger.getLogger(GoProImageSource.class);

    private final ImageFinder imageFinder;

    private final File imagesDirectory;

    public GoProImageSource(ImageFinder imageFinder, String imagesDirectoryPath) {
        this.imageFinder = imageFinder;
        this.imagesDirectory = new File(imagesDirectoryPath);
    }

    String getImageName(String imageUrl) throws URISyntaxException {
        if (imageUrl == null) {
            throw new IllegalArgumentException("Null image URL passes to GoPro image source");
        }
        URI uri = new URI(imageUrl);
        String[] segments = uri.getPath().split("/");
        return segments[segments.length - 1];
    }

    void getAvailableImagesFromImageFinder() {
        List<String> availableImageUrls = imageFinder.getAvailableImages();

        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Add any new images to our directory
        for (String imageUrl : availableImageUrls) {
            HttpGet httpGet = new HttpGet(imageUrl);
            try {
                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String imageName = getImageName(imageUrl);
                    File file = new File(imagesDirectory + File.separator + imageName);
                    if (!file.exists()) {
                        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                            entity.writeTo(fileOutputStream);
                        }
                    }
                }
            } catch (IOException | URISyntaxException ex) {
                logger.error(ex.getLocalizedMessage());
            }
        }
    }

    @Override
    public List<File> getAvailableImages() {
        getAvailableImagesFromImageFinder();

        List<File> availableImages = new ArrayList<>();
        // Now return the contents of the directory
        Collections.addAll(availableImages, imagesDirectory.listFiles());
        return availableImages;
    }
}
