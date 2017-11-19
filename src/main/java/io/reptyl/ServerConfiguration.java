package io.reptyl;

import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Singleton;

import static java.util.Collections.unmodifiableCollection;

@Singleton
public class ServerConfiguration {

    private Integer port;

    private String host;

    private String workerName;

    private Collection<Controller> controllers = new ArrayList<>();

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public void addController(Controller controller) {
        this.controllers.add(controller);
    }

    public void addControllers(Collection<Controller> controllers) {
        this.controllers.addAll(controllers);
    }

    public Collection<Controller> getControllers() {
        return unmodifiableCollection(controllers);
    }
}
