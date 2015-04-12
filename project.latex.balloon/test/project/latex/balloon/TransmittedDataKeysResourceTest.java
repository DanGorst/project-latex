/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dgorst
 */
public class TransmittedDataKeysResourceTest {

       /**
     * Test of loadTransmittedDataKeys method, of class BalloonController.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadTransmittedDataKeysThrowsIfFilePathIsNull() throws Exception {
        TransmittedDataKeysResource transmittedDataKeysResource = new TransmittedDataKeysResource(null);
    }

    @Test(expected = FileNotFoundException.class)
    public void testLoadTransmittedDataKeysThrowsIfFilePathIsInvalid() throws Exception {
        TransmittedDataKeysResource transmittedDataKeysResource = new TransmittedDataKeysResource("invalid");
    }

    @Test
    public void testLoadTransmittedDataKeysSucceedsWithValidFile() throws IOException {
        TransmittedDataKeysResource transmittedDataKeysResource = new TransmittedDataKeysResource("test/testKeys.json");
        List<String> actual = transmittedDataKeysResource.getTransmittedDataKeys();
        List<String> expected = new ArrayList<>();
        expected.add("first");
        expected.add("second");
        expected.add("last");
        assertEquals(expected, actual);
    }
}
