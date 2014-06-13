/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex;

import com.google.gson.Gson;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import project.latex.ground.BalloonDataModel;

/**
 *
 * @author dgorst
 */
public class BalloonDataModelResource extends ServerResource {
    
    @Get
    public String represent() {
        BalloonDataModel dataModel = new BalloonDataModel();
        dataModel.setHeight(1234.0f);
        dataModel.setLatitude(51.45f);
        dataModel.setLongitude(-2.58f);
        
        // Write the data model to JSON, and return the JSON string
        Gson gson = new Gson();
        return gson.toJson(dataModel);
    }
}
