package org.ow2.chameleon.test.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.felix.ipojo.Factory;
import org.ow2.chameleon.rose.server.EndpointFactory;

@Path("test")
public class MyRessource {

    private EndpointFactory pub;

    private Factory fac;

    private String name;

    @GET
    @Produces("text/plain")
    public String hello() {
        return fac.getName();
    }

    void start() {
        pub.createEndpoint(this, null);
    }

    void stop() {
        pub.destroyEndpoint("test");
    }
}