/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import java.io.File;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import project.latex.balloon.BalloonController;

/**
 *
 * @author Dan
 */
public class GoProFileDataWriter implements DataWriter {

    private static final Logger logger = Logger.getLogger(BalloonController.class);

    static final String LATITUDE_KEY = "latitude";

    static final String LONGITUDE_KEY = "longitude";

    static final String ALTITUDE_KEY = "altitude";

    static final String SENTENCE_ID = "sentence_id";

    private final String dataFolder;

    public GoProFileDataWriter(String dataFolder) {
        this.dataFolder = dataFolder;
        File dataDirectory = new File(this.dataFolder);
        if (!dataDirectory.exists()) {
            logger.info("Creating data directory: " + this.dataFolder);
            dataDirectory.mkdir();
        }
    }

    @Override
    public void writeData(Map<String, Object> dataModel) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("data");
            doc.appendChild(rootElement);

            Element latitude = doc.createElement(LATITUDE_KEY);
            latitude.appendChild(doc.createTextNode(String.valueOf(dataModel.get(LATITUDE_KEY))));
            rootElement.appendChild(latitude);

            Element longitude = doc.createElement(LONGITUDE_KEY);
            longitude.appendChild(doc.createTextNode(String.valueOf(dataModel.get(LONGITUDE_KEY))));
            rootElement.appendChild(longitude);

            Element altitude = doc.createElement(ALTITUDE_KEY);
            altitude.appendChild(doc.createTextNode(String.valueOf(dataModel.get(ALTITUDE_KEY))));
            rootElement.appendChild(altitude);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            String fileName = String.valueOf(dataModel.get(SENTENCE_ID));
            String filePath = dataFolder + File.separator + fileName + ".xml";
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        } catch (TransformerException | ParserConfigurationException ex) {
            logger.error(ex.getMessage());
        }
    }

}
