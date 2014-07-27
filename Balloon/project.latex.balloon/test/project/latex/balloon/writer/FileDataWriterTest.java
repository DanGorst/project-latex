/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import project.latex.SensorData;
import project.latex.writer.DataWriteFailedException;

/**
 *
 * @author Dan
 */
public class FileDataWriterTest {
    
    private FileDataWriter writer;
    private SensorFileLoggerService mockService;
    
    public FileDataWriterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        mockService = mock(SensorFileLoggerService.class);
        writer = new FileDataWriter(mockService);
    }
    
    @After
    public void tearDown() {
        writer = null;
    }

    /**
     * Test of writeData method, of class FileDataWriter.
     * @throws project.latex.writer.DataWriteFailedException
     */
    @Test(expected = DataWriteFailedException.class)
    public void testWriteDataThrowsMeaningfulExceptionIfDataIsNull() throws DataWriteFailedException {
        writer.writeData(null);
    }
    
    @Test(expected = DataWriteFailedException.class)
    public void testWriteDataThrowsMeaningfulExceptionIfLoggerIsNull() throws DataWriteFailedException  {
        when(mockService.getLoggerForSensor("Test")).thenReturn(null);
        SensorData data = new SensorData("Test", new Date(), new HashMap<String, Object>());
        writer.writeData(data);
    }
    
    @Test
    public void testHeadersAreWrittenOnceBeforeData()   {
        try {
            Logger mockLogger = mock(Logger.class);
            when(mockService.getLoggerForSensor("Test")).thenReturn(mockLogger);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("Value", 5);
            Date sensorDate = new Date();
            SensorData data = new SensorData("Test", sensorDate, dataMap);
            writer.writeData(data);
            writer.writeData(data);
            
            verify(mockLogger).info("Date,Value");
            verify(mockLogger, times(2)).info(sensorDate.toString() + ",5");
        } catch (DataWriteFailedException ex) {
            fail(ex.getMessage());
        }
    }
}
