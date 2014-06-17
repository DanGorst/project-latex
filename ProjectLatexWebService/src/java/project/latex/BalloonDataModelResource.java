/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex;

import com.google.gson.Gson;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import project.latex.ground.BalloonDataModel;

/**
 *
 * @author dgorst
 */
public class BalloonDataModelResource extends ServerResource {
    private static final BalloonDataModel dataModel = new BalloonDataModel();
    
    @Get
    public String represent() {
        // Write the data model to JSON, and return the JSON string
        Gson gson = new Gson();
        return gson.toJson(BalloonDataModelResource.dataModel);
    }
    
    @Put
    public void store(String jsonString)   {
        Gson gson = new Gson();
        BalloonDataModel dataModelIn = gson.fromJson(jsonString, BalloonDataModel.class);
        
        BalloonDataModelResource.dataModel.setHeight(dataModelIn.getHeight());
        BalloonDataModelResource.dataModel.setLatitude(dataModelIn.getLatitude());
        BalloonDataModelResource.dataModel.setLongitude(dataModelIn.getLongitude());
    }
}
