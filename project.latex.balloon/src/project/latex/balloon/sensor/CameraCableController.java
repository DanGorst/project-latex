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
public class CameraCableController {

    private static final double ARMING_HEIGHT = 500;

    private static final double SWITCHING_HEIGHT = 100;

    private static final int NUMBER_OF_READINGS_FOR_CONFIDENCE = 5;

    private int armingReadings = 0;

    private int switchingReadings = 0;

    private boolean armed;

    private final SwitchingCable cable;

    public CameraCableController(SwitchingCable cable) {
        this.cable = cable;
    }

    /**
     * Once we go above a threshold height, arm the controller. Once we
     * subsequently go below a threshold, switch the cable on.
     *
     * @param height Current height of the balloon
     */
    private void processHeight(double height) {
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
        if (height > ARMING_HEIGHT) {
            ++armingReadings;
        } else {
            armingReadings = 0;
        }
        if (armingReadings >= NUMBER_OF_READINGS_FOR_CONFIDENCE) {
            armed = true;
        }
    }

    /**
     * Calculate if we should turn on the cable. For this, we require a number
     * of consecutive readings below the switching threshold.
     *
     * @param height
     */
    private void switchIfReady(double height) {
        if (height < SWITCHING_HEIGHT) {
            ++switchingReadings;
        } else {
            switchingReadings = 0;
        }
        if (switchingReadings >= NUMBER_OF_READINGS_FOR_CONFIDENCE) {
            cable.enable(true);
        }
    }
}
