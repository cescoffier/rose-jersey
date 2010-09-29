package org.ow2.chameleon.rose.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("mytest")
public class MyRessource{
    @GET
    @Produces("text/plain")
    public String hello(){
        return "toto";
    }
}