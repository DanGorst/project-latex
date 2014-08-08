/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dgorst
 */
public class HttpDataWriterTest {
    
    private HttpDataWriter writer;
    private List<String> dataKeys;
    
    public HttpDataWriterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.dataKeys = new ArrayList<>();
        this.writer = new HttpDataWriter(dataKeys, new DataModelConverter(), null);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getJsonStringFromRawData method, of class HttpDataWriter.
     */
    @Test
    public void testGetJsonStringFromRawData()  {
        String actual = writer.getJsonStringFromRawData("testing");
        
        byte[] encodedBytes = Base64.encodeBase64("testing".getBytes());
        Map<String, Object> data = new HashMap<>();
        data.put("_raw", new String(encodedBytes));
        
        Map<String, Object> body = new HashMap<>();
        body.put("data", data);
        Gson gson = new Gson();
        String expected = gson.toJson(body);
        
        assertEquals(expected, actual);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetJsonStringThrowsIfRawDataIsNull() {
        writer.getJsonStringFromRawData(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSendPostRequestThrowsIfRawDataIsNull() throws IOException {
        writer.sendPostRequest(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWriteDataThrowsIfDataIsNull() {
        writer.writeData(null);
    }
}
