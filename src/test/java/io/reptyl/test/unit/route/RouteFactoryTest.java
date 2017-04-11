package io.reptyl.test.unit.route;

import io.reptyl.request.binding.BindingFactory;
import io.reptyl.request.handler.MethodInvokerHandlerFactory;
import io.reptyl.route.RouteFactory;
import io.reptyl.route.exception.EmptyBasePathException;
import io.reptyl.route.exception.EmptyControllerPathException;
import io.reptyl.route.exception.MalformedControllerPathException;
import io.reptyl.route.exception.NonPublicControllerException;
import io.reptyl.route.exception.NonVoidReturningControllerException;
import io.undertow.server.RoutingHandler;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatcher;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import static io.reptyl.test.unit.HandlerTestUtils.getFirstTemplateString;
import static io.reptyl.test.unit.ReflectionTestUtils.getField;
import static com.googlecode.catchexception.CatchException.verifyException;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PUBLIC;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteFactoryTest {

    @Mock
    private BindingFactory bindingFactory;

    @Mock
    private MethodInvokerHandlerFactory methodInvokerHandlerFactory;

    @Mock
    private Method method;

    @Mock
    private GET getAnnotation;

    @Mock
    private POST postAnnotation;

    @Mock
    private Path pathAnnotation;

    private RouteFactory routeFactory;

    @Before
    public void setUp() throws Exception {

        routeFactory = new RouteFactory(bindingFactory, methodInvokerHandlerFactory);

        when(method.getModifiers()).thenReturn(PUBLIC);

        OngoingStubbing<Class<?>> returnType = when(method.getReturnType());
        returnType.thenReturn(Void.TYPE);

        when(method.getParameters()).thenReturn(new Parameter[0]);

        OngoingStubbing<Class<?>> when = when(method.getDeclaringClass());
        when.thenReturn(this.getClass());

        OngoingStubbing<Class<? extends Annotation>> getAnnotationClass = when(getAnnotation.annotationType());
        getAnnotationClass.thenReturn(GET.class);

        OngoingStubbing<Class<? extends Annotation>> postAnnotationClass = when(postAnnotation.annotationType());
        postAnnotationClass.thenReturn(POST.class);
    }

    @Test
    public void nonPublicMethodShouldBeRejected() {

        when(method.getModifiers()).thenReturn(PRIVATE);

        verifyException(routeFactory, NonPublicControllerException.class).getRoutingHandler(method, null);
    }

    @Test
    public void nonVoidReturningMethodsShouldBeRejected() {

        OngoingStubbing<Class<?>> returnType = when(method.getReturnType());
        returnType.thenReturn(Integer.TYPE);

        verifyException(routeFactory, NonVoidReturningControllerException.class).getRoutingHandler(method, null);
    }

    @Test
    public void controllerWithNoAnnotationsShouldReturnAnEmptyRoutingHandler() {

        when(method.getAnnotations()).thenReturn(new Annotation[0]);

        RoutingHandler routingHandler = routeFactory.getRoutingHandler(method, null);

        Map<HttpString, PathTemplateMatcher<?>> matches = getField(routingHandler, "matches");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        assertThat("the routing handler should be empty", matches.entrySet(), empty());
    }

    @Test
    public void controllerWithMethodAnnotationShouldReturnaValidRoutingHandler() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation });

        RoutingHandler routingHandler = routeFactory.getRoutingHandler(method, null);

        Map<HttpString, PathTemplateMatcher<?>> matches = getField(routingHandler, "matches");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        assertThat("the routing handler should contain one route", matches.entrySet(), hasSize(1));
        assertThat("the template string should be the default", getFirstTemplateString(matches), equalTo("/"));
    }

    @Test
    public void controllerWithMultipleMethodAnnotationsShouldReturnaValidRoutingHandler() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation, postAnnotation });

        RoutingHandler routingHandler = routeFactory.getRoutingHandler(method, null);

        Map<HttpString, PathTemplateMatcher<?>> matches = getField(routingHandler, "matches");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        assertThat("the routing handler should contain 2 routes", matches.entrySet(), hasSize(2));
    }

    @Test
    public void controllerShouldBeBoundToTheGivenPath() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation });
        when(method.getAnnotation(Path.class)).thenReturn(pathAnnotation);

        when(pathAnnotation.value()).thenReturn("/test");

        RoutingHandler routingHandler = routeFactory.getRoutingHandler(method, null);

        Map<HttpString, PathTemplateMatcher<?>> matches = getField(routingHandler, "matches");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        assertThat("the template string should be equal to the one configured in the annotation", getFirstTemplateString(matches), equalTo("/test"));
    }

    @Test
    public void emptyControllerPathShouldBeRejected() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation });
        when(method.getAnnotation(Path.class)).thenReturn(pathAnnotation);

        when(pathAnnotation.value()).thenReturn("");

        verifyException(routeFactory, EmptyControllerPathException.class).getRoutingHandler(method, null);
    }

    @Test
    public void emptyBasePathShouldBeRejected() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation });

        verifyException(routeFactory, EmptyBasePathException.class).getRoutingHandler(method, "");
    }

    @Test
    public void defaultControllerPathShouldBeIgnored() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation });
        when(method.getAnnotation(Path.class)).thenReturn(pathAnnotation);

        when(pathAnnotation.value()).thenReturn("/");

        RoutingHandler routingHandler = routeFactory.getRoutingHandler(method, null);

        Map<HttpString, PathTemplateMatcher<?>> matches = getField(routingHandler, "matches");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        assertThat("the template string should be the default", getFirstTemplateString(matches), equalTo("/"));
    }

    @Test
    public void controllerShouldBeBoundToTheGivenPathUsingTheBasePath() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation });
        when(method.getAnnotation(Path.class)).thenReturn(pathAnnotation);

        when(pathAnnotation.value()).thenReturn("/test");

        RoutingHandler routingHandler = routeFactory.getRoutingHandler(method, "/base");

        Map<HttpString, PathTemplateMatcher<?>> matches = getField(routingHandler, "matches");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        assertThat(
                "the template string should be equal to the one configured in the annotation prefixed with the base path",
                getFirstTemplateString(matches),
                equalTo("/base/test"));
    }

    @Test
    public void defaultBasePathShouldBeIgnored() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation });
        when(method.getAnnotation(Path.class)).thenReturn(pathAnnotation);

        when(pathAnnotation.value()).thenReturn("/test");

        RoutingHandler routingHandler = routeFactory.getRoutingHandler(method, "/");

        Map<HttpString, PathTemplateMatcher<?>> matches = getField(routingHandler, "matches");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        assertThat(
                "the template string should be equal to the one configured in the annotation",
                getFirstTemplateString(matches),
                equalTo("/test"));
    }

    @Test
    public void defaultBaseAndControllerPathsShouldBeIgnored() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation });
        when(method.getAnnotation(Path.class)).thenReturn(pathAnnotation);

        when(pathAnnotation.value()).thenReturn("/");

        RoutingHandler routingHandler = routeFactory.getRoutingHandler(method, "/");

        Map<HttpString, PathTemplateMatcher<?>> matches = getField(routingHandler, "matches");

        assertThat("a RoutingHandler should be returned", routingHandler, notNullValue());
        assertThat("the template string should be the default", getFirstTemplateString(matches), equalTo("/"));
    }

    @Test
    public void malformedControllerPathShouldBeRejected() {

        when(method.getAnnotations()).thenReturn(new Annotation[] { getAnnotation });
        when(method.getAnnotation(Path.class)).thenReturn(pathAnnotation);

        when(pathAnnotation.value()).thenReturn("test");   // ht apath does not start with a slash

        verifyException(routeFactory, MalformedControllerPathException.class).getRoutingHandler(method, null);
    }
}
