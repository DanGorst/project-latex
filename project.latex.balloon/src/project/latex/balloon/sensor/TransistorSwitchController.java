/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

/**
 *
 * @author dgorst
 */
public class TransistorSwitchController {

    private final double armingHeight;

    private final double switchingHeight;

    static final int NUMBER_OF_READINGS_FOR_CONFIDENCE = 5;

    private int armingReadings = 0;

    private int switchingReadings = 0;

    private boolean armed;

    private final TransistorSwitch transistorSwitch;

    public TransistorSwitchController(TransistorSwitch transistorSwitch, double armingHeight, double switchingHeight) {
        this.transistorSwitch = transistorSwitch;
        this.armingHeight = armingHeight;
        this.switchingHeight = switchingHeight;
    }

    public boolean isArmed() {
        return armed;
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
