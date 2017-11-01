package io.reptyl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.reptyl.error.GlobalExceptionHandler;
import io.reptyl.route.RoutingHandlerFactory;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.ExceptionHandler;
import javax.inject.Named;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.xnio.Options.WORKER_NAME;

public class ReptylModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReptylModule.class);

    private static final String ROOT_HANDLER = "ROOT_HANDLER";

    @Provides
    @Singleton
    public Undertow undertowBuilder(
            final ServerConfiguration serverConfiguration,
            final @Named(ROOT_HANDLER) HttpHandler httpHandler) {

        return Undertow
                .builder()
                .addHttpListener(serverConfiguration.getPort(), serverConfiguration.getHost())
                .setWorkerOption(WORKER_NAME, serverConfiguration.getWorkerName())
                .setHandler(httpHandler)
                .build();
    }

    @Provides
    @Singleton
    public RoutingHandler routingHandler(
            final ServerConfiguration serverConfiguration,
            final RoutingHandlerFactory routingHandlerFactory) {

        RoutingHandler routingHandler;

        if (serverConfiguration.getScanPackage() != null) {
            routingHandler = routingHandlerFactory.getFromPackage(serverConfiguration.getScanPackage());
        } else {
            routingHandler = new RoutingHandler();
        }

        serverConfiguration
                .getControllers()
                .stream()
                .map(routingHandlerFactory::fromClass)
                .forEach(routingHandler::addAll);

        return routingHandler;
    }

    @Provides
    @Singleton
    public ExceptionHandler exceptionHandler(final RoutingHandler routingHandler) {

        return new ExceptionHandler(routingHandler);
    }

    @Provides
    @Singleton
    @Named(ROOT_HANDLER)
    public HttpHandler httpHandler(final GlobalExceptionHandler globalExceptionHandler) {

        return new BlockingHandler(globalExceptionHandler);
    }

    @Override
    protected void configure() {

        LOGGER.debug("Reptyl configuration completed");
    }
}
