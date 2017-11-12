package io.reptyl.test.unit.route;

import io.reptyl.route.RouteFactory;
import io.reptyl.route.RoutingHandlerFactory;
import io.reptyl.route.exception.EmptyControllerException;
import io.reptyl.route.exception.NonSingletonControllerException;
import javax.inject.Singleton;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RoutingHandlerFactoryTest {

    @Mock
    private RouteFactory routeFactory;

    RoutingHandlerFactory routingHandlerFactory;

    @Before
    public void setUp() throws Exception {

        routingHandlerFactory = new RoutingHandlerFactory(routeFactory);
    }

    @Test(expected = NonSingletonControllerException.class)
    public void nonSingletonClassesShouldBeRejected() {

        routingHandlerFactory.fromClass(RoutingHandlerFactoryTest.class);
    }

    @Test(expected = EmptyControllerException.class)
    public void emptyControllerClassesShouldBeIgnored() {

        routingHandlerFactory.fromClass(Controller.class);
    }

    @Singleton
    private static class Controller {

    }
}
