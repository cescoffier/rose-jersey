package org.ow2.chameleon.test.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public interface RessourceService {
    
    @GET
    @Produces("text/plain")
    public String hello();
}
