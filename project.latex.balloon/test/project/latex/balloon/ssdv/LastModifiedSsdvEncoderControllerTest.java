/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

import java.io.File;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author will
 */
public class LastModifiedSsdvEncoderControllerTest {
    
    private SsdvImageSource mockPiCamImageSource;
    private DirectorySsdvImageSource mockEncodedImageSource;
    private SsdvEncoder mockSsdvEncoder;
    private File mockLastModifiedImage;
    private File mockEncodedImage;
    File mockEncodedImagesDirectory;
    
    private LastModifiedSsdvEncoderController ssdvEncoderController;
    
    @Before
    public void setUp() {
        this.mockPiCamImageSource = mock(SsdvImageSource.class);
        this.mockEncodedImageSource = mock(DirectorySsdvImageSource.class);
        this.mockSsdvEncoder = mock(SsdvEncoder.class); 
        ssdvEncoderController = new LastModifiedSsdvEncoderController(
                        mockPiCamImageSource, mockEncodedImageSource, mockSsdvEncoder);
        // Mock encoded image directory.
        File[] mockEncodedImageDirectoryContents = new File[1];
        mockEncodedImage = mock(File.class);
        mockEncodedImageDirectoryContents[0] = mockEncodedImage;
        mockEncodedImagesDirectory = mock(File.class);
        when(mockEncodedImagesDirectory.getPath()).thenReturn("path");
        when(mockEncodedImagesDirectory.listFiles()).thenReturn(mockEncodedImageDirectoryContents);
        when(mockEncodedImageSource.getImagesDirectory()).thenReturn(mockEncodedImagesDirectory);
        // Mock last modified image.
        mockLastModifiedImage = mock(File.class);
        when(mockLastModifiedImage.getName()).thenReturn("name");
        when(mockPiCamImageSource.getLastModifiedImage()).thenReturn(mockLastModifiedImage);
    }
    
    @Test
    public void getEncodedImageFile_shouldCallSsdvEncoderWithCorrectArguments() {       
        ssdvEncoderController.getEncodedImageFile();
        verify(mockSsdvEncoder).encode(anyString(), eq(0), eq(mockLastModifiedImage), eq("path/name"));
    }
    
    @Test
    public void getEncodedImageFile_eachCallToSsdvEncoderShouldIncrementImageId() {       
        ssdvEncoderController.getEncodedImageFile();
        verify(mockSsdvEncoder).encode(anyString(), eq(0), any(File.class), anyString());
        ssdvEncoderController.getEncodedImageFile();
        verify(mockSsdvEncoder).encode(anyString(), eq(1), any(File.class), anyString());
        ssdvEncoderController.getEncodedImageFile();
        verify(mockSsdvEncoder).encode(anyString(), eq(2), any(File.class), anyString());
    }
    
    @Test
    public void getEncodedImageFile_shouldReturnImageFileIfSingleEncodedImageFound() {       
        File encodedImage = ssdvEncoderController.getEncodedImageFile();
        assertEquals(mockEncodedImage, encodedImage);
    }
    
    @Test
    public void getEncodedImageFile_shouldReturnNullIfMultipleEncodedImagesFound() {  
        when(mockEncodedImagesDirectory.listFiles()).thenReturn(new File[2]);
        File encodedImage = ssdvEncoderController.getEncodedImageFile();
        assertNull(encodedImage);
    }
    
    @Test
    public void getEncodedImageFile_shouldReturnNullIfNoEncodedImagesFound() { 
        when(mockEncodedImagesDirectory.listFiles()).thenReturn(new File[0]);
        File encodedImage = ssdvEncoderController.getEncodedImageFile();
        assertNull(encodedImage);
    }   
}
