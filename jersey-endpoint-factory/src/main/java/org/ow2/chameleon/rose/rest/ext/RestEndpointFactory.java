package org.ow2.chameleon.rose.rest.ext;

import org.osgi.framework.Bundle;


public interface RestEndpointFactory {
    public void createEndpoint(Object instance,String className,Bundle bundle);
    public void destroyEndpoint(String className);
}
