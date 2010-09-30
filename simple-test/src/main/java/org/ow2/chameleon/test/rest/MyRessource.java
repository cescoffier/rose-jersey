package org.ow2.chameleon.test.rest;

import static org.ow2.chameleon.rose.server.EndpointFactory.PROP_ENDPOINT_NAME;
import static org.ow2.chameleon.rose.server.EndpointFactory.PROP_INTERFACE_NAME;

import java.util.HashMap;

import org.ow2.chameleon.rose.server.EndpointFactory;


public class MyRessource implements RessourceService{
    
    private EndpointFactory endpointFac;
    private String name;
    
    public String hello(){
        return name;
    }
    
    void start(){
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put(PROP_INTERFACE_NAME, RessourceService.class.getName());
        properties.put(PROP_ENDPOINT_NAME, "test");
        
        endpointFac.createEndpoint(this,RessourceService.class.getClassLoader(), properties);
    }
    
    void stop(){
        endpointFac.destroyEndpoint("test");
    }
}