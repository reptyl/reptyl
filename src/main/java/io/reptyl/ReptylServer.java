package io.reptyl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.undertow.Undertow;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReptylServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReptylServer.class);

    private final Undertow undertow;

    @Inject
    private ReptylServer(final Undertow undertow) {
        this.undertow = undertow;
    }

    public void start() {
        LOGGER.info("starting reptyl server");
        undertow.start();
    }

    public void stop() {
        LOGGER.info("stopping reptyl server");
        undertow.stop();
    }

    public static ReptylServer.Builder builder() {
        return new ReptylServer.Builder(Guice.createInjector(new ReptylModule()));
    }

    public static class Builder {

        public static final Integer DEFAULT_PORT = 8080;
        public static final String DEFAULT_HOST = "localhost";
        public static final String DEFAULT_WORKER_NAME = "Reptyl";

        private final Injector injector;
        private final ServerConfiguration serverConfiguration;

        public Builder(Injector injector) {
            this.injector = injector;
            this.serverConfiguration = injector.getInstance(ServerConfiguration.class);
        }

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

        public Builder scanPackage(String packageName) {
            this.serverConfiguration.setScanPackage(packageName);
            return this;
        }

        public Builder scanCurrentPackage() {

            try {
                serverConfiguration.setScanPackage(
                        Class.forName(
                                Thread.currentThread()
                                        .getStackTrace()[2]
                                        .getClassName())
                                .getPackage()
                                .getName());
            } catch (ClassNotFoundException e) {
                throw new AssertionError("a class already in stack cannot be missed by Class.forName()");
            }

            return this;
        }

        public Builder withController(Class<?> clazz) {
            this.serverConfiguration.addController(clazz);
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

            return injector.getInstance(ReptylServer.class);
        }
    }
}
