package com.abnamro.resteasy;

import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRestServer implements AutoCloseable {
    private String host = "localhost";
    private int port;

    private Set<Object> beans = new HashSet<>();
    private Set<Class> resourceAndProviderClasses = new HashSet<>();

    private UndertowJaxrsServer server;

    protected abstract UndertowJaxrsServer buildServer();

    protected void append(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Class) {
                resourceAndProviderClasses.add((Class) object);
            } else {
                this.beans.add(object);
            }
        }
    }

    protected void start() throws IOException {
        port = findFreePort();

        server = buildServer();

        server.start(Undertow.builder().addHttpListener(port, host));
    }

    protected void registerBeans(ResteasyDeployment deployment, Set<Object> beans) {
        for (Object object : beans) {
            if (object instanceof Application) {
                deployment.setApplication((Application) object);
            } else {
                if (object.getClass().isAnnotationPresent(Path.class)) {
                    deployment.getResources().add(object);
                } else if (object.getClass().isAnnotationPresent(Provider.class)) {
                    deployment.getProviders().add(object);
                } else {
                    if(!resourceDetectedAndDeployed(deployment, object)) {
                        deployment.getDefaultContextObjects().put(object.getClass(), object);
                    }
                }
            }
        }
    }

    private boolean resourceDetectedAndDeployed(ResteasyDeployment deployment, Object object) {
        Class[] interfaces = object.getClass().getInterfaces();
        for (Class anInterface: interfaces) {
            if (anInterface.isAnnotationPresent(Path.class)) {
                deployment.getResources().add(object);

                return true;
            }
        }

        return false;
    }

    protected void registerResourcesAndProviders(ResteasyDeployment deployment, Set<Class> resourcesAndProviders) {
        for (Class resourceOrProvider : resourcesAndProviders) {
            if (Application.class.isAssignableFrom(resourceOrProvider)) {
                deployment.setApplicationClass(resourceOrProvider.getName());
            } else {
                deployment.getProviderClasses().add(resourceOrProvider.getName());
            }
        }
    }

    private static int findFreePort() throws IOException {
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();

        return port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Set<Object> getBeans() {
        return beans;
    }

    public Set<Class> getResourceAndProviderClasses() {
        return resourceAndProviderClasses;
    }

    @Override
    public void close() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
}
