/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor.gps;

import project.latex.balloon.sensor.SensorReadFailedException;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 *
 * @author will
 */
public class UBloxGPSSensor {

    private static final Logger logger = Logger.getLogger(UBloxGPSSensor.class);
    private static final String USB_SERIAL_PORT = "/dev/ttyUSB0";
    private static final int BAUD_RATE = 9600;
    private Serial serial;

    public UBloxGPSSensor() throws SensorReadFailedException {
        this.serial = SerialFactory.createInstance();
        serial.open(USB_SERIAL_PORT, BAUD_RATE);

        try {
            // Sometimes these lines randomly fail to register with the UBlox sensor.
            for (int i = 0; i < 25; i++) {
                // Disable all the default NMEA sentences so that the serial port 
                // read buffer is empty unless we request data. 
                serial.writeln("$PUBX,40,RMC,0,0,0,0*47");
                serial.writeln("$PUBX,40,GGA,0,0,0,0*5A");
                serial.writeln("$PUBX,40,GLL,0,0,0,0*5C");
                serial.writeln("$PUBX,40,GSA,0,0,0,0*4E");
                serial.writeln("$PUBX,40,GSV,0,0,0,0*59");
                serial.writeln("$PUBX,40,VTG,0,0,0,0*5E");
            }
        } catch (SerialPortException ex) {
            throw new SensorReadFailedException("Failed to open serial port: " + ex);
        } catch (IllegalStateException ex) {
            throw new SensorReadFailedException("Failed to read serial port: " + ex);
        } finally {
            if (serial.isOpen()) {
                try {
                    serial.close();
                } catch (IllegalStateException ex) {
                    logger.error("Failed to close serial port:" + ex);
                }
            }
        }
        serial.close();
    }

    public UBloxGPSSensor(Serial serial) {
        this.serial = serial;
    }

    public String getPolledSentence() throws SensorReadFailedException {
        String sentence = "";

        try {
            serial.open(USB_SERIAL_PORT, BAUD_RATE);
            // Request a polled sentence which contains all the data we need.
            serial.writeln("$PUBX,00*33");
            // The requested sentence should arrive within 1.5 seconds.
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            if (serial.availableBytes() == 0) {
                throw new SensorReadFailedException("No serial data available to"
                        + " read, check hardware connections.");
            }

            // Build a String of all characters in the requested sentence.
            while (serial.availableBytes() != 0) {
                sentence += serial.read();
                if (sentence.length() >= 500) {
                    throw new SensorReadFailedException("Unexpectedly large amount "
                            + "of data in serial port read buffer");
                }
            }

            System.out.println(sentence);

        } catch (UnsatisfiedLinkError error) {
            throw new SensorReadFailedException("Unsatisfied link error");
        } catch (SerialPortException ex) {
            throw new SensorReadFailedException("Failed to open serial port: " + ex);
        } catch (IllegalStateException ex) {
            throw new SensorReadFailedException("Failed to read serial port: " + ex);
        } finally {
            if (serial.isOpen()) {
                try {
                    serial.close();
                } catch (IllegalStateException ex) {
                    logger.error("Failed to close serial port:" + ex);
                }
            }
        }

        return sentence;
    }
}
