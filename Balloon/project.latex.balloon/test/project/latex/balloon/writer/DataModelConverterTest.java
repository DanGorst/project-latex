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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dgorst
 */
public class DataModelConverterTest {
    
    public DataModelConverterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of convertDataKeysToCsvString method, of class DataModelConverter.
     */
    @Test
    public void testConvertDataKeysToCsvString() {
        List<String> dataKeys = new ArrayList<>();
        dataKeys.add("First");
        dataKeys.add("Second");
        DataModelConverter instance = new DataModelConverter();
        String expResult = "First,Second";
        String result = instance.convertDataKeysToCsvString(dataKeys);
        assertEquals(expResult, result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertDataKeysThrowsIfPassedNullString()   {
        DataModelConverter converter = new DataModelConverter();
        converter.convertDataKeysToCsvString(null);
    }
    
    @Test
    public void testConvertDataKeysReturnsEmptyStringIfPassedEmptyList()    {
        DataModelConverter converter = new DataModelConverter();
        String actual = converter.convertDataKeysToCsvString(new ArrayList<String>());
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
        
        DataModelConverter instance = new DataModelConverter();
        String expResult = "1.23,0.34";
        String result = instance.convertDataToCsvString(dataKeys, data);
        assertEquals(expResult, result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertDataToCsvStringThrowsIfPassedNullDataKeys()  {
        DataModelConverter converter = new DataModelConverter();
        converter.convertDataToCsvString(null, new HashMap<String, Object>());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConvertDataToCsvStringThrowsIfPassedNullData()  {
        DataModelConverter converter = new DataModelConverter();
        List<String> dataKeys = new ArrayList<>();
        dataKeys.add("First");
        dataKeys.add("Second");
        
        converter.convertDataToCsvString(dataKeys, null);
    }
    
    @Test
    public void testConvertDataToCsvStringReturnsEmptyStringIfPassedEmptyKeysArray()    {
        DataModelConverter converter = new DataModelConverter();
        List<String> dataKeys = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("Second", 0.34);
        data.put("First", 1.23);
        String actual = converter.convertDataToCsvString(dataKeys, data);
        assertEquals("", actual);
    }
    
    @Test
    public void testConvertDataToCsvStringReturnsNullStringsIfDataDoesntMatchKeys() {
        DataModelConverter converter = new DataModelConverter();
        List<String> dataKeys = new ArrayList<>();
        dataKeys.add("First");
        dataKeys.add("Second");
        
        Map<String, Object> data = new HashMap<>();
        data.put("X", 0.34);
        data.put("Y", 1.23);
        String actual = converter.convertDataToCsvString(dataKeys, data);
        assertEquals("null,null", actual);
    }
}
