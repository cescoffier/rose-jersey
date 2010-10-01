package org.ow2.chameleon.rose.rest;

import static org.osgi.service.log.LogService.LOG_ERROR;

import javax.ws.rs.Path;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;
import org.ow2.chameleon.rose.rest.ext.RestEndpointFactory;

/**
 * This component provides a REST, Jersey based implementation of an
 * EndpointFactory service provider.
 * @author Jonathan Bardin <jonathan.bardin@imag.fr>
 */
public class JerseyEndpointFactory implements RestEndpointFactory {

    public static final String[] CONFIGS = {"jersey"};

    /**
     * The HTTPService available on this gateway, injected by iPOJO.
     */
    private HttpService httpservice;

    /**
     * The LogService, injected by iPOJO.
     */
    private LogService logger;
    
    private OSGiComponentProviderFactory provider = new OSGiComponentProviderFactory();

    /**
     * 
     */
    private JerseyServletBridge container = null;

    private String rootName;

    /*------------------------------------*
     *  Component Life-cycle methods      *
     *------------------------------------*/

    /**
     * Execute while this instance is starting. Call by iPOJO.
     */
    @SuppressWarnings("unused")
    private void start() {

    }

    /**
     * Execute while this instance is stopping. Call by iPOJO.
     */
    @SuppressWarnings("unused")
    private void stop() {
        if (container != null) {
            httpservice.unregister(rootName);
        }
    }

    /**
     * Singleton
     * @return
     */
    private JerseyServletBridge getContainer() {
        if (container == null) {
            container = new JerseyServletBridge(provider);
            try {
                httpservice.registerServlet(rootName, container, null, null);
            } catch (Exception e) {
                logger.log(LOG_ERROR, "Cannot register the JerseyServlet bridge",e);
                throw new RuntimeException("Cannot register the JerseyServlet bridge",e);
            }
        }
        
        return container;
    }

    public void createEndpoint(Object instance, String className, Bundle bundle) {
           
        try {
            Class<?> klass = bundle.loadClass(className);
            provider.createManagedComponentProvider(instance, klass);
            getContainer().load();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
    }

    public void destroyEndpoint(String className) {
        httpservice.unregister("/rest");
    }

}
