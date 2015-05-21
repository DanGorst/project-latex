/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import java.util.zip.Adler32;
import java.util.zip.Checksum;

/**
 * Generates checksums using the Adler32 algorithm.
 *
 * If you need to generate checksums manually, for example for verification
 * during testing, there are various online services which will do this. I've
 * used this one: http://www.unit-conversion.info/texttools/adler-32/
 *
 * @author Dan
 */
public class Adler32ChecksumGenerator implements ChecksumGenerator {

    @Override
    public String generateChecksum(String input) {
        byte bytes[] = input.getBytes();
        Checksum checksum = new Adler32();
        checksum.update(bytes, 0, bytes.length);
        long checksumValue = checksum.getValue();
        return Long.toHexString(checksumValue);
    }
}
