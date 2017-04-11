package io.reptyl.test.integration.package2;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Singleton
public class Controller3 {

    @GET
    @Path("/test3")
    public void method1() {

    }
}
