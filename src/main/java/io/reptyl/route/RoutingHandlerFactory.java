package io.reptyl.route;

import io.undertow.server.RoutingHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RoutingHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingHandlerFactory.class);

    private final RouteFactory routeFactory;

    @Inject
    public RoutingHandlerFactory(final RouteFactory routeFactory) {
        this.routeFactory = routeFactory;
    }

    public RoutingHandler getFromPackage(String packageName) {

        RoutingHandler routingHandler = new RoutingHandler();

        Map<Class<?>, String> basePaths = new HashMap<>();

        new Reflections(packageName)
                .getTypesAnnotatedWith(Singleton.class)
                .stream()

                // keep only classes having method annotate with JAX-RS annotations
                .filter(singleton -> methodsOf(singleton).anyMatch(IS_HTTP_METHOD_ANNOTATED))

                .peek(singleton -> {

                    LOGGER.debug("found class {}", singleton);

                    // determine the base path of the controllers defined in the current class
                    String basePath = Optional.ofNullable(singleton.getAnnotation(Path.class))
                            .map(Path::value)
                            .orElse("/");

                    basePaths.put(singleton, basePath);

                })

                .flatMap(singleton -> methodsOf(singleton).filter(IS_HTTP_METHOD_ANNOTATED))

                .forEach(method -> {

                    LOGGER.debug("found method {}", method);

                    routingHandler.addAll(routeFactory.getRoutingHandler(method, basePaths.get(method.getDeclaringClass())));
                });

        return routingHandler;
    }

    private static Stream<Method> methodsOf(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods());
    }

    private static final Predicate<Method> IS_HTTP_METHOD_ANNOTATED = method -> {

        for (Annotation annotation : method.getAnnotations()) {

            for (Annotation inheritedAnnotation : annotation.annotationType().getAnnotations()) {

                if (inheritedAnnotation instanceof HttpMethod) {
                    return true;
                }
            }
        }

        return false;
    };
}
