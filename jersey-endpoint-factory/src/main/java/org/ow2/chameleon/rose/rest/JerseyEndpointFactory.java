package org.ow2.chameleon.rose.rest;

import static org.osgi.service.log.LogService.LOG_INFO;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;
import org.ow2.chameleon.rose.server.EndpointFactory;

/**
 * This component provides a REST, Jersey based implementation of an
 * EndpointFactory service provider.
 * @author Jonathan Bardin <jonathan.bardin@imag.fr>
 */
public class JerseyEndpointFactory implements EndpointFactory {

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
     * The Servlet name of the jersey containers.
     */
    private Set<String> servletNames = new HashSet<String>();
    

    private MyDummyRessourceSpec dummy;
    
    /*------------------------------------*
     *  Component Life-cycle methods      *
     *------------------------------------*/

    /**
     * Execute while this instance is starting. Call by iPOJO.
     */
    @SuppressWarnings("unused")
    private void start() {
    	OSGiComponentProviderFactory pro = new OSGiComponentProviderFactory(logger);
    	pro.createProvider(dummy, MyDummyRessourceSpec.class);
    }

    /**
     * Execute while this instance is stopping. Call by iPOJO.
     */
    @SuppressWarnings("unused")
    private void stop() {
            for (Iterator<String> iterator = servletNames.iterator(); iterator.hasNext();) {
                String name = iterator.next();
                try {
                    httpservice.unregister(name);
                } catch (RuntimeException re) {
                    logger.log(LogService.LOG_ERROR, re.getMessage(), re);
                }
                iterator.remove();
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

        if (!properties.containsKey(PROP_INTERFACE_NAME)){
            throw new IllegalArgumentException("The properties must contain the "+PROP_INTERFACE_NAME+ " key");
        }
        
        if (!properties.containsKey(PROP_ENDPOINT_NAME)){
            throw new IllegalArgumentException("The properties must contain the "+PROP_ENDPOINT_NAME+ " key");
        }
        
        
        String classname = properties.get(PROP_INTERFACE_NAME);
        String name = properties.get(PROP_ENDPOINT_NAME);

        if (servletNames.contains(name)){
            throw new IllegalArgumentException("A ressource with the path "+name+" has already been published !");
        }
                
        Class<?> klass;
        try {
            klass = pLoader.loadClass(classname);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("The given classloader does not contains the class: "+classname,e);
        }
        
        OSGiComponentProviderFactory providerFact = new OSGiComponentProviderFactory(logger);
			providerFact.createProvider(pService,klass);

			servletNames.add(name);
        
        logger.log(LogService.LOG_INFO, "org.ow2.chameleon.rose.server.EndpointFactory-REST starting");
        try {
            // Register the JerseyServletBridge
            httpservice.registerServlet("/rest",new JerseyServletContainer(providerFact), null, null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(LogService.LOG_ERROR, "Cannot register JerseyServletBridge ",e);
        } 
        
        logger.log(LOG_INFO, "The ressource: "+name+" has been succesfully registered");
    }

    /*
     * (non-Javadoc)
     * @see
     * org.ow2.chameleon.rose.server.EndpointFactory#destroyEndpoint(java.lang
     * .String)
     */
    public void destroyEndpoint(String pName) throws IllegalArgumentException, NullPointerException {
        if (!servletNames.contains(pName)){
            throw new IllegalArgumentException("The ressource of path "+pName+" has never been exposed");
        }
        
        try {
            if (httpservice != null) {
                httpservice.unregister("/rest");
            }
        } catch (RuntimeException re) {
            logger.log(LogService.LOG_ERROR, re.getMessage(), re);
        }
        
        logger.log(LOG_INFO, "The ressource: "+pName+" has been succesfully unregistered");
    }

}
