package io.reptyl.test.integration.package3;

import io.reptyl.Controller;
import io.undertow.server.HttpServerExchange;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Singleton
public class Controller4 implements Controller {

    @GET
    @Path("/test")
    public void test(HttpServerExchange exchange) {
        throw new TestException();
    }
}
