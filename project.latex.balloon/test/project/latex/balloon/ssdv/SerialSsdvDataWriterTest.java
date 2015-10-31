/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.ssdv;

import com.pi4j.io.serial.Serial;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author will
 */
public class SerialSsdvDataWriterTest {
    
    Serial mockSerial;
    SerialSsdvDataWriter writer;
    
    @Before
    public void setUp() {
        int baudRate = 1200;
        mockSerial = mock(Serial.class);
        writer = new SerialSsdvDataWriter(baudRate, mockSerial);
    }
    
    @Test
    public void WriteDataShouldWrite256BytePacketAs16Chunks() throws InterruptedException {
        byte[] packet = new byte[256];
        writer.start();
        writer.writeData(packet);
        Thread.sleep(2000);
        verify(mockSerial, times(16)).write(any(byte[].class));
    }
    
    @Test
    public void WriteDataShouldWriteAChunkEvery107MillisecondsForBaudRate1200() throws InterruptedException {
        // ceil(dataChunkSizeInBits/baudRate) = ceil(128/1200) = 107
        int delayBetweenDataChunks = 107;
        byte[] packet = new byte[256];
        writer.start();
        writer.writeData(packet);
        Thread.sleep(50);
        verify(mockSerial, atMost(1)).write(any(byte[].class));
        Thread.sleep(107);
        verify(mockSerial, atLeast(1)).write(any(byte[].class));
        verify(mockSerial, atMost(2)).write(any(byte[].class));
        Thread.sleep(428);
        verify(mockSerial, atLeast(5)).write(any(byte[].class));
        verify(mockSerial, atMost(6)).write(any(byte[].class));
        Thread.sleep(428);
        verify(mockSerial, atLeast(9)).write(any(byte[].class));
        verify(mockSerial, atMost(10)).write(any(byte[].class));
        Thread.sleep(642);
        verify(mockSerial, atLeast(15)).write(any(byte[].class));
        verify(mockSerial, atMost(16)).write(any(byte[].class));
    }
    
    @Test
    public void WriteDataShouldDoNothingIfNoDataToSend() throws InterruptedException {
        byte[] packet = new byte[0];
        writer.start();
        writer.writeData(packet);
        Thread.sleep(500);
        verify(mockSerial, times(0)).write(any(byte[].class));
    }
}
