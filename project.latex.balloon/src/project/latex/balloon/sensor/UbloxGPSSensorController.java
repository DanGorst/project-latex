/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 */
public class UbloxGPSSensorController implements GPSSensorController, AltimeterSensorController {

    private static final Logger logger = Logger.getLogger(UbloxGPSSensorController.class);
    private Serial serial;
    private String latitudeKey;
    private String longitudeKey;
    private String altitudeKey;
    private double latitude;
    private double longitude;
    private double altitude;

    public UbloxGPSSensorController(String latitudeKey, String longitudeKey, String altitudeKey) {
        this.latitudeKey = latitudeKey;
        this.longitudeKey = longitudeKey;
        this.altitudeKey = altitudeKey;
        serial = SerialFactory.createInstance();
        try {
            serial.open(Serial.DEFAULT_COM_PORT, 9600);
        } catch (SerialPortException ex) {
            logger.error("Failed to open serial port.");
        }

    }

    @Override
    public HashMap<String, Object> getCurrentData() {
 
        HashMap<String, Object> data = new HashMap<>();
        // What we read from the serial port is an NMEA sentence, see:
        // http://www.gpsinformation.org/dale/nmea.htm#GSA
        String[] currentNmeaSentence;

        do {
            currentNmeaSentence = serialReadLine().split(",", -1);
        } // GPGGA sentences hold the data that we are interested in.
        while (!currentNmeaSentence[0].substring(2).equals("GPGGA"));

        // Extract latitude, longitude, altitude and timestamp data from 
        // the GPGGA sentence and parse lat/long from Nmea to decimal format.
        try {
            latitude = Double.parseDouble(currentNmeaSentence[2]);
            longitude = Double.parseDouble(currentNmeaSentence[4]);
            altitude = Double.parseDouble(currentNmeaSentence[9]);
        } catch (NumberFormatException ex) {
            logger.error("Error converting String to double.");
        }

        data.put(latitudeKey, this.latitude);
        data.put(longitudeKey, this.longitude);
        data.put(altitudeKey, this.altitude);
 
        return data;
    }

    // pi4j Serial object has no readLine method, so we write one.
    public String serialReadLine() {

        String sentence = "";
        char currentChar;

        try {
            // Find the start of a new line and move to its first character.
            while (serial.read() != Character.LINE_SEPARATOR) {
                serial.read();
            }
            currentChar = serial.read();

            // Create a String of all characters until the end of the line.
            while (currentChar != Character.LINE_SEPARATOR) {
                sentence += currentChar;
                currentChar = serial.read();
            }
        } catch (IllegalStateException ex) {
            logger.error("Failed to write to serial port.");
        }

        return sentence;
    }
    
    public double NmeaToDecimal(double position, String bearing) {
        double decimal = 0;
        //TODO
        return decimal;
    }

    public void close() {
        if (serial.isOpen()) {
            try {
                serial.close();
            } catch (IllegalStateException ex) {
                logger.error("Failed to close serial port.");
            }
        }
    }

    @Override
    public double getLatitude() {
        getCurrentData();
        return latitude;
    }

    @Override
    public double getLongitude() {
        getCurrentData();
        return longitude;
    }

    @Override
    public double getHeight() {
        getCurrentData();
        return altitude;
    }
}
