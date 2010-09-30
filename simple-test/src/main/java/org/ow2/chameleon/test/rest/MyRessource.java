package org.ow2.chameleon.test.rest;

import static org.ow2.chameleon.rose.server.EndpointFactory.PROP_ENDPOINT_NAME;
import static org.ow2.chameleon.rose.server.EndpointFactory.PROP_INTERFACE_NAME;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.ow2.chameleon.rose.server.EndpointFactory;

@Path("test")
public class MyRessource {
    
    private EndpointFactory endpointFac;
    private String name;
    
    @GET
    @Produces("text/plain")
    public String hello(){
        return name;
    }
    
    void start(){
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put(PROP_INTERFACE_NAME, MyTest.class.getName());
        properties.put(PROP_ENDPOINT_NAME, "test");
        
        endpointFac.createEndpoint(new MyTest(),MyTest.class.getClassLoader(), properties);
    }
    
    void stop(){
        endpointFac.destroyEndpoint("test");
    }
}