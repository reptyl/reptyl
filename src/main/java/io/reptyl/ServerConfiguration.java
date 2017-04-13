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

    private String scanPackage;

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

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public void addController(Class<?> clazz) {
        controllers.add(clazz);
    }

    public Collection<Class<?>> getControllers() {
        return unmodifiableCollection(controllers);
    }
}
