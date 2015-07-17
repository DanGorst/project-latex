/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.io.File;
import java.util.List;

/**
 *
 * @author Dan
 */
public interface CameraDataWriter {
    void writeImageFiles(List<File> imageFiles) throws DataWriteFailedException;
}
