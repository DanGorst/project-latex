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
 * @author Dan
 */
public class IncrementalSentenceIdGeneratorTest {
    
    private IncrementalSentenceIdGenerator sentenceIdGenerator;
    
    @Before
    public void setUp() {
        sentenceIdGenerator = new IncrementalSentenceIdGenerator();
    }

    @Test
    public void testGenerateId() {
        assertEquals("0", sentenceIdGenerator.generateId());
        assertEquals("1", sentenceIdGenerator.generateId());
        assertEquals("2", sentenceIdGenerator.generateId());
    }
    
}
