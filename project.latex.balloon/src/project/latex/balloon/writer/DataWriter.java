/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.util.Map;

/**
 *
 * @author Dan
 */
public interface DataWriter {
    void writeData(Map<String, Object> dataModel);
}
