/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author will
 */
public class SsdvControllerImpleTest {
    
    SsdvController ssdvController;
    SsdvEncoderController encoder;
    SsdvDataWriter writer;
    
    @Before
    public void setUp() {
        encoder = mock(SsdvEncoderController.class);
        writer = mock(SsdvDataWriter.class);
        ssdvController = new SsdvController(encoder, writer);
    }
    
    @Test
    public void testSendNextPacketDoesNotWritePacketIfNoEncodedImageAvailable() {
        when(encoder.getEncodedImageFile()).thenReturn(null);
        ssdvController.sendNextPacket();
        verify(writer, never()).writePacket(any(String.class), any(int.class));     
    }
    
    @Test
    public void testSendNextPacketWritesPacketIfEncodedImageAvailable() {
        String testImageFilePath = SsdvControllerImpleTest.class.getResource("testFile").getFile();
        File mockEncodedImage = new File(testImageFilePath);
        when(encoder.getEncodedImageFile()).thenReturn(mockEncodedImage);
        ssdvController.sendNextPacket();
        verify(writer).writePacket(mockEncodedImage.getAbsolutePath(), 0);
    }
    
    @Test
    public void testSendNextPacketWritesAllPacketsBeforeGettingNextEncodedImage() {
        String testImageFilePath = SsdvControllerImpleTest.class.getResource("testFile").getFile();
        // File contains 2 packets worth of data (512 bytes)
        File mockEncodedImage = new File(testImageFilePath);
        when(encoder.getEncodedImageFile()).thenReturn(mockEncodedImage);
        ssdvController.sendNextPacket();
        verify(encoder).getEncodedImageFile();
        verify(writer).writePacket(mockEncodedImage.getAbsolutePath(), 0);        
        ssdvController.sendNextPacket();
        verify(writer).writePacket(mockEncodedImage.getAbsolutePath(), 1);
        verify(encoder).getEncodedImageFile();
        ssdvController.sendNextPacket();
        verify(writer, times(2)).writePacket(mockEncodedImage.getAbsolutePath(), 0);
        verify(encoder, times(2)).getEncodedImageFile();       
    }
}
