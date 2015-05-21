/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import com.pi4j.io.serial.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import project.latex.balloon.TransmittedDataKeysResource;

/**
 *
 * @author Dan
 */
public class SerialDataWriterTest {

    private SerialDataWriter writer;
    private Serial mockSerial;
    private List<String> dataKeys;
    private TransmittedDataKeysResource mockTransmittedDataKeysResource;
    private ChecksumGenerator mockChecksumGenerator;

    @Before
    public void setUp() {
        mockSerial = mock(Serial.class);
        dataKeys = new ArrayList<>();
        dataKeys.add("Test");
        dataKeys.add("Test2");
        mockTransmittedDataKeysResource = mock(TransmittedDataKeysResource.class);
        when(mockTransmittedDataKeysResource.getTransmittedDataKeys()).thenReturn(dataKeys);
        mockChecksumGenerator = mock(ChecksumGenerator.class);
        when(mockChecksumGenerator.generateChecksum(Matchers.anyString())).thenReturn("XX");
        writer = new SerialDataWriter(mockTransmittedDataKeysResource, new DataModelConverter(mockChecksumGenerator), mockSerial, 50);
    }

    @Test(expected = UnsatisfiedLinkError.class)
    public void testConstructorThrowsIfSerialPortCantOpen() {
        doThrow(UnsatisfiedLinkError.class).when(mockSerial).open(Serial.DEFAULT_COM_PORT, 50);
        writer = new SerialDataWriter(mockTransmittedDataKeysResource, new DataModelConverter(mockChecksumGenerator), mockSerial, 50);
    }

    /**
     * Test of writeData method, of class SerialDataWriter.
     */
    @Test
    public void testWriteValidData() {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("Test", "DataString");
        dataModel.put("Test2", "HelloWorld");
        writer.writeData(dataModel);
        verify(mockSerial).writeln("DataString,HelloWorld*XX");
    }

    @Test
    public void testWriteDataWhichDoesntMatchKeys() {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("Invalid", "DataString");
        dataModel.put("Invalid2", "HelloWorld");
        writer.writeData(dataModel);
        verify(mockSerial).writeln("99.99,99.99*XX");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteDataThrowsIfDataModelIsNull() {
        writer.writeData(null);
    }
}
