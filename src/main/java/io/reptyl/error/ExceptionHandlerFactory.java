package io.reptyl.error;

import io.reptyl.error.exception.UnsupportedResponseStatusAnnotationException;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.ExceptionHandler;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.reflections.Reflections;

@Singleton
public class ExceptionHandlerFactory {

    final RoutingHandler routingHandler;

    @Inject
    public ExceptionHandlerFactory(
            final RoutingHandler routingHandler) {
        this.routingHandler = routingHandler;
    }

    public ExceptionHandler getFromPackage(String packageName) {

        ExceptionHandler exceptionHandler = new ExceptionHandler(routingHandler);

        new Reflections(packageName)
                .getTypesAnnotatedWith(ResponseStatus.class)
                .forEach(clazz -> {

                    if (!Exception.class.isAssignableFrom(clazz)) {
                        throw new UnsupportedResponseStatusAnnotationException(clazz);
                    }

                    @SuppressWarnings("unchecked")   // this cast is actually safe
                    Class<? extends Exception> exceptionClass = (Class<Exception>) clazz;

                    exceptionHandler.addExceptionHandler(exceptionClass, new ErrorHandler(exceptionClass.getAnnotation(ResponseStatus.class).value()));
                });

        return exceptionHandler;
    }
}
