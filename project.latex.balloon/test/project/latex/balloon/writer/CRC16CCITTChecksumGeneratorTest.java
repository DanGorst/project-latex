/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dan
 */
public class CRC16CCITTChecksumGeneratorTest {
    
    private CRC16CCITTChecksumGenerator checksumGenerator;
    
    @Before
    public void setUp() {
        checksumGenerator = new CRC16CCITTChecksumGenerator();
    }

    /**
     * Test of generateChecksum method, of class CRC16CCITTChecksumGenerator.
     */
    @Test
    public void testGenerateChecksumWithThreeNonZeroDigits() {
        String result = checksumGenerator.generateChecksum("test1");
        assertEquals("3ac", result);
    }
    
    @Test
    public void testGenerateChecksum() {
        String result = checksumGenerator.generateChecksum("1234567");
        assertEquals("7718", result);
    }
}
