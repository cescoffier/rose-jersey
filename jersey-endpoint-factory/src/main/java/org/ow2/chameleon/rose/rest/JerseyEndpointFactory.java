package org.ow2.chameleon.rose.rest;

import static org.osgi.service.log.LogService.LOG_INFO;

import java.util.Hashtable;
import java.util.Map;

import javax.ws.rs.Path;

import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;
import org.ow2.chameleon.rose.rest.provider.ManagedComponentProvider;
import org.ow2.chameleon.rose.rest.provider.ProxiedComponentProvider;
import org.ow2.chameleon.rose.server.EndpointFactory;

import com.sun.jersey.api.core.ResourceConfig;
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
     * 
     */
    private JerseyServletBridge container = null;

    private MyResourceConfig rsconfig = new MyResourceConfig();

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
     * Register the servlet contrainer or reload it. Must be called after adding
     * or removing a class into the ResourceConfig.
     */
    private void reloadServlet() {
        if (container == null) {
            container = new JerseyServletBridge(this);
            try {
                httpservice.registerServlet(rootName, container, new Hashtable<String, Object>(), null);
            } catch (Exception e) {
                throw new RuntimeException("Cannot register the JerseyServlet bridge", e);
            }
        } else {
            container.reload();
        }
    }

    /*----------------------------*
     *   EndpointFactory methods  *
     *----------------------------*/

    public void destroyEndpoint(String pathName) {
        rsconfig.removeComponentProvider(pathName);
        try {
            container.reload();
        } catch (Exception e) {
            //no big deal
        }
        logger.log(LOG_INFO, "The ressource: " + pathName + " is no more available.");
    }

    public void createEndpoint(Object pService, Map<String, String> properties) throws IllegalArgumentException {
        addRessource(pService, pService.getClass());
        logger.log(LOG_INFO, "The ressource: " + pService.getClass().getAnnotation(Path.class).value() + " is now available.");
    }

    public void createEndpoint(Object pService, ClassLoader pLoader, Map<String, String> properties) throws IllegalArgumentException {
        String className = properties.get(PROP_INTERFACE_NAME);
        if (className == null) {
            addRessource(pService, pService.getClass());
        } else {
            try {
                Class<?> klass = pLoader.loadClass(className);
                addRessource(pService, klass);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("The class " + PROP_INTERFACE_NAME + " must be reachable from the given classloader", e);
            }
        }

    }

    /**
     * Create a ManagedComponentProvider and register it.
     * @param instance
     * @param klass
     * @throws IllegalArgumentException
     */
    public void addRessource(Object instance, Class<?> klass) throws IllegalArgumentException {
        // Create the managed component provider and add the class to the
        // ressource config
        rsconfig.addComponentProvider(klass, new ManagedComponentProvider(instance));

        // reload the servlet
        try {
            reloadServlet();
        } catch (RuntimeException e) {
            rsconfig.removeComponentProvider(klass.getAnnotation(Path.class).value());
            throw e;
        }
    }

    /*------------------------------------------------*
     *  IoCComponentProviderFactory methods           *
     *------------------------------------------------*/

    /**
     * @return The ResourceConfig of this IoCComponentProviderFactory
     */
    ResourceConfig getResourceConfig() {
        return rsconfig;
    }

    /*
     * (non-Javadoc)
     * @seecom.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory#
     * getComponentProvider(java.lang.Class)
     */
    public IoCComponentProvider getComponentProvider(final Class<?> klass) {
        return getComponentProvider(null, klass);
    }

    /*
     * (non-Javadoc)
     * @seecom.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory#
     * getComponentProvider(com.sun.jersey.core.spi.component.ComponentContext,
     * java.lang.Class)
     */
    public IoCComponentProvider getComponentProvider(ComponentContext ccontext, final Class<?> klass) {
        System.out.println("Get component Provider " + klass.getCanonicalName());
        // TODO What about the context

        // Singleton case
        // Check if an instance is available as an OSGi services
        if (rsconfig.isManaged(klass)) {
            // The ressource is an OSGi service, return an
            // OSGiManagedComponentProvider
            return rsconfig.getComponentProvider(klass);
        }

        // Otherwise ?
        // For now, we let jersey handle the creation of the ressource
        return new ProxiedComponentProvider(klass);
    }

}
