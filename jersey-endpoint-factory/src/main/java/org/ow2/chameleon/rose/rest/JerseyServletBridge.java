package org.ow2.chameleon.rose.rest;

import java.util.Map;

import javax.servlet.ServletException;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;


public class JerseyServletBridge extends ServletContainer{

    private static final long serialVersionUID = -1399500555655064091L;
    
    private final IoCComponentProviderFactory providerFactory;
    
    
    public JerseyServletBridge(IoCComponentProviderFactory pProviderFactory) {
        super();
        providerFactory = pProviderFactory;
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig wc) throws ServletException {
        return new DefaultResourceConfig();
    }
    
    @Override
    protected void initiate(ResourceConfig rsc, WebApplication webApp) {
        webApp.initiate(rsc,providerFactory);
    }
}
