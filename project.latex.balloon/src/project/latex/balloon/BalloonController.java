/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import project.latex.balloon.ssdv.SsdvController;
import project.latex.balloon.writer.DataWriter;

/**
 *
 * @author Dan
 */
public class BalloonController {

    private static final Logger logger = Logger.getLogger(BalloonController.class);

    private final String payloadName = "$$latex";

    // Sensors to determine the current state of the balloon
    private List<SensorController> sensors;

    private List<DataModelConsumer> dataModelConsumers;

    private List<DataWriter> dataWriters;

    // Camera
    private CameraSensorController cameraSensor;

    private SentenceIdGenerator sentenceIdGenerator;

    private ControllerRunner controllerRunner;

    // Ssdv
    private SsdvController ssdvController;
    
    // Required properties
    private String timeKey;
    private String dateKey;
    private String payloadNameKey;
    private String sentenceIdKey;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("logger.properties");
        logger.info("Project Latex Balloon Controller, version 0.1");

        String configFile = args[0];

        ApplicationContext context = new FileSystemXmlApplicationContext(configFile);
        BalloonController balloonController = (BalloonController) context.getBean("balloonController");
        logger.info("Balloon created");

        balloonController.run();
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
    
    public SsdvController getSsdvController() {
        return ssdvController;
    }

    public SentenceIdGenerator getSentenceIdGenerator() {
        return sentenceIdGenerator;
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
    
    public void setSsdvController(SsdvController ssdvController) {
        this.ssdvController = ssdvController;
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

    public void setControllerRunner(ControllerRunner controllerRunner) {
        this.controllerRunner = controllerRunner;
    }

    void run() {
        if (controllerRunner == null) {
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

        while (controllerRunner.shouldKeepRunning()) {
            switch (controllerRunner.getCurrentRunLoop()) {
                case SensorDataRunLoop:
                    processSensorData();
                    break;
                case SsdvRunLoop:
                    if (ssdvController != null) {
                        ssdvController.sendNextPacket();                    
                    }
                    controllerRunner.controllerFinishedRunLoop(null);
            }
        }
    }
    
    private void processSensorData() {
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

        // Handle any new camera images which are available
        if (this.cameraSensor != null) {
            cameraSensor.handleNewImages();
        }

        controllerRunner.controllerFinishedRunLoop(data);
    }
}
