/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dan
 */
public class ChecksumGeneratorTest {
    
    public ChecksumGeneratorTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of generateChecksum method, of class ChecksumGenerator.
     */
    @Test
    public void testGenerateChecksum() {
        assertEquals("59b016d", ChecksumGenerator.generateChecksum("1234567"));
        assertEquals("81e0256", ChecksumGenerator.generateChecksum("abcdef"));
    }
    
}
