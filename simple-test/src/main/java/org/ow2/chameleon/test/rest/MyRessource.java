package org.ow2.chameleon.test.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.osgi.framework.BundleContext;
import org.ow2.chameleon.rose.rest.ext.RestEndpointFactory;

@Path("test")
public class MyRessource {
    
    private RestEndpointFactory pub;
    private String name;
    private BundleContext mycontext;
    
    public MyRessource(BundleContext context) {
        mycontext=context;
    }
    
    @GET
    @Produces("text/plain")
    public String hello(){
        return name;
    }
    
    void start(){
        pub.createEndpoint(this,MyRessource.class.getName(),mycontext.getBundle());
    }
    
    void stop(){
        pub.destroyEndpoint(MyRessource.class.getName());
    }
}