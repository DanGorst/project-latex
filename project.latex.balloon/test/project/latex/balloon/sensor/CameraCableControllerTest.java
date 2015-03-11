/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.sensor;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 *
 * @author dgorst
 */
public class CameraCableControllerTest {

    private static final double ARMING_HEIGHT = 500;

    private static final double SWITCHING_HEIGHT = 100;

    private CameraCableController controller;

    private SwitchingCable mockCable;

    @Before
    public void setUp() {
        mockCable = mock(SwitchingCable.class);
        controller = new CameraCableController(mockCable, ARMING_HEIGHT, SWITCHING_HEIGHT);
    }

    @Test
    public void testControllerArmedAfterConsecutiveReadingsOverThreshold() {
        for (int i = 0; i < CameraCableController.NUMBER_OF_READINGS_FOR_CONFIDENCE; ++i) {
            assertFalse(controller.isArmed());
            controller.processHeight(ARMING_HEIGHT + 5);
        }
        assertTrue(controller.isArmed());
    }

    @Test
    public void testControllerNotArmedAfterNonConsecutiveReadingsOverThreshold() {
        for (int i = 0; i < CameraCableController.NUMBER_OF_READINGS_FOR_CONFIDENCE - 1; ++i) {
            assertFalse(controller.isArmed());
            controller.processHeight(ARMING_HEIGHT + 5);
        }
        // Break up the readings with one below the threshold
        controller.processHeight(ARMING_HEIGHT - 5);
        assertFalse(controller.isArmed());
        controller.processHeight(ARMING_HEIGHT + 5);
        assertFalse(controller.isArmed());
    }

    private void armController() {
        for (int i = 0; i < CameraCableController.NUMBER_OF_READINGS_FOR_CONFIDENCE; ++i) {
            controller.processHeight(ARMING_HEIGHT + 5);
        }
        assertTrue(controller.isArmed());
    }

    @Test
    public void testControllerSwitchesAfterConsecutiveReadingsUnderThresholdWhenArmed() {
        armController();
        for (int i = 0; i < CameraCableController.NUMBER_OF_READINGS_FOR_CONFIDENCE; ++i) {
            controller.processHeight(SWITCHING_HEIGHT - 5);
        }
        verify(mockCable).enable(true);
    }

    @Test
    public void testControllerDoesntSwitchWithoutConsecutiveReadingsWhenArmed() {
        armController();
        for (int i = 0; i < CameraCableController.NUMBER_OF_READINGS_FOR_CONFIDENCE - 1; ++i) {
            controller.processHeight(SWITCHING_HEIGHT - 5);
        }
        controller.processHeight(SWITCHING_HEIGHT + 5);
        controller.processHeight(SWITCHING_HEIGHT - 5);
        verify(mockCable, never()).enable(true);
    }
}
