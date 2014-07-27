/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import project.latex.SensorData;

/**
 *
 * @author Dan
 */
public class CameraController implements CameraSensorController {

    private final String imagesDirectory;
    
    public CameraController(String imagesDirectory)   {
        this.imagesDirectory = imagesDirectory;
    }
    
    @Override
    public String getSensorName() {
        return "Camera";
    }

    @Override
    public SensorData getCurrentData() {
        // TODO: This is placeholder behaviour for now. We should this directory for real instead
        Map<String, Object> data = new HashMap<>();
        data.put("latestImageFile", this.imagesDirectory + File.separator + "test.jpg");
        return new SensorData(this.getSensorName(), new Date(), data);
    }
}
