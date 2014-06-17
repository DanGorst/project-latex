/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.ground.writer;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import project.latex.ground.BalloonDataModel;

/**
 *
 * @author Dan
 */
public class WebServiceDataModelWriter implements DataModelWriter {

    private final String serviceUrl;

    public WebServiceDataModelWriter(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public void writeDataModel(BalloonDataModel dataModel) {
        ClientResource resource = new ClientResource(serviceUrl);

        // Send the HTTP PUT request
        Gson gson = new Gson();
        resource.put(gson.toJson(dataModel));

        if (!resource.getStatus().isSuccess()) {
            // Unexpected status
            System.out.println("An unexpected status was returned: "
                    + resource.getStatus());
        }
    }

}
