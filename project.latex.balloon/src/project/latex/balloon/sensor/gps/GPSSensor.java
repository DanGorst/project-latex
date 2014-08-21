/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 */
public class GPSSensor {

    private static final Logger logger = Logger.getLogger(GPSSensorController.class);
    private Serial serial;
    private HashSet<String> supportedNmeaSentences = new HashSet<String>();

    public GPSSensor(String... supportedNmeaSentences) {
        for (String sentence : supportedNmeaSentences) {
            this.supportedNmeaSentences.add(sentence);
        }
        this.serial = SerialFactory.createInstance();
    }

    // Constructor where we inject the dependencies
    public GPSSensor(Serial serial, String... supportedNmeaSentences) {
        this(supportedNmeaSentences);
        this.serial = serial;
    }

    public String getNmeaSentence(String GPXXX) throws SensorReadFailedException {
        // What we read from the serial port is an NMEA sentence, see:
        // http://www.gpsinformation.org/dale/nmea.htm#GSA
        char currentChar = 0;
        String sentence = "";

        if (!supportedNmeaSentences.contains(GPXXX)) {
            throw new SensorReadFailedException("GPS module was not initialised "
                    + "as supporting " + GPXXX + " sentences");
        }

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

            // Find GPXXX sentence
            for (int i = 0; i < 20; i++) {
                sentence = "";
                // Find the start of a new line and move to its first character.
                for (int j = 0; true; j++) {
                    currentChar = serial.read();
                    if (currentChar == '$') {
                        sentence += currentChar;
                        break;
                    } else if (j==200) {
                        throw new SensorReadFailedException("Incompatible sensor hardware.");
                    }
                }
                // Create a String of all characters until the end of the line.
                for (int j = 0; true; j++) {
                    currentChar = serial.read();
                    if (currentChar == '$') {
                        break;
                    }
                    sentence += currentChar;
                    if (j==200) {
                        throw new SensorReadFailedException("Incompatible sensor hardware.");
                    }
                    
                }
                // If NMEA sentence is of the specified type we can break
                // the loop. Otherwise, read the next sentence.
                if (sentence.length() >= 6 && sentence.substring(1, 6).equals(GPXXX)) {
                    break;
                }
            }

            // If after 20 iterations, the specified sentence type is not found then 
            // the GPS module must not support GPXXX sentence type.
            if (sentence.equals("")) {
                throw new SensorReadFailedException("Sensor hardware does not support "
                        + GPXXX + " sentences");
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

    public HashSet<String> getSupportedNmeaSentences() {
        return supportedNmeaSentences;
    }
}
