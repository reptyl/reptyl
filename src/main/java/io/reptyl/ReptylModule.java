package io.reptyl;

import io.reptyl.error.ExceptionHandlerFactory;
import io.reptyl.route.RoutingHandlerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.ExceptionHandler;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.xnio.Options.WORKER_NAME;

public class ReptylModule extends AbstractModule {

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

        return routingHandlerFactory.getFromPackage(serverConfiguration.getScanPackage());
    }

    @Provides
    @Singleton
    public ExceptionHandler exceptionHandler(
            final ServerConfiguration serverConfiguration,
            final ExceptionHandlerFactory exceptionHandlerFactory,
            final RoutingHandler routingHandler) {

        return exceptionHandlerFactory.getFromPackage(serverConfiguration.getScanPackage());
    }

    @Provides
    @Singleton
    @Named(ROOT_HANDLER)
    public HttpHandler httpHandler(final ExceptionHandler exceptionHandler) {

        return new BlockingHandler(exceptionHandler);
    }

    @Override
    protected void configure() {

    }
}
