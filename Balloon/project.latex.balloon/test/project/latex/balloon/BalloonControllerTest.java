/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import project.latex.balloon.sensor.CameraSensorController;
import project.latex.balloon.sensor.SensorController;
import project.latex.writer.CameraDataWriter;
import project.latex.writer.DataWriter;

/**
 *
 * @author Dan
 */
public class BalloonControllerTest {
    
    public BalloonControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of shouldWriterHandleDataFromSensor method, of class BalloonController.
     */
    @Test
    public void testNormalWriterShouldntHandleDataFromCamera() {
        DataWriter writer = Mockito.mock(DataWriter.class);
        SensorController controller = Mockito.mock(CameraSensorController.class);
        boolean expResult = false;
        boolean result = BalloonController.shouldWriterHandleDataFromSensor(writer, controller);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCameraDataWriterShouldHandleDataFromCamera() {
        DataWriter writer = Mockito.mock(CameraDataWriter.class);
        SensorController controller = Mockito.mock(CameraSensorController.class);
        boolean expResult = true;
        boolean result = BalloonController.shouldWriterHandleDataFromSensor(writer, controller);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCameraDataWriterShouldntHandleDataFromNormalSensor() {
        DataWriter writer = Mockito.mock(CameraDataWriter.class);
        SensorController controller = Mockito.mock(SensorController.class);
        boolean expResult = false;
        boolean result = BalloonController.shouldWriterHandleDataFromSensor(writer, controller);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testDataWriterShouldHandleDataFromNormalSensor() {
        DataWriter writer = Mockito.mock(DataWriter.class);
        SensorController controller = Mockito.mock(SensorController.class);
        boolean expResult = true;
        boolean result = BalloonController.shouldWriterHandleDataFromSensor(writer, controller);
        assertEquals(expResult, result);
    }
}
