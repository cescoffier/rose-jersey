package org.ow2.chameleon.rose.rest;

import org.osgi.framework.BundleContext;
import org.ow2.chameleon.rose.rest.ext.RestEndpointFactory;
import org.ow2.chameleon.rose.rest.spec.MyDummyRessourceSpec;


public class MyDummyRessource implements MyDummyRessourceSpec {
    private RestEndpointFactory publisher;
    private BundleContext mycontext;
    
    public MyDummyRessource(BundleContext context) {
        mycontext = context;
    }
    
    public String hello() {
        return "YATAjjjAA";
    }

    private void start(){
        publisher.createEndpoint(this,MyDummyRessourceSpec.class.getName(),mycontext.getBundle());
    }
    
    private void stop(){
        publisher.destroyEndpoint(MyDummyRessourceSpec.class.getName());
    }
}
