/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Dan
 */
public class IncrementalSentenceIdGeneratorTest {
    
    private IncrementalSentenceIdGenerator sentenceIdGenerator;
    
    private FlightInfoManager flightInfoManager;
    
    @Before
    public void setUp() {
        flightInfoManager = mock(FlightInfoManager.class);
        when(flightInfoManager.getFlightInfo()).thenReturn(new FlightInfo());
        sentenceIdGenerator = new IncrementalSentenceIdGenerator(flightInfoManager);
    }

    @Test
    public void testGenerateId() {
        assertEquals("1_0", sentenceIdGenerator.generateId());
        assertEquals("1_1", sentenceIdGenerator.generateId());
        assertEquals("1_2", sentenceIdGenerator.generateId());
    }
    
}
