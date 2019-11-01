package com.abnamro.utils;

import com.abnamro.examples.dao.HardCodedPersonDAO;
import com.abnamro.examples.jaxrs.MyApplication;
import com.abnamro.examples.utils.InMemoryLogger;
import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.io.IOException;

public class ApplicationStarter {
    private UndertowJaxrsServer jaxrsServer;

    private int port;


    public void start() throws IOException {
        jaxrsServer = new UndertowJaxrsServer();

        ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        deployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
        deployment.setApplicationClass(MyApplication.class.getName());

        DeploymentInfo di = jaxrsServer.undertowDeployment(deployment, "/");
        di.setDeploymentName("Undertow + RestEasy CDI example")
                .setContextPath("").setClassLoader(MyApplication.class.getClassLoader());

        //Add CDI listener
        di.addListeners(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));

        jaxrsServer.deploy(di);

        port = SocketUtil.findFreePort();
        jaxrsServer.start(Undertow.builder().addHttpListener(port, "localhost"));
    }

    public int getPort() {
        return port;
    }

    public void stop() {
        jaxrsServer.stop();

        InMemoryLogger.reset();

        HardCodedPersonDAO.reset();
    }
}
