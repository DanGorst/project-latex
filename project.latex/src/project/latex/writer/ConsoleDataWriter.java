/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.writer;

import project.latex.SensorData;

/**
 *
 * @author Dan
 */
public class ConsoleDataWriter implements DataWriter {

    @Override
    public void writeData(SensorData data) {
        System.out.println(data.getSensorName());
        System.out.println(data.getDate());
        System.out.println(data.getData());
    }
    
}
