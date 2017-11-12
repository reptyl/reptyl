package io.reptyl.test.unit.route;

import io.reptyl.route.RoutingHandlerFactory;
import io.reptyl.route.RouteFactory;
import io.reptyl.route.exception.EmptyControllerException;
import io.reptyl.route.exception.NonSingletonControllerException;
import io.undertow.server.RoutingHandler;
import javax.inject.Singleton;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.googlecode.catchexception.CatchException.verifyException;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoutingHandlerFactoryTest {

    @Mock
    private RouteFactory routeFactory;

    RoutingHandlerFactory routingHandlerFactory;

    @Before
    public void setUp() throws Exception {

        routingHandlerFactory = new RoutingHandlerFactory(routeFactory);
    }

    @Test
    public void nonSingletonClassesShouldBeRejected() {

        verifyException(routingHandlerFactory, NonSingletonControllerException.class).fromClass(RoutingHandlerFactoryTest.class);
    }

    @Test
    public void emptyControllerClassesShouldBeIgnored() {

        verifyException(routingHandlerFactory, EmptyControllerException.class).fromClass(Controller.class);
    }

    @Singleton
    private static class Controller {

    }
}
