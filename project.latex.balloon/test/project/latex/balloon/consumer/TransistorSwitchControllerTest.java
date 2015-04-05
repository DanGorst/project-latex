/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.consumer;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 *
 * @author dgorst
 */
public class TransistorSwitchControllerTest {

    private static final double ARMING_HEIGHT = 500;

    private static final double SWITCHING_HEIGHT = 100;
    
    private static final String HEIGHT_KEY = "altitude";

    private TransistorSwitchController controller;

    private TransistorSwitch mockSwitch;

    @Before
    public void setUp() {
        mockSwitch = mock(TransistorSwitch.class);
        controller = new TransistorSwitchController(mockSwitch, ARMING_HEIGHT, SWITCHING_HEIGHT, HEIGHT_KEY);
    }

    @Test
    public void testControllerArmedAfterConsecutiveReadingsOverThreshold() {
        for (int i = 0; i < TransistorSwitchController.NUMBER_OF_READINGS_FOR_CONFIDENCE; ++i) {
            assertFalse(controller.isArmed());
            controller.processHeight(ARMING_HEIGHT + 5);
        }
        assertTrue(controller.isArmed());
    }

    @Test
    public void testControllerNotArmedAfterNonConsecutiveReadingsOverThreshold() {
        for (int i = 0; i < TransistorSwitchController.NUMBER_OF_READINGS_FOR_CONFIDENCE - 1; ++i) {
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
        for (int i = 0; i < TransistorSwitchController.NUMBER_OF_READINGS_FOR_CONFIDENCE; ++i) {
            controller.processHeight(ARMING_HEIGHT + 5);
        }
        assertTrue(controller.isArmed());
    }

    @Test
    public void testControllerSwitchesAfterConsecutiveReadingsUnderThresholdWhenArmed() {
        armController();
        for (int i = 0; i < TransistorSwitchController.NUMBER_OF_READINGS_FOR_CONFIDENCE; ++i) {
            controller.processHeight(SWITCHING_HEIGHT - 5);
        }
        verify(mockSwitch).close();
    }

    @Test
    public void testControllerDoesntSwitchWithoutConsecutiveReadingsWhenArmed() {
        armController();
        for (int i = 0; i < TransistorSwitchController.NUMBER_OF_READINGS_FOR_CONFIDENCE - 1; ++i) {
            controller.processHeight(SWITCHING_HEIGHT - 5);
        }
        controller.processHeight(SWITCHING_HEIGHT + 5);
        controller.processHeight(SWITCHING_HEIGHT - 5);
        verify(mockSwitch, never()).close();
    }
    
    @Test
    public void testControllerConsumesValidDataModelCorrectly() {
        Map<String, Object> validDataModel = new HashMap<>();
        validDataModel.put(HEIGHT_KEY, ARMING_HEIGHT - 5);
        controller.consumeDataModel(validDataModel);
        assertFalse(controller.isArmed());
        
        // Now set the height in the model to be over the arming height
        validDataModel.put(HEIGHT_KEY, ARMING_HEIGHT + 5);
        for (int i = 0; i < TransistorSwitchController.NUMBER_OF_READINGS_FOR_CONFIDENCE; ++i) {
            controller.consumeDataModel(validDataModel);
        }
        assertTrue(controller.isArmed());
    }
    
    @Test
    public void testControllerConsumesDataModelWithoutReadingsWithoutThrowingException() {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("OtherKey", "Other data");
        controller.consumeDataModel(dataModel);
    }
    
    @Test
    public void testControllerConsumesNullDataModelWithoutThrowingException() {
        controller.consumeDataModel(null);
    }
    
    @Test
    public void testControllerConsumesDataModelWithReadingOfWrongTypeWithoutThrowingException() {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(HEIGHT_KEY, "Invalid data");
        controller.consumeDataModel(dataModel);
    }
}
