package org.ow2.chameleon.rose.rest.auto;

import static org.osgi.framework.Constants.SERVICE_ID;
import static org.osgi.service.log.LogService.LOG_WARNING;

import javax.ws.rs.Path;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.ow2.chameleon.rose.server.EndpointFactory;

public class GenericEndpointFactory implements ServiceTrackerCustomizer {

    private final static String FILTER = "(|(service.exported.configs=jsr311)(service.exported.configs=jersey))";

    private EndpointFactory exporter;

    private BundleContext context;

    private LogService logger;

    private ServiceTracker tracker;

    public GenericEndpointFactory(BundleContext pContext) {
        context = pContext;
        try {
            tracker = new ServiceTracker(context, context.createFilter(FILTER), this);
        } catch (InvalidSyntaxException e) {
            assert (false);
        }
    }

    private void start() {
        tracker.open();
    }

    private void stop() {
        tracker.close();
    }

    public Object addingService(ServiceReference reference) {
        final Object service = context.getService(reference);

        try {
            exporter.createEndpoint(service, null);
            return service;
        } catch (Exception e) {
            logger.log(LOG_WARNING, "Cannot create the  an endpoint for service: " + String.valueOf(reference.getProperty(SERVICE_ID)), e);
            return null;
        }
    }

    public void modifiedService(ServiceReference reference, Object service) {
    }

    public void removedService(ServiceReference reference, Object service) {
        final Class<?> klass = service.getClass();

        if (!klass.isAnnotationPresent(Path.class)) {
            logger.log(LOG_WARNING, "Unable to destroy the ressource assosiated to the given Service, the service is not a valid ressource.");
            return;
        }

        // unpublished the endpoint
        try {
            exporter.destroyEndpoint(klass.getAnnotation(Path.class).value());
        } catch (Exception e) {
            logger.log(LOG_WARNING, "Cannot destroy the ressource for service: " + String.valueOf(reference.getProperty(SERVICE_ID)), e);
        }
    }

}
