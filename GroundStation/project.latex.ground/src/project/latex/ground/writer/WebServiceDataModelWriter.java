/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.ground.writer;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import project.latex.ground.BalloonDataModel;

/**
 *
 * @author Dan
 */
public class WebServiceDataModelWriter implements DataModelWriter {

    private final String serviceUrl;
    
    private static final Logger logger = Logger.getRootLogger();

    public WebServiceDataModelWriter(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public void writeDataModel(BalloonDataModel dataModel) {
        ClientResource resource = new ClientResource(serviceUrl);

        // Send the HTTP PUT request
        Gson gson = new Gson();
        try {
            resource.put(gson.toJson(dataModel));

            if (!resource.getStatus().isSuccess()) {
                // Unexpected status
                logger.error("An unexpected status was returned: " + resource.getStatus());
            }
        } catch (ResourceException e) {
            logger.error("Failed to send data to " + serviceUrl);
        }
    }

}
