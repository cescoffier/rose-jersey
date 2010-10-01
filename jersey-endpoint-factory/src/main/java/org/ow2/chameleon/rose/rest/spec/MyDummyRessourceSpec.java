package org.ow2.chameleon.rose.rest.spec;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("dummy")
public interface MyDummyRessourceSpec {

    @GET
    @Produces(TEXT_PLAIN)
    public String hello();
}
