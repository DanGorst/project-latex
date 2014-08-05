/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import java.util.Map;

/**
 *
 * @author dgorst
 */
public interface ControllerRunner {
    boolean shouldKeepRunning();
    
    void controllerFinishedRunLoop(Map<String, Object> data);
}
