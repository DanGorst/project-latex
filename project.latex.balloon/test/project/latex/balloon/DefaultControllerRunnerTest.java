/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dgorst
 */
public class DefaultControllerRunnerTest {
    
    private DefaultControllerRunner instance;
    
    @Before
    public void setUp() {
        instance = new DefaultControllerRunner();
    }

    /**
     * Test of shouldKeepRunning method, of class DefaultControllerRunner.
     */
    @Test
    public void testShouldKeepRunning() {
        assertEquals(true, instance.shouldKeepRunning());
    }
}
