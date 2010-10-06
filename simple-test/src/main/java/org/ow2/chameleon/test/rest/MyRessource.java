package org.ow2.chameleon.test.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.felix.ipojo.Factory;

@Path("test")
public class MyRessource implements RessourceService {

    private Factory fac;

    @GET
    @Produces("text/plain")
    public String hello() {
        return fac.getName();
    }
}