package com.abnamro.resteasy;

import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.io.IOException;

/**
 * A test utility class that provides you with an in-memory jaxrs server that does not support CDI. This
 * implementation uses Undertow.
 */
public class InMemoryRestServer extends AbstractRestServer implements AutoCloseable {
    private InMemoryRestServer(Object... objects) {
        append(objects);
    }

    /**
     * Create instance and pass given instances/resource/provider classes
     */
    public static InMemoryRestServer instance(Object... objects) throws IOException {
        InMemoryRestServer inMemoryRestServer = new InMemoryRestServer(objects);
        inMemoryRestServer.start();
        return inMemoryRestServer;
    }

    protected UndertowJaxrsServer buildServer() {
        UndertowJaxrsServer jaxrsServer = new UndertowJaxrsServer();

        ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        registerBeans(deployment, getBeans());
        registerResourcesAndProviders(deployment, getResourceAndProviderClasses());

        DeploymentInfo deploymentInfo = jaxrsServer.undertowDeployment(deployment, "/");
        deploymentInfo.setClassLoader(InMemoryRestServer.class.getClassLoader());
        deploymentInfo.setDeploymentName("Undertow + RestEasy example");
        deploymentInfo.setContextPath("");

        return jaxrsServer.deploy(deploymentInfo);
    }
}
