/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 * @author dgorst
 */
public class BalloonDataModelResource extends ServerResource {
    
    @Get
    public String represent() {
        return "Hello, world!";
    }
}
