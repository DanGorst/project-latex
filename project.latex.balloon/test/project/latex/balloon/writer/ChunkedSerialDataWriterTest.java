/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author dgorst
 */
public class ChunkedSerialDataWriterTest {

    private ChunkedSerialDataWriter chunkedSerialDataWriter;
    
    private SerialDataWriter mockDataWriter;
    
    @Before
    public void setUp() {
        mockDataWriter = mock(SerialDataWriter.class);
        chunkedSerialDataWriter = new ChunkedSerialDataWriter(5, 15000, mockDataWriter);
    }

    /**
     * Test of breakDataIntoChunks method, of class ChunkedSerialDataWriter.
     */
    @Test
    public void testBreakDataIntoChunks() {
        String data = "abcdef1234\n";
        List<String> expResult = new ArrayList<>();
        expResult.add("abcde");
        expResult.add("f1234");
        expResult.add("\n");
        List<String> result = chunkedSerialDataWriter.breakDataIntoChunks(data);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testBreakEmptyDataIntoChunks() {
        String data = "";
        List<String> expResult = new ArrayList<>();
        List<String> result = chunkedSerialDataWriter.breakDataIntoChunks(data);
        assertEquals(expResult, result);
    }

    /**
     * Test that we add chunks to our collection when data is passed to us
     */
    @Test
    public void testWriteData() {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("value1", 2.34567);
        dataModel.put("value2", "test");
        when(mockDataWriter.convertDataToCsvString(dataModel)).thenReturn("2.34567, test");
        chunkedSerialDataWriter.writeData(dataModel);
        List<String> expResult = new ArrayList<>();
        expResult.add("2.345");
        expResult.add("67, t");
        expResult.add("est\n");
        assertEquals(expResult, chunkedSerialDataWriter.getChunks());
    }
    
    @Test
    public void testWriteNextChunkRemovesItFromOurCollection() {
        chunkedSerialDataWriter.addChunk("12345");
        assertFalse(chunkedSerialDataWriter.getChunks().isEmpty());
        chunkedSerialDataWriter.writeNextChunk();
        assertTrue(chunkedSerialDataWriter.getChunks().isEmpty());
        verify(mockDataWriter).writeString("12345");
    }
    
    @Test
    public void testWriteNextChunkDoesNothingIfChunksIsEmpty() {
        assertTrue(chunkedSerialDataWriter.getChunks().isEmpty());
        chunkedSerialDataWriter.writeNextChunk();
        verify(mockDataWriter, never()).writeString(anyString());
    }
}
