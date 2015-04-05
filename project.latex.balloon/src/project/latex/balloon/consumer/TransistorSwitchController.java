/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.consumer;

import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author dgorst
 */
public class TransistorSwitchController implements DataModelConsumer {
    
    private static final Logger logger = Logger.getLogger(TransistorSwitchController.class);

    private final double armingHeight;

    private final double switchingHeight;

    static final int NUMBER_OF_READINGS_FOR_CONFIDENCE = 5;

    private int armingReadings = 0;

    private int switchingReadings = 0;

    private boolean armed;

    private final TransistorSwitch transistorSwitch;
    
    private final String heightKey;

    public TransistorSwitchController(TransistorSwitch transistorSwitch, double armingHeight, 
            double switchingHeight, String heightKey) {
        this.transistorSwitch = transistorSwitch;
        this.armingHeight = armingHeight;
        this.switchingHeight = switchingHeight;
        this.heightKey = heightKey;
    }

    public boolean isArmed() {
        return armed;
    }

    @Override
    public void consumeDataModel(Map<String, Object> dataModel) {
        if (dataModel == null) {
            logger.warn("Could not consume null data model");
            return;
        }
        Object heightValue =  dataModel.get(heightKey);
        if (heightValue == null) {
            logger.warn("No data found against the " + heightKey + " key within the data model");
            return;
        }
        if (heightValue instanceof Double) {
            processHeight((double) heightValue);
        } else {
            logger.warn("Object of wrong type passed for height. Object passed was: " + heightValue);
        }
    }

    /**
     * Once we go above a threshold height, arm the controller. Once we
     * subsequently go below a threshold, switch the transistorSwitch on.
     *
     * @param height Current height of the balloon
     */
    public void processHeight(double height) {
        if (!armed) {
            armIfReady(height);
        } else {
            switchIfReady(height);
        }
    }

    /**
     * Calculate if we should arm the controller. For this, we require a number
     * of consecutive readings above the arming threshold.
     *
     * @param height
     */
    private void armIfReady(double height) {
        if (height > armingHeight) {
            ++armingReadings;
        } else {
            armingReadings = 0;
        }
        if (armingReadings >= NUMBER_OF_READINGS_FOR_CONFIDENCE) {
            armed = true;
        }
    }

    /**
     * Calculate if we should turn on the transistorSwitch. For this, we require
     * a number of consecutive readings below the switching threshold.
     *
     * @param height
     */
    private void switchIfReady(double height) {
        if (height < switchingHeight) {
            ++switchingReadings;
        } else {
            switchingReadings = 0;
        }
        if (switchingReadings >= NUMBER_OF_READINGS_FOR_CONFIDENCE) {
            transistorSwitch.close();
        }
    }
}
