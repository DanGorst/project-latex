/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class ProjectLatexApplication extends Application {

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of BalloonDataModelResource.
        Router router = new Router(getContext());

        // Defines only one route
        router.attachDefault(BalloonDataModelResource.class);

        return router;
    }
}