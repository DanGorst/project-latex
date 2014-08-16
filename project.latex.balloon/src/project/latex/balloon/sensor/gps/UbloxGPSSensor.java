/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import project.latex.balloon.sensor.gps.UbloxGPSSensorController;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 */
public class UbloxGPSSensor {

    private static final Logger logger = Logger.getLogger(UbloxGPSSensorController.class);
    private final Serial serial;

    public UbloxGPSSensor() {
        this.serial = SerialFactory.createInstance();
    }

    // Constructor where we inject the dependencies
    public UbloxGPSSensor(Serial serial) {
        this();
    }

    public String getNMEASentence(String GPXXX) throws SensorReadFailedException {
        // What we read from the serial port is an NMEA sentence, see:
        // http://www.gpsinformation.org/dale/nmea.htm#GSA
        char currentChar = 0;
        String sentence = "";

        try {
            serial.open(Serial.DEFAULT_COM_PORT, 9600);
            // Check that the serial port read buffer is receiving data.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            if (serial.availableBytes() == 0) {
                throw new SensorReadFailedException("No serial data available to"
                        + " read, check hardware connections.");
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

        } catch (UnsatisfiedLinkError error) {
            throw new SensorReadFailedException("Unsatisfied link error");
        } catch (SerialPortException ex) {
            throw new SensorReadFailedException("Failed to open serial port");
        } catch (IllegalStateException ex) {
            throw new SensorReadFailedException("Failed to read serial port");
        } finally {
            if (serial.isOpen()) {
                try {
                    serial.close();
                } catch (IllegalStateException ex) {
                    logger.error("Failed to close serial port.");
                }
            }
        }

        return sentence;
    }
}
