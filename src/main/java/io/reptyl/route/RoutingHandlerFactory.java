package io.reptyl.route;

import io.reptyl.Controller;
import io.reptyl.route.exception.EmptyControllerException;
import io.reptyl.route.exception.NonSingletonControllerException;
import io.undertow.server.RoutingHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

@Singleton
public class RoutingHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingHandlerFactory.class);

    private final RouteFactory routeFactory;

    @Inject
    public RoutingHandlerFactory(final RouteFactory routeFactory) {
        this.routeFactory = routeFactory;
    }

    public RoutingHandler fromController(Controller controller) {

        @SuppressWarnings("unchecked")
        Class<Controller> clazz = (Class<Controller>) controller.getClass();

        Singleton singletonAnnotation = clazz.getDeclaredAnnotation(Singleton.class);

        if (singletonAnnotation == null) {
            throw new NonSingletonControllerException(clazz);
        }

        List<Method> methods = methodsOfAsList(clazz);

        if (methods.isEmpty()) {
            throw new EmptyControllerException(clazz);
        }

        RoutingHandler routingHandler = new RoutingHandler();

        // determine the base path of the controllers defined in the current class
        String basePath = Optional.ofNullable(clazz.getAnnotation(Path.class))
                .map(Path::value)
                .orElse("/");

        methods.forEach(method -> {

            LOGGER.debug("found method {}", method);

            routingHandler.addAll(routeFactory.getRoutingHandler(controller, method, basePath));
        });

        return routingHandler;
    }

    private static List<Method> methodsOfAsList(Class<?> clazz) {
        return Stream.of(clazz.getDeclaredMethods())
                .filter(IS_HTTP_METHOD_ANNOTATED)
                .collect(toList());
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
