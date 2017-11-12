package io.reptyl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import io.undertow.Undertow;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

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

        XnioWorker worker = undertow.getWorker();
        Xnio xnio = undertow.getXnio();
    }

    public void stop() {
        LOGGER.info("stopping reptyl server");
        undertow.stop();

        XnioWorker worker = undertow.getWorker();
        Xnio xnio = undertow.getXnio();
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

        private final Collection<Module> modules = new ArrayList<>();
        private final ServerConfiguration serverConfiguration = new ServerConfiguration();

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


            Injector injector = Guice.createInjector(Modules.override(new ReptylModule()).with(modules));

            ServerConfiguration serverConfiguration = injector.getInstance(ServerConfiguration.class);
            serverConfiguration.setHost(this.serverConfiguration.getHost());
            serverConfiguration.setPort(this.serverConfiguration.getPort());
            serverConfiguration.setWorkerName(this.serverConfiguration.getWorkerName());
            serverConfiguration.addControllers(this.serverConfiguration.getControllers());

            return injector.getInstance(ReptylServer.class);
        }

        public Builder withModule(Module module) {
            modules.add(module);
            return this;
        }
    }
}
