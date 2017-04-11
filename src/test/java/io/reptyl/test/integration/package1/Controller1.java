package io.reptyl.test.integration.package1;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Singleton
public class Controller1 {

    @GET
    @Path("/test1")
    public void method1() {

    }
}
