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
    private final Serial serial;
    private String latitudeKey;
    private String longitudeKey;
    private String altitudeKey;
    private String time;
    private String date;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speedInMPH;

    /* Constructor where we inject all the dependencies. Useful for testing if we want to mock out dependencies. */
    public UbloxGPSSensorController(String latitudeKey, String longitudeKey, String altitudeKey, Serial serial) {
        this.latitudeKey = latitudeKey;
        this.longitudeKey = longitudeKey;
        this.altitudeKey = altitudeKey;
        this.serial = serial;
    }

    /* Constructor where we default to using the SerialFactory to give us the Serial instance. 
     This means callers on the sensor don't need to depend on the pi4j library. */
    public UbloxGPSSensorController(String latitudeKey, String longitudeKey, String altitudeKey) {
        this(latitudeKey, longitudeKey, altitudeKey, SerialFactory.createInstance());
    }

    @Override
    public HashMap<String, Object> getCurrentData() {
        HashMap<String, Object> data = new HashMap<>();

        try {
            serial.open(Serial.DEFAULT_COM_PORT, 9600);
            // readSensorValues() attempts to update the latitude, longitude, altitude,
            // speed, time and date fields and returns true if it succeeded.
            if (readSensorValues() == true) {
                data.put(latitudeKey, this.latitude);
                data.put(longitudeKey, this.longitude);
                data.put(altitudeKey, this.altitude);
            }

        } catch (UnsatisfiedLinkError error) {
            logger.error(error);
        } catch (SerialPortException ex) {
            logger.error("Failed to open serial port.");
        } finally {
            try {
                serial.close();
            } catch (IllegalStateException ex) {
                logger.error("Failed to close serial port.");
            }
        }

        return data;
    }

    private boolean readSensorValues() {
        // What we read from the serial port is an NMEA sentence, see:
        // http://www.gpsinformation.org/dale/nmea.htm#GSA
        String[] currentNmeaSentence;

        // Get a GPGGA NMEA sentence:
        currentNmeaSentence = getNmeaSentence("GPGGA").split(",", -1);
        if (currentNmeaSentence.equals("error")) {
            return false;
        }

        // Make sure we have a GPS fix.
        if (Integer.parseInt(currentNmeaSentence[6]) == 0) {
            logger.info("No GPS fix");
            return false;
        }

        // Extract latitude, longitude, altitude and speed data from 
        // the GPGGA sentence and parse lat/long from NMEA to decimal format.
        latitude = parseNmeaCoordinateToDecimal(currentNmeaSentence[2], currentNmeaSentence[3]);
        longitude = parseNmeaCoordinateToDecimal(currentNmeaSentence[4], currentNmeaSentence[5]);
        altitude = Double.parseDouble(currentNmeaSentence[9]);

        // Get a GPRMC NMEA sentence:
        currentNmeaSentence = getNmeaSentence("GPRMC").split(",", -1);
        if (currentNmeaSentence.equals("error")) {
            return false;
        }

        // Make sure we have a GPS fix.
        if (currentNmeaSentence[2].equals("V")) {
            logger.info("No GPS fix");
            return false;
        }

        // Extract date, time and speed data from the
        // the GPRMC sentence and convert to correct fommats.
        time = currentNmeaSentence[1].substring(0, 2) + ":"
                + currentNmeaSentence[1].substring(2, 4) + ":"
                + currentNmeaSentence[1].substring(4, 6);
        date = currentNmeaSentence[7];
        speedInMPH = Double.parseDouble(currentNmeaSentence[5]) * 1.1508;

        return true;
    }

    public String getNmeaSentence(String GPXXX) {

        char currentChar = 0;
        String sentence = "";

            try {
                // Check that the serial port read buffer is receiving data.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                if (serial.availableBytes() == 0) {
                    logger.error("No serial data available to read, check hardware connections.");
                    return "error";
                }

                while (true) {
                    sentence = "";
                    // Find the start of a new line and move to its first character.
                    while (currentChar != Character.LINE_SEPARATOR) {
                        currentChar = serial.read();
                    }
                    currentChar = serial.read();
                    // Create a String of all characters until the end of the line.
                    while (currentChar != Character.LINE_SEPARATOR) {
                        sentence += currentChar;
                        currentChar = serial.read();
                    }
                    // If NMEA sentence is of the specified type we can break
                    // the loop. Otherwise, read the next sentence.
                    if (sentence.substring(2, 7).equals(GPXXX)) {
                        break;
                    }
                }
            } catch (IllegalStateException ex) {
                logger.error("Failed read serial port.");
                return "error";
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
