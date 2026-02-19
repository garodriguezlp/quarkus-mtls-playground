///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
// Update the Quarkus version to what you want here or run jbang with
// `-Dquarkus.version=<version>` to override it.
//DEPS io.quarkus:quarkus-bom:${quarkus.version:3.15.1}@pom
//DEPS io.quarkus:quarkus-rest
//DEPS io.quarkus:quarkus-rest-jackson
// //DEPS io.quarkus:quarkus-smallrye-openapi
// //DEPS io.quarkus:quarkus-swagger-ui
//JAVAC_OPTIONS -parameters

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/echo")
@ApplicationScoped
public class QServer {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EchoResponse echo(EchoRequest request) {
        return new EchoResponse("message received");
    }

}

record EchoRequest(String message) {}

record EchoResponse(String echo) {}
