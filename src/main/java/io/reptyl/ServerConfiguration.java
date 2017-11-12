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

    private Collection<Class<?>> controllers = new ArrayList<>();

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

    public void addController(Class<?> clazz) {
        controllers.add(clazz);
    }

    public void addControllers(Collection<Class<?>> clazz) {
        controllers.addAll(clazz);
    }

    public Collection<Class<?>> getControllers() {
        return unmodifiableCollection(controllers);
    }
}
