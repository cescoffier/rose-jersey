package org.ow2.chameleon.rose.rest;

import static com.sun.jersey.api.core.ResourceConfig.isProviderClass;
import static com.sun.jersey.api.core.ResourceConfig.isRootResourceClass;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.log.LogService;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCManagedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCProxiedComponentProvider;

/**
 * This component provides an OSGi based implementation of an
 * IoCComponentProviderFactory service provider.
 * @author Jonathan Bardin <jonathan.bardin@imag.fr>
 */
public class OSGiComponentProviderFactory implements IoCComponentProviderFactory {

    /**
     * The LogService, injected by iPOJO.
     */
    private LogService logger;

    /**
     * Path to ManagedComponentProvider
     */
    private Map<Class<?>,OSGiManagedComponentProvider> pathToInstance = new HashMap<Class<?>, OSGiManagedComponentProvider>();
    
    
    private ResourceConfig rsconfig = new DefaultResourceConfig();
    

    
    public OSGiComponentProviderFactory(LogService pLogger) {
        logger = pLogger;
    }
    
    /*------------------------------------------------*
     *  IoCComponentProviderFactory methods           *
     *------------------------------------------------*/
    
    /**
     * @return The ResourceConfig of this IoCComponentProviderFactory
     */
    ResourceConfig getResourceConfig(){
        return rsconfig;
    }

    /*
     * (non-Javadoc)
     * @see com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory#getComponentProvider(java.lang.Class)
     */
    public IoCComponentProvider getComponentProvider(final Class<?> klass) {
        return getComponentProvider(null, klass);
    }

    /*
     * (non-Javadoc)
     * @see com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory#getComponentProvider(com.sun.jersey.core.spi.component.ComponentContext, java.lang.Class)
     */
    public IoCComponentProvider getComponentProvider(ComponentContext ccontext,final Class<?> klass) {
        System.out.println("Get component Provider "+klass.getCanonicalName());
        //TODO What about the context

        //Singleton case
        //Check if an instance is available as an OSGi services
        if (pathToInstance.containsKey(klass)){
            //The ressource is an OSGi service, return an OSGiManagedComponentProvider
            return pathToInstance.get(klass);
        }

        //Otherwise ?
        //For now, we let jersey handle the creation of the ressource
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
        
        private final Object instance;
        private final Class<?> klass;

        OSGiManagedComponentProvider(Object pInstance, Class<?> pClass) {
            instance = pInstance;
            klass = pClass;
        }

        /**
         * Since the instance is an OSGi service, this is a singleton pattern !
         */
        public ComponentScope getScope() {
            return ComponentScope.Singleton;
        }

        public Object getInjectableInstance(Object o) {
            return o;
        }

        public Object getInstance() {
            return instance;
        }
    }
    
    
    public void createProvider(Object instance, Class<?> klass){
    	createProvider(klass);
        pathToInstance.put(klass,new OSGiManagedComponentProvider(instance,instance.getClass()));
    }
    
    public void createProvider(final Class<?> klass){
    	for (Annotation anno : klass.getAnnotations()) {
			System.out.println(anno.toString());
		}
        if (!(isRootResourceClass(klass) || isProviderClass(klass) )){
            throw new IllegalArgumentException("The class:"+klass.getCanonicalName()+" is not a root ressource or a provider class.");
        }
        getResourceConfig().getClasses().add(klass);
    }
    
}
