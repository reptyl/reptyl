package io.reptyl.test.integration.package1;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Singleton
public class Controller2 {

    @GET
    @Path("/test2")
    public void method1() {

    }
}
