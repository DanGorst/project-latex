/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer.camera;

import java.io.File;
import java.util.List;
import project.latex.balloon.writer.DataWriteFailedException;

/**
 *
 * @author Dan
 */
public interface CameraDataWriter {
    void writeImageFiles(List<File> imageFiles) throws DataWriteFailedException;
}
