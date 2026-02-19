///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
// Update the Quarkus version to what you want here or run jbang with
// `-Dquarkus.version=<version>` to override it.
//DEPS io.quarkus:quarkus-bom:${quarkus.version:3.15.1}@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS io.quarkus:quarkus-rest-client
//DEPS io.quarkus:quarkus-rest-client-jackson
//Q:CONFIG quarkus.banner.enabled=false
//Q:CONFIG quarkus.log.level=WARN

import picocli.CommandLine;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.quarkus.runtime.QuarkusApplication;

@QuarkusMain
@CommandLine.Command(name = "qclient", mixinStandardHelpOptions = true)
public class QClient implements QuarkusApplication, Runnable {

    @CommandLine.Option(names = "--message", description = "Message to send", defaultValue = "hello")
    String message;

    @Inject
    CommandLine.IFactory factory;

    @Inject
    @RestClient
    EchoApi echoApi;

    @Override
    public int run(String... args) {
        return new CommandLine(this, factory).execute(args);
    }

    @Override
    public void run() {
        EchoResponse response = echoApi.echo(new EchoRequest(message));
        System.out.println(response.echo());
    }

}

@RegisterRestClient(configKey = "echo-api")
@Path("/echo")
@Dependent
interface EchoApi {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    EchoResponse echo(EchoRequest request);
}

record EchoRequest(String message) {}

record EchoResponse(String echo) {}