/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import project.latex.writer.DataWriteFailedException;

/**
 *
 * @author Dan
 */
public class SensorFileLoggerServiceTest {
    
    private SensorFileLoggerService service;
    
    public SensorFileLoggerServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.service = new SensorFileLoggerService();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setLoggerForSensor method, of class SensorFileLoggerService.
     * @throws project.latex.writer.DataWriteFailedException
     */
    @Test(expected = DataWriteFailedException.class)
    public void testSetLoggerThrowsMeaningfulExceptionForNullSensor() throws DataWriteFailedException {
        service.setLoggerForSensor(null, "");
    }
    
    @Test(expected = DataWriteFailedException.class)
    public void testSetLoggerThrowsMeaningfulExceptionForNullBaseUrl() throws DataWriteFailedException {
        service.setLoggerForSensor("Sensor", null);
    }
    
    @Test
    public void testServiceReturnsLoggerForValidNameAndUrl()    {
        try {
            service.setLoggerForSensor("Sensor", "");
            assertNotNull(service.getLoggerForSensor("Sensor"));
        } catch (DataWriteFailedException ex) {
            fail(ex.getMessage());
        }
    }
}
