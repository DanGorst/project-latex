/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

import java.util.List;

/**
 *
 * @author Dan
 */
public interface CameraSensorController {
    final static String dataKey = "latestImageFiles";
    
    public String getSensorName();
    
    List<String> getImageFileNames();
}
