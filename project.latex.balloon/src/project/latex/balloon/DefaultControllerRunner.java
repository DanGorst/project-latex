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
    
    @Override
    public boolean shouldKeepRunning() {
        return true;
    }

    @Override
    public void controllerFinishedRunLoop(Map<String, Object> data) {
        try {
            // Sleep this thread so we're not loading the CPU too much from the controller
            Thread.sleep(60000);
        } catch (InterruptedException ex) {
            logger.error(ex);
        }
    }
    
}
