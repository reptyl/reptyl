package io.reptyl.test.unit.route.package3;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Singleton
@Path("/base")
public class Controller {

    @GET
    public void method() {

    }
}
