package io.reptyl.test.unit.route;

import io.reptyl.route.RoutingHandlerFactory;
import io.reptyl.route.RouteFactory;
import io.undertow.server.RoutingHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    @Before
    public void setUp() throws Exception {

        when(routeFactory.getRoutingHandler(any(), any())).thenReturn(new RoutingHandler());
    }

    @Test
    public void nonSingletonClassesShouldBeIgnored() {

        RoutingHandlerFactory routingHandlerFactory = new RoutingHandlerFactory(routeFactory);

        RoutingHandler routingHandler = routingHandlerFactory.getFromPackage("io.reptyl.test.unit.route.package1");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        verify(routeFactory, times(1)).getRoutingHandler(any(), any());
    }

    @Test
    public void nonAnnotatedMethodsShouldBeIgnored() {

        RoutingHandlerFactory routingHandlerFactory = new RoutingHandlerFactory(routeFactory);

        RoutingHandler routingHandler = routingHandlerFactory.getFromPackage("io.reptyl.test.unit.route.package2");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        verify(routeFactory, times(1)).getRoutingHandler(any(), any());
    }

    @Test
    public void basePathShouldBePassedToTheRouteFactory() {

        RoutingHandlerFactory routingHandlerFactory = new RoutingHandlerFactory(routeFactory);

        RoutingHandler routingHandler = routingHandlerFactory.getFromPackage("io.reptyl.test.unit.route.package3");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        verify(routeFactory, times(1)).getRoutingHandler(any(), eq("/base"));
    }

    @Test
    public void emptyBasePathShouldBeNormalized() {

        RoutingHandlerFactory routingHandlerFactory = new RoutingHandlerFactory(routeFactory);

        RoutingHandler routingHandler = routingHandlerFactory.getFromPackage("io.reptyl.test.unit.route.package4");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        verify(routeFactory, times(1)).getRoutingHandler(any(), eq("/"));
    }
}
