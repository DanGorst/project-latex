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
    }
    
    @Override
    public HashMap<String, Object> getCurrentData() {

        HashMap<String, Object> data = new HashMap<>();

        readSensorValues();

        data.put(latitudeKey, this.latitude);
        data.put(longitudeKey, this.longitude);
        data.put(altitudeKey, this.altitude);

        return data;
    }

    private void readSensorValues() {
        // What we read from the serial port is an NMEA sentence, see:
        // http://www.gpsinformation.org/dale/nmea.htm#GSA
        String[] currentNmeaSentence;

        // Keep reading the serial port until we get a GPGGA sentence.
        do {
            currentNmeaSentence = serialReadLine().split(",", -1);
        } while (!currentNmeaSentence[0].substring(2).equals("GPGGA"));

        // Extract latitude, longitude, altitude and timestamp data from 
        // the GPGGA sentence and parse lat/long from NMEA to decimal format.
        latitude = parseNmeaCoordinateToDecimal(currentNmeaSentence[2], currentNmeaSentence[3]);
        longitude = parseNmeaCoordinateToDecimal(currentNmeaSentence[4], currentNmeaSentence[5]);
        try {
            altitude = Double.parseDouble(currentNmeaSentence[9]);
        } catch (NumberFormatException ex) {
            logger.error("Failed to parse altitude to double");
        }
    }

    // pi4j Serial object has no readLine method, so we write one.
    public String serialReadLine() {

        String sentence = "";
        char currentChar;

        try {
            serial.open(Serial.DEFAULT_COM_PORT, 9600);
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
        } catch (SerialPortException ex) {
            logger.error("Failed to open serial port.");
        } finally {
            try {
                serial.close();
            } catch (IllegalStateException ex) {
                logger.error("Failed to close serial port.");
            }
        }

        return sentence;
    }

    public double parseNmeaCoordinateToDecimal(String nmeaCoordinate, String bearing) {
        double decimalCoordinate;
        double degrees;
        double minutes;
        // Co-ordinate must have at least 7 digits to be a valid Nmea coordinate.
        if (nmeaCoordinate.length() < 7) {
            logger.error("Could not parse Nmea co-ordinate, invalid co-ordinate length.");
            return -1;
        }

        // Split NMEA co-ordinate into degrees and minutes so we can convert it to decimal.
        try {
            if (nmeaCoordinate.charAt(4) == '.') {
                // In this case we have a latitude.
                degrees = Double.parseDouble(nmeaCoordinate.substring(0, 2));
                if (degrees >= 90) {
                    logger.error("Could not parse NMEA co-ordinate, co-ordinate value to large");
                    return -1;
                }
                minutes = Double.parseDouble(nmeaCoordinate.substring(2));
            } else if (nmeaCoordinate.charAt(5) == '.') {
                // In this case we have a longitude.
                degrees = Double.parseDouble(nmeaCoordinate.substring(0, 3));
                if (degrees >= 180) {
                    logger.error("Could not parse NMEA co-ordinate, co-ordinate value to large");
                    return -1;
                }
                minutes = Double.parseDouble(nmeaCoordinate.substring(3));
            } else {
                // Co-ordinate is not of the format XXXX.XXX(...) or XXXXX.XXX(...).
                logger.error("Could not parse NMEA co-ordinate, invalid co-ordinate format.");
                return -1;
            }
        } catch (NumberFormatException ex) {
            logger.error("Could not parse NMEA co-ordinate, co-ordinate."
                    + "String must contain a number");
            return -1;
        }

        // Convert the NMEA co-rdinate to a decimal co-ordinate.
        decimalCoordinate = degrees + (minutes / 60);
        if (bearing.equals("S") || bearing.equals("W")) {
            decimalCoordinate *= -1;
        } else if (!bearing.equals("N") && !bearing.equals("E")) {
            logger.error("Could not parse NMEA co-ordinate, co-ordinate."
                    + "bearing must be N,S,E or W.");
            return -1;
        }

        return decimalCoordinate;
    }
    
    @Override
    public double getLatitude() {
        readSensorValues();
        return latitude;
    }

    @Override
    public double getLongitude() {
        readSensorValues();
        return longitude;
    }

    @Override
    public double getHeight() {
        readSensorValues();
        return altitude;
    }
}
