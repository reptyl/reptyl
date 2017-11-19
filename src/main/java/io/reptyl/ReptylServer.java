package io.reptyl;

import io.reptyl.error.DefaultExceptionHandler;
import io.reptyl.request.binding.BindingFactory;
import io.reptyl.request.handler.MethodInvokerHandlerFactory;
import io.reptyl.route.RouteFactory;
import io.reptyl.route.RoutingHandlerFactory;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.xnio.Options.WORKER_NAME;

public class ReptylServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReptylServer.class);

    private final Undertow undertow;

    private final ServerConfiguration serverConfiguration;

    @Inject
    private ReptylServer(
            final Undertow undertow,
            final ServerConfiguration serverConfiguration) {
        this.undertow = undertow;
        this.serverConfiguration = serverConfiguration;
    }

    public void start() {
        LOGGER.info("starting reptyl server");
        undertow.start();
    }

    public void stop() {
        LOGGER.info("stopping reptyl server");
        undertow.stop();
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public static ReptylServer.Builder builder() {
        return new ReptylServer.Builder();
    }

    public static class Builder {

        public static final Integer DEFAULT_PORT = 8080;
        public static final String DEFAULT_HOST = "localhost";
        public static final String DEFAULT_WORKER_NAME = "Reptyl";

        private final ServerConfiguration serverConfiguration = new ServerConfiguration();
        private HttpHandler httpHandler;
        private Undertow undertow;

        public Builder port(Integer port) {
            this.serverConfiguration.setPort(port);
            return this;
        }

        public Builder host(String host) {
            this.serverConfiguration.setHost(host);
            return this;
        }

        public Builder workerName(String worker) {
            this.serverConfiguration.setWorkerName(worker);
            return this;
        }

        public Builder withController(Controller controller) {
            this.serverConfiguration.addController(controller);
            return this;
        }

        public Builder withHttpHandler(HttpHandler httpHandler) {
            this.httpHandler = httpHandler;
            return this;
        }

        public Builder withUndertow(Undertow undertow) {
            this.undertow = undertow;
            return this;
        }

        public ReptylServer build() {

            LOGGER.debug("creating reptyl server");

            if (serverConfiguration.getHost() == null) {
                serverConfiguration.setHost(DEFAULT_HOST);
            }

            if (serverConfiguration.getPort() == null) {
                serverConfiguration.setPort(DEFAULT_PORT);
            }

            if (serverConfiguration.getWorkerName() == null) {
                serverConfiguration.setWorkerName(DEFAULT_WORKER_NAME);
            }

            if (httpHandler == null) {

                DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

                BindingFactory bindingFactory = new BindingFactory();
                MethodInvokerHandlerFactory methodInvokerHandlerFactory = new MethodInvokerHandlerFactory(defaultExceptionHandler);

                RouteFactory routeFactory = new RouteFactory(bindingFactory, methodInvokerHandlerFactory);
                RoutingHandlerFactory routingHandlerFactory = new RoutingHandlerFactory(routeFactory);

                RoutingHandler httpHandler = new RoutingHandler();

                serverConfiguration
                        .getControllers()
                        .stream()
                        .map(routingHandlerFactory::fromController)
                        .forEach(httpHandler::addAll);

                this.httpHandler = httpHandler;
            }

            if (undertow == null) {

                undertow = Undertow
                        .builder()
                        .addHttpListener(serverConfiguration.getPort(), serverConfiguration.getHost())
                        .setWorkerOption(WORKER_NAME, serverConfiguration.getWorkerName())
                        .setHandler(httpHandler)
                        .setIoThreads(1)
                        .setWorkerThreads(1)
                        .build();
            }

            return new ReptylServer(undertow, serverConfiguration);
        }
    }
}
