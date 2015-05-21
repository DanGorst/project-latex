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
import org.mockito.Matchers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author dgorst
 */
public class DataModelConverterTest {
    
    private ChecksumGenerator mockChecksumGenerator;
    
    private DataModelConverter dataModelConverter;
    
    @Before
    public void setUp() {
        mockChecksumGenerator = mock(ChecksumGenerator.class);
        when(mockChecksumGenerator.generateChecksum(Matchers.anyString())).thenReturn("XX");
        dataModelConverter = new DataModelConverter(mockChecksumGenerator);
    }

    /**
     * Test of convertDataKeysToCsvString method, of class DataModelConverter.
     */
    @Test
    public void testConvertDataKeysToCsvString() {
        List<String> dataKeys = new ArrayList<>();
        dataKeys.add("First");
        dataKeys.add("Second");
        String expResult = "First,Second";
        String result = dataModelConverter.convertDataKeysToCsvString(dataKeys);
        assertEquals(expResult, result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertDataKeysThrowsIfPassedNullString()   {
        dataModelConverter.convertDataKeysToCsvString(null);
    }
    
    @Test
    public void testConvertDataKeysReturnsEmptyStringIfPassedEmptyList()    {
        String actual = dataModelConverter.convertDataKeysToCsvString(new ArrayList<String>());
        assertEquals("", actual);
    }

    /**
     * Test of convertDataToCsvString method, of class DataModelConverter.
     */
    @Test
    public void testConvertDataToCsvString() {
        List<String> dataKeys = new ArrayList<>();
        dataKeys.add("First");
        dataKeys.add("Second");
        
        Map<String, Object> data = new HashMap<>();
        data.put("Second", 0.34);
        data.put("First", 1.23);
        
        String expResult = "1.23,0.34*XX";
        String result = dataModelConverter.convertDataToCsvString(dataKeys, data);
        assertEquals(expResult, result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertDataToCsvStringThrowsIfPassedNullDataKeys()  {
        dataModelConverter.convertDataToCsvString(null, new HashMap<String, Object>());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertDataToCsvStringThrowsIfPassedNullData()  {
        List<String> dataKeys = new ArrayList<>();
        dataKeys.add("First");
        dataKeys.add("Second");
        
        dataModelConverter.convertDataToCsvString(dataKeys, null);
    }
    
    @Test
    public void testConvertDataToCsvStringReturnsEmptyStringIfPassedEmptyKeysArray()    {
        List<String> dataKeys = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("Second", 0.34);
        data.put("First", 1.23);
        String actual = dataModelConverter.convertDataToCsvString(dataKeys, data);
        assertEquals("", actual);
    }
    
    @Test
    public void testConvertDataToCsvStringReturnsNullStringsIfDataDoesntMatchKeys() {
        List<String> dataKeys = new ArrayList<>();
        dataKeys.add("First");
        dataKeys.add("Second");
        
        Map<String, Object> data = new HashMap<>();
        data.put("X", 0.34);
        data.put("Y", 1.23);
        String actual = dataModelConverter.convertDataToCsvString(dataKeys, data);
        assertEquals("99.99,99.99*XX", actual);
    }
}
