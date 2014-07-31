/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import project.latex.writer.DataWriteFailedException;

/**
 *
 * @author Dan
 */
public class FileDataWriterTest {
    
    private FileDataWriter writer;
    private List<String> dataKeys;
    private DataModelConverter converter;
    
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
        dataKeys = new ArrayList<>();
        dataKeys.add("Date");
        dataKeys.add("Value");
        converter = new DataModelConverter();
        writer = new FileDataWriter("", dataKeys, converter);
    }
    
    @After
    public void tearDown() {
        File savedFile = new File(writer.fileName);
        if (savedFile.exists()) {
            savedFile.delete();
        }
        writer = null;
        dataKeys = null;
    }

    /**
     * Test of writeData method, of class FileDataWriter.
     * @throws project.latex.writer.DataWriteFailedException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWriteDataThrowsMeaningfulExceptionIfDataIsNull() throws DataWriteFailedException {
        writer.writeData(null);
    }
    
    @Test
    public void testHeadersAreWrittenOnceBeforeData()   {
        try {
            Logger mockLogger = mock(Logger.class);
         
            writer = new FileDataWriter("", dataKeys, converter, mockLogger);
            
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("Value", 5);
            Date modelDate = new Date();
            dataMap.put("Date", modelDate);
            
            writer.writeData(dataMap);
            writer.writeData(dataMap);
            
            verify(mockLogger).info("Date,Value");
            verify(mockLogger, times(2)).info(modelDate.toString() + ",5");
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}
