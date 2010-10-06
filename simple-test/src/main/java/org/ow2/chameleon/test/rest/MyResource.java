package org.ow2.chameleon.test.rest;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;

@Component(immediate = true, name = "myresource.component")
@Provides(specifications = {MyResource.class})
@Path("users/{username}")
public class MyResource {

    @ServiceProperty(name = "service.exported.configs")
    private String[] service_exported_configs = {"jsr311", "nodefault"};

    @Requires(filter = "(factory.state=1)")
    private Factory fac;

    @GET
    @Produces(TEXT_PLAIN)
    public String hello(@PathParam("username") String userName, @QueryParam("toto") String toto) {

        return "Salut " + userName + " tu sens le " + toto + " ! Oh une factory: " + fac.getName();
    }
}