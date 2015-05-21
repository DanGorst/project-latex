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
public class Adler32ChecksumGeneratorTest {

    private Adler32ChecksumGenerator checksumGenerator;
    
    @Before
    public void setUp() {
        checksumGenerator = new Adler32ChecksumGenerator();
    }

    /**
     * Test of generateChecksum method, of class Adler32ChecksumGenerator.
     */
    @Test
    public void testGenerateChecksum() {
        assertEquals("59b016d", checksumGenerator.generateChecksum("1234567"));
        assertEquals("81e0256", checksumGenerator.generateChecksum("abcdef"));
    }
    
}
