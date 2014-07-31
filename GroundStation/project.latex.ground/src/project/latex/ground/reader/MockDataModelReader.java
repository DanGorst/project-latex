/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.ground.reader;

import project.latex.ground.BalloonDataModel;

/**
 *
 * @author dgorst
 */
public class MockDataModelReader implements DataModelReader {
    
    private final BalloonDataModel mockDataModel = new BalloonDataModel();
    
    public MockDataModelReader()    {
        mockDataModel.setLatitude(0.0f);
        mockDataModel.setLongitude(0.0f);
    }

    @Override
    public BalloonDataModel readDataModel() {
        mockDataModel.setHeight(mockDataModel.getHeight() + 10.f);        
        return mockDataModel;
    }
    
}
