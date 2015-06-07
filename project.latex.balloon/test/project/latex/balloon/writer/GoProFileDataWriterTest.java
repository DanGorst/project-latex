/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Dan
 */
public class GoProFileDataWriterTest {

    private GoProFileDataWriter goProFileDataWriter;

    private final String dataFolder = "testData";

    @Before
    public void setUp() {
        goProFileDataWriter = new GoProFileDataWriter(dataFolder);
    }

    @After
    public void tearDown() {
        // Clean up the test data folder
        File folder = new File(dataFolder);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            folder.delete();
        }
    }

    @Test
    public void testWriteData() throws SAXException, ParserConfigurationException, IOException {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(GoProFileDataWriter.ALTITUDE_KEY, 500.0);
        dataModel.put(GoProFileDataWriter.LATITUDE_KEY, 1.23);
        dataModel.put(GoProFileDataWriter.LONGITUDE_KEY, 4.56);
        dataModel.put(GoProFileDataWriter.SENTENCE_ID, "test1");
        goProFileDataWriter.writeData(dataModel);

        File expectedFile = new File(dataFolder + File.separator + "test1.xml");
        Assert.assertTrue(expectedFile.exists());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(expectedFile);
        Element dataElement = doc.getDocumentElement();
        dataElement.normalize();
        assertEquals("data", dataElement.getNodeName());

        Element altitudeElement = (Element) doc.getElementsByTagName(GoProFileDataWriter.ALTITUDE_KEY).item(0);
        assertNotNull(altitudeElement);
        assertEquals("500.0", altitudeElement.getTextContent());

        Element latitudeElement = (Element) doc.getElementsByTagName(GoProFileDataWriter.LATITUDE_KEY).item(0);
        assertNotNull(latitudeElement);
        assertEquals("1.23", latitudeElement.getTextContent());

        Element longitudeElement = (Element) doc.getElementsByTagName(GoProFileDataWriter.LONGITUDE_KEY).item(0);
        assertNotNull(longitudeElement);
        assertEquals("4.56", longitudeElement.getTextContent());
    }

    /**
     * It's not ideal, but it's probably better that we write something out to
     * file rather than throwing an exception.
     */
    @Test
    public void testThatDataIsWrittenToFileCalledNullIfModelDoesntContainSentenceId() {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(GoProFileDataWriter.ALTITUDE_KEY, 500.0);
        dataModel.put(GoProFileDataWriter.LATITUDE_KEY, 1.23);
        dataModel.put(GoProFileDataWriter.LONGITUDE_KEY, 4.56);
        goProFileDataWriter.writeData(dataModel);

        File expectedFile = new File(dataFolder + File.separator + "null.xml");
        Assert.assertTrue(expectedFile.exists());
    }

    @Test
    public void testThatNullStringsAreWrittenIfDataIsntInModel() throws ParserConfigurationException, SAXException, IOException {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(GoProFileDataWriter.SENTENCE_ID, "test1");
        goProFileDataWriter.writeData(dataModel);

        File expectedFile = new File(dataFolder + File.separator + "test1.xml");
        Assert.assertTrue(expectedFile.exists());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(expectedFile);
        Element dataElement = doc.getDocumentElement();
        dataElement.normalize();
        assertEquals("data", dataElement.getNodeName());

        Element altitudeElement = (Element) doc.getElementsByTagName(GoProFileDataWriter.ALTITUDE_KEY).item(0);
        assertNotNull(altitudeElement);
        assertEquals("null", altitudeElement.getTextContent());

        Element latitudeElement = (Element) doc.getElementsByTagName(GoProFileDataWriter.LATITUDE_KEY).item(0);
        assertNotNull(latitudeElement);
        assertEquals("null", latitudeElement.getTextContent());

        Element longitudeElement = (Element) doc.getElementsByTagName(GoProFileDataWriter.LONGITUDE_KEY).item(0);
        assertNotNull(longitudeElement);
        assertEquals("null", longitudeElement.getTextContent());
    }
}
