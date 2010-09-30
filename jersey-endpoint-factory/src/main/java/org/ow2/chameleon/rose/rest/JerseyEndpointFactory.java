package org.ow2.chameleon.rose.rest;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;
import org.ow2.chameleon.rose.server.EndpointFactory;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCManagedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCProxiedComponentProvider;

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
    
    /**
     * Path to instance
     */
    private Map<String,Object> pathToInstance = new HashMap<String, Object>();
    
    
    private ResourceConfig rsconfig = new DefaultResourceConfig();
    
    private RessourceService myressource;
    
    ResourceConfig getResourceConfig(){
        return rsconfig;
    }
    

    /*------------------------------------*
     *  Component Life-cycle methods      *
     *------------------------------------*/

    /**
     * Execute while this instance is starting. Call by iPOJO.
     */
    @SuppressWarnings("unused")
    private void start() {
        pathToInstance.put("test", myressource);
        rsconfig.getClasses().add(RessourceService.class);
        Dictionary<String, String> properties = new Hashtable<String, String>();
        logger.log(LogService.LOG_INFO, "org.ow2.chameleon.rose.server.EndpointFactory-REST starting");
        try {
            // Registered the JerseyServletBridge
            httpservice.registerServlet(servletname, new JerseyServletBridge(this), properties, null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(LogService.LOG_ERROR, "Cannot register JerseyServletBridge ",e);
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
    public IoCComponentProvider getComponentProvider(final Class<?> klass) {
        //String name = klass.getAnnotation(javax.ws.rs.Path.class).value();
        //System.out.println("PAth value: "+name);
        Path path = klass.getAnnotation(javax.ws.rs.Path.class);
        
        if (path !=null && pathToInstance.containsKey(path.value())){
            return new OSGiManagedComponentProvider(path.value(), klass);
        }
        
        System.out.println(klass.getCanonicalName());
        return new IoCProxiedComponentProvider() {
            
            public Object proxy(Object arg0) {
                // TODO Auto-generated method stub
                System.out.println("proxy called");
                return klass.cast(arg0);
            }
            
            public Object getInstance() {
                try {
                    System.out.println("getInstance called");
                    return klass.newInstance();
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    /*
     * (non-Javadoc)
     * @see com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory#getComponentProvider(com.sun.jersey.core.spi.component.ComponentContext, java.lang.Class)
     */
    public IoCComponentProvider getComponentProvider(ComponentContext ccontext, Class<?> klass) {
        // TODO Auto-generated method stub
        //String name = klass.getAnnotation(javax.ws.rs.Path.class).value();
        //System.out.println("PAth value: "+name);
        System.out.println(klass.getCanonicalName());
        return getComponentProvider(klass);
    }
    
    /*------------------------------------------------*
     *  ComponentProvider Class                       *
     *------------------------------------------------*/
    
    /**
     * If an instance of ManagedComponentProvider is returned then the
     * component is fully managed by the underlying IoC framework, which
     * includes managing the construction, injection and destruction according
     * to the life-cycle declared in the IoC framework's semantics.
     */
    private class OSGiManagedComponentProvider implements IoCManagedComponentProvider {
        
        private final String name;
        private final Class klass;

        OSGiManagedComponentProvider(String pName, Class pKlass) {
            System.out.println("OSGi managed "+ pName);
            name = pName;
            klass = pKlass;
        }

        /**
         * For now we support only the singleton pattern.
         */
        public ComponentScope getScope() {
            return ComponentScope.Singleton;
        }

        public Object getInjectableInstance(Object o) {
            //FIXME 
            System.out.println("Object name: " +o.toString());
            System.out.println("injectable "+((RessourceService) o).hello());
            
            return o;
        }

        public Object getInstance() 
        {
            System.out.println("" + pathToInstance.get(name));
            return klass.cast(pathToInstance.get(name));
        }
        
    }
    
}
