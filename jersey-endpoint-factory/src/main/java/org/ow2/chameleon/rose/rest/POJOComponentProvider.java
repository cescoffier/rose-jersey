package org.ow2.chameleon.rose.rest;

import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;


public class POJOComponentProvider implements IoCComponentProvider {
    private final Object instance;
    
    public POJOComponentProvider(Object service) {
        instance = service;
    }
    
    public Object getInstance() {
        return instance;
    }
}
