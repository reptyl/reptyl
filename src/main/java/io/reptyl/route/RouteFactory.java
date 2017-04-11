package io.reptyl.route;

import io.reptyl.request.binding.Binding;
import io.reptyl.request.binding.BindingFactory;
import io.reptyl.request.handler.MethodInvokerHandlerFactory;
import io.reptyl.route.exception.EmptyBasePathException;
import io.reptyl.route.exception.EmptyControllerPathException;
import io.reptyl.route.exception.MalformedControllerPathException;
import io.reptyl.route.exception.NonPublicControllerException;
import io.reptyl.route.exception.NonVoidReturningControllerException;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Singleton
public class RouteFactory {

    private static final RoutingHandler EMPTY_ROUTING_HANDLER = new RoutingHandler();

    private final BindingFactory bindingFactory;
    private final MethodInvokerHandlerFactory methodInvokerHandlerFactory;

    @Inject
    public RouteFactory(
            final BindingFactory bindingFactory,
            final MethodInvokerHandlerFactory methodInvokerHandlerFactory) {
        this.bindingFactory = bindingFactory;
        this.methodInvokerHandlerFactory = methodInvokerHandlerFactory;
    }

    public RoutingHandler getRoutingHandler(Method method, @Nullable String basePath) {

        requireNonNull(method, "method should not be null");

        if (!Modifier.isPublic(method.getModifiers())) {
            throw new NonPublicControllerException(method);
        }

        if (!method.getReturnType().equals(Void.TYPE)) {
            throw new NonVoidReturningControllerException(method);
        }

        // normalize the base path
        if (basePath == null) {
            basePath = "/";
        }

        if (basePath.isEmpty()) {
            throw new EmptyBasePathException(method);
        }

        Optional<String> controllerPath = Optional.ofNullable(method.getAnnotation(Path.class))
                .map(Path::value)
                .map(String::trim);

        controllerPath.ifPresent(path -> {

            if (path.isEmpty()) {
                throw new EmptyControllerPathException(method);
            }

            if (!path.startsWith("/")) {
                throw new MalformedControllerPathException(path, method);
            }
        });

        List<Annotation> annotations = Arrays.asList(method.getAnnotations());

        List<String> httpMethodAnnotations = annotations
                .stream()
                .flatMap(annotation -> Arrays.stream(annotation.annotationType().getAnnotations())
                        .filter(HttpMethod.class::isInstance)
                        .map(HttpMethod.class::cast)
                        .map(HttpMethod::value))
                .collect(toList());

        // if this method is not annotated with any http method annotation
        if (httpMethodAnnotations.isEmpty()) {
            return EMPTY_ROUTING_HANDLER;
        }

        StringBuilder pathBuilder = new StringBuilder();

        // determine the path of the current controller

        if (!basePath.equals("/") || !controllerPath.isPresent()) {
            pathBuilder.append(basePath);
        }

        controllerPath.ifPresent(pathBuilder::append);

        String path = pathBuilder.toString();

        // create a binding between each method argument and the http request component its
        // annotation refers to
        List<Binding<?>> bindings = Arrays.stream(method.getParameters())
                .map(bindingFactory::getParameterBinding)
                .collect(toList());

        // create the request handler
        HttpHandler httpHandler = methodInvokerHandlerFactory.getHandler(method.getDeclaringClass(), method, bindings);

        RoutingHandler routingHandler = new RoutingHandler();

        // a controller can be handle requests of different http methods, so every configured
        // method must be bound the the current handler
        httpMethodAnnotations.forEach(httpMethod -> routingHandler.add(httpMethod, path, httpHandler));

        return routingHandler;
    }
}
