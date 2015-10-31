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
    
    private int delay;
    
    private ControllerRunLoop currentRunLoop;

    public DefaultControllerRunner() {
        this.currentRunLoop = ControllerRunLoop.SensorDataRunLoop;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
    
    @Override
    public boolean shouldKeepRunning() {
        return true;
    }

    @Override
    public void controllerFinishedRunLoop(Map<String, Object> data) {
        try {
            logger.info(String.format("%s loop finished. Going to sleep for %f seconds.", currentRunLoop.toString(), delay/1000.0));
            switch (currentRunLoop) {
                case SensorDataRunLoop:
                    currentRunLoop = ControllerRunLoop.SsdvRunLoop;
                    // Sleep this thread so we're not loading the CPU too much from the controller          
                    Thread.sleep(delay);
                    break;
                case SsdvRunLoop:
                    currentRunLoop = ControllerRunLoop.SensorDataRunLoop;
                    // Sleep this thread so we're not loading the CPU too much from the controller          
                    Thread.sleep(75000);
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
