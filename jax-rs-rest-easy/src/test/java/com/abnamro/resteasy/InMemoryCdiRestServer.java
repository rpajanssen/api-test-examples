package com.abnamro.resteasy;

import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.io.IOException;

/**
 * A test utility class that provides you with an in-memory jaxrs server that fully supports CDI. This
 * implementation uses Undertow.
 */
public class InMemoryCdiRestServer extends AbstractRestServer implements AutoCloseable {
    private InMemoryCdiRestServer(Object... objects) {
        append(objects);
    }

    /**
     * Create instance and pass given instances/resource/provider classes
     */
    public static InMemoryCdiRestServer instance(Object... objects) throws IOException {
        InMemoryCdiRestServer inMemoryRestServer = new InMemoryCdiRestServer(objects);
        inMemoryRestServer.start();
        return inMemoryRestServer;
    }

    protected UndertowJaxrsServer buildServer() {
        UndertowJaxrsServer jaxrsServer = new UndertowJaxrsServer();

        ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        registerBeans(deployment, getBeans());
        registerResourcesAndProviders(deployment, getResourceAndProviderClasses());
        deployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());

        DeploymentInfo deploymentInfo = jaxrsServer.undertowDeployment(deployment, "/");
        deploymentInfo.setClassLoader(InMemoryCdiRestServer.class.getClassLoader());
        deploymentInfo.setDeploymentName("Undertow + RestEasy CDI example");
        deploymentInfo.setContextPath("");

        deploymentInfo.addListener(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));

        return jaxrsServer.deploy(deploymentInfo);
    }
}
