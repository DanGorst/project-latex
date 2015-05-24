/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import java.io.File;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dan
 */
public class FlightInfoManagerTest {
    
    private FlightInfoManager flightInfoManager;
    
    private final String infoFile = "testInfo.json";
    
    @Before
    public void setUp() {
        flightInfoManager = new FlightInfoManager(infoFile);
    }
    
    @After
    public void tearDown() {
        File file = new File(infoFile);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testGetFlightInfoWhenFileDoesntExist() {
        FlightInfo result = flightInfoManager.getFlightInfo();
        assertEquals(0, result.getFlightNumber());
    }

    @Test
    public void testUpdateFlightInfoWritesToFile() {
        FlightInfo flightInfo = new FlightInfo();
        flightInfo.setFlightNumber(123);
        flightInfoManager.updateFlightInfo(flightInfo);
        
        File file = new File(infoFile);
        assertTrue(file.exists());
        FlightInfo result = flightInfoManager.getFlightInfo();
        assertEquals(123, result.getFlightNumber());
    }
    
}
