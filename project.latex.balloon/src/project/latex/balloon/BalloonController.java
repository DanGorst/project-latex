/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import project.latex.balloon.consumer.DataModelConsumer;
import project.latex.balloon.sensor.CameraSensorController;
import project.latex.balloon.sensor.SensorController;
import project.latex.balloon.sensor.SensorReadFailedException;
import project.latex.balloon.writer.DataModelConverter;
import project.latex.balloon.writer.CameraDataWriter;
import project.latex.balloon.writer.CameraFileWriter;
import project.latex.balloon.writer.DataWriteFailedException;
import project.latex.balloon.writer.DataWriter;

/**
 *
 * @author Dan
 */
public class BalloonController {

    private static final Logger logger = Logger.getLogger(BalloonController.class);

    private final String payloadName = "$$latex";
    
    private List<String> transmittedTelemetryKeys;
    private DataModelConverter converter;

    // Sensors to determine the current state of the balloon
    private List<SensorController> sensors;

    private List<DataModelConsumer> dataModelConsumers;

    private List<DataWriter> dataWriters;

    // Camera
    private CameraSensorController cameraSensor;
    private CameraDataWriter cameraWriter;

    private SentenceIdGenerator sentenceIdGenerator;
    
    // Required properties
    private String timeKey;
    private String dateKey;
    private String payloadNameKey;
    private String sentenceIdKey;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("logger.properties");

            logger.info("Project Latex Balloon Controller, version 0.1");

            List<String> transmittedDataKeys = loadTransmittedDataKeys("../telemetryKeys.json");
            File dataFolder = createDataFolder();

            ApplicationContext context = new FileSystemXmlApplicationContext("beans.xml");
            CameraFileWriter cameraFileWriter = (CameraFileWriter) context.getBean("cameraWriter");
            cameraFileWriter.setBaseFolder(dataFolder);
            // TODO: Set the data keys on the serial data writer

            BalloonController balloonController = (BalloonController) context.getBean("balloonController");
            logger.info("Balloon created");
            balloonController.run(new DefaultControllerRunner());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    static File createDataFolder() throws IOException {
        // We create a new folder for each flight that the balloon makes. All of our sensor data for the 
        // flight is then put into that folder
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        Date date = new Date();
        String baseUrl = "data" + File.separator + "Flight starting - " + dateFormat.format(date);
        File dataFolder = new File(baseUrl);
        if (!dataFolder.mkdirs()) {
            throw new IOException("Unable to create directory to contain sensor data logs");
        }
        return dataFolder;
    }

    static List<String> loadTransmittedDataKeys(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Cannot load keys from null file");
        }

        JsonReader reader = null;
        try {
            List<String> dataKeys = new ArrayList<>();
            reader = new JsonReader(new FileReader(filePath));
            reader.beginObject();
            while (reader.hasNext()) {
                reader.nextName();
                reader.beginArray();
                while (reader.hasNext()) {
                    dataKeys.add(reader.nextString());
                }
                reader.endArray();
            }
            reader.endObject();
            reader.close();

            return dataKeys;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public List<String> getTransmittedTelemetryKeys() {
        return transmittedTelemetryKeys;
    }

    public DataModelConverter getConverter() {
        return converter;
    }

    public String getPayloadName() {
        return payloadName;
    }

    public List<SensorController> getSensors() {
        return sensors;
    }

    public List<DataWriter> getDataWriters() {
        return dataWriters;
    }

    public List<DataModelConsumer> getDataModelConsumers() {
        return dataModelConsumers;
    }

    public CameraSensorController getCameraSensor() {
        return cameraSensor;
    }

    public CameraDataWriter getCameraWriter() {
        return cameraWriter;
    }

    public SentenceIdGenerator getSentenceIdGenerator() {
        return sentenceIdGenerator;
    }

    public void setTransmittedTelemetryKeys(List<String> transmittedTelemetryKeys) {
        this.transmittedTelemetryKeys = transmittedTelemetryKeys;
    }

    public void setConverter(DataModelConverter converter) {
        this.converter = converter;
    }

    public void setSensors(List<SensorController> sensors) {
        this.sensors = sensors;
    }

    public void setDataModelConsumers(List<DataModelConsumer> dataModelConsumers) {
        this.dataModelConsumers = dataModelConsumers;
    }

    public void setDataWriters(List<DataWriter> dataWriters) {
        this.dataWriters = dataWriters;
    }

    public void setCameraSensor(CameraSensorController cameraSensor) {
        this.cameraSensor = cameraSensor;
    }

    public void setCameraWriter(CameraDataWriter cameraWriter) {
        this.cameraWriter = cameraWriter;
    }

    public void setSentenceIdGenerator(SentenceIdGenerator sentenceIdGenerator) {
        this.sentenceIdGenerator = sentenceIdGenerator;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }

    public void setDateKey(String dateKey) {
        this.dateKey = dateKey;
    }

    public void setPayloadNameKey(String payloadNameKey) {
        this.payloadNameKey = payloadNameKey;
    }

    public void setSentenceIdKey(String sentenceIdKey) {
        this.sentenceIdKey = sentenceIdKey;
    }

    void run(ControllerRunner runner) {
        if (runner == null) {
            throw new IllegalArgumentException("Cannot run with null ControllerRunner");
        }

        if (timeKey == null) {
            throw new IllegalArgumentException("Null time key specified");
        }
        if (dateKey == null) {
            throw new IllegalArgumentException("Null date key specified");
        }
        if (payloadNameKey == null) {
            throw new IllegalArgumentException("Null payload name key specified");
        }
        if (sentenceIdKey == null) {
            throw new IllegalArgumentException("Null sentence id key specified");
        }

        while (runner.shouldKeepRunning()) {
            // Build up a model of the current balloon state from the sensors
            Map<String, Object> data = new HashMap<>();

            // Add entries for date and time. If the GPS module is running, that will 
            // override these values when we read data from it.
            // For now we put the date into the same timeFormat as the Icarus test data, 
            // as this means we don't need to change the receiver to be able to handle both
            // sets of data
            Date now = new Date();
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            data.put(timeKey, timeFormat.format(now));
            DateFormat dateFormat = new SimpleDateFormat("ddMMYY");
            data.put(dateKey, dateFormat.format(now));

            data.put(payloadNameKey, this.payloadName);
            data.put(sentenceIdKey, this.sentenceIdGenerator.generateId());

            // Get readings from each of our sensors.
            for (SensorController controller : this.sensors) {
                try {
                    Map<String, Object> sensorData = controller.getCurrentData();
                    for (String key : sensorData.keySet()) {
                        data.put(key, sensorData.get(key));
                    }
                } catch (SensorReadFailedException ex) {
                    logger.error(ex);
                }
            }

            // Allow our consumers to consume the data model
            for (DataModelConsumer dataModelConsumer : this.dataModelConsumers) {
                dataModelConsumer.consumeDataModel(data);
            }

            // Write the model
            for (DataWriter dataWriter : this.dataWriters) {
                try {
                    dataWriter.writeData(data);
                } // If we get some kind of exception, let's catch it here rather than just crashing the app
                catch (Exception e) {
                    logger.error(e);
                }
            }

            // Find any new camera images and write them out
            if (this.cameraSensor != null) {
                List<String> imageFiles = this.cameraSensor.getImageFileNames();
                try {
                    this.cameraWriter.writeImageFiles(imageFiles);
                } catch (DataWriteFailedException ex) {
                    logger.error("Failed to write image files", ex);
                }
            }

            runner.controllerFinishedRunLoop(data);
        }
    }
}
