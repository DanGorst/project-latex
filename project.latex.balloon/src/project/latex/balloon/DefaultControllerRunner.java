/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author dgorst
 */
public class DefaultControllerRunner implements ControllerRunner {

    private final Logger logger = Logger.getLogger(DefaultControllerRunner.class);
    // Time in millis that thread will sleep for after a sensor data run loop,
    // this is calculated from baud rate.
    private final int sensorDataRunLoopSleepTime;
    // Time in millis that the thread will sleep for after an ssdv run loop, 
    // this is calculated from baud rate.
    private final int ssdvRunLoopSleepTime;
    
    private ControllerRunLoop currentRunLoop;

    public DefaultControllerRunner(int baudRate) {
        this.currentRunLoop = ControllerRunLoop.SensorDataRunLoop;
        final int byteSize = 8;
        // This provides some allowance for the time that there is no data being sent
        // due to the software being on another thread etc.. gives some margin of error.
        final double extraSleepMultiplier = 1.1;
        // Assume a max of 250 bytes of data in sensor data sentences.
        final int sensorDataMaxBytes = 250;
        sensorDataRunLoopSleepTime = (int) (((sensorDataMaxBytes*byteSize)/baudRate)* extraSleepMultiplier * 1000);
        // Size of an ssdv image packet.
        final int packetSize = 256;
        ssdvRunLoopSleepTime = (int) ((packetSize * byteSize)/baudRate * extraSleepMultiplier * 1000);
    }
    
    @Override
    public boolean shouldKeepRunning() {
        return true;
    }

    @Override
    public void controllerFinishedRunLoop(Map<String, Object> data) {
        try {
            switch (currentRunLoop) {
                case SensorDataRunLoop:
                    currentRunLoop = ControllerRunLoop.SsdvRunLoop;
                    // Sleep this thread so we're not loading the CPU too much from the controller          
                    logger.info(String.format("Sensor data run loop finished. Going to sleep for %f seconds.", sensorDataRunLoopSleepTime/1000.0));
                    Thread.sleep(sensorDataRunLoopSleepTime);
                    break;
                case SsdvRunLoop:
                    currentRunLoop = ControllerRunLoop.SensorDataRunLoop;
                    // Sleep this thread so we're not loading the CPU too much from the controller          
                    logger.info(String.format("Ssdv run loop finished. Going to sleep for %f seconds.", ssdvRunLoopSleepTime/1000.0));
                    Thread.sleep(ssdvRunLoopSleepTime);
                    break;  
            }
        } catch (InterruptedException ex) {
            logger.error(ex);
        }
    }
    
    @Override
    public ControllerRunLoop getCurrentRunLoop() {
        return currentRunLoop;
    }
    
}
