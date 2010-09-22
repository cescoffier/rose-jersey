package org.ow2.chameleon.rose.rest;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;
import org.ow2.chameleon.rose.server.EndpointFactory;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

/**
 * This component provides a REST, Jersey based implementation of an
 * EndpointFactory service provider.
 * @author Jonathan Bardin <jonathan.bardin@imag.fr>
 */
public class JerseyEndpointFactory implements EndpointFactory, IoCComponentProviderFactory {

    public static final String[] CONFIGS = {"jersey"};

    /**
     * The HTTPService available on this gateway, injected by iPOJO.
     */
    private HttpService httpservice;

    /**
     * The LogService, injected by iPOJO.
     */
    private LogService logger;

    /**
     * The Servlet name of the jersey bridge.
     */
    private String servletname;


    /*------------------------------------*
     *  Component Life-cycle methods      *
     *------------------------------------*/

    /**
     * Execute while this instance is starting. Call by iPOJO.
     */
    @SuppressWarnings("unused")
    private void start() {
        Dictionary<String, String> properties = new Hashtable<String, String>();

        try {
            // Registered the JerseyServletBridge
            httpservice.registerServlet(servletname, new JerseyServletBridge(this), properties, null);
        } catch (NamespaceException e) {
            logger.log(LogService.LOG_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            logger.log(LogService.LOG_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Execute while this instance is stopping. Call by iPOJO.
     */
    @SuppressWarnings("unused")
    private void stop() {
        try {
            if (httpservice != null) {
                httpservice.unregister(servletname);
            }
        } catch (RuntimeException re) {
            logger.log(LogService.LOG_ERROR, re.getMessage(), re);
        }
    }

    /*------------------------------------*
     *  EndpointFactory methods           *
     *------------------------------------*/

    /*
     * (non-Javadoc)
     * @see
     * org.ow2.chameleon.rose.server.EndpointFactory#createEndpoint(java.lang
     * .Object, java.util.Map)
     */
    public void createEndpoint(final Object pService, final Map<String, String> properties) throws IllegalArgumentException {
    }

    /*
     * (non-Javadoc)
     * @see
     * org.ow2.chameleon.rose.server.EndpointFactory#createEndpoint(java.lang
     * .Object, java.lang.ClassLoader, java.util.Map)
     */
    public void createEndpoint(final Object pService, final ClassLoader pLoader, final Map<String, String> properties){
    }

    /*
     * (non-Javadoc)
     * @see
     * org.ow2.chameleon.rose.server.EndpointFactory#destroyEndpoint(java.lang
     * .String)
     */
    public void destroyEndpoint(String pService) throws IllegalArgumentException, NullPointerException {
    }

    /*------------------------------------------------*
     *  IoCComponentProviderFactory methods           *
     *------------------------------------------------*/

    /*
     * (non-Javadoc)
     * @see com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory#getComponentProvider(java.lang.Class)
     */
    public IoCComponentProvider getComponentProvider(Class<?> klass) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory#getComponentProvider(com.sun.jersey.core.spi.component.ComponentContext, java.lang.Class)
     */
    public IoCComponentProvider getComponentProvider(ComponentContext ccontext, Class<?> klass) {
        // TODO Auto-generated method stub
        return null;
    }
}
