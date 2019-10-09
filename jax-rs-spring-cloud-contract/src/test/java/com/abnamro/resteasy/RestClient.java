package com.abnamro.resteasy;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.ClientBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestClient implements Closeable {
    private final static String URL_TEMPLATE = "%s%s:%s";
    private final static String PROTOCOL = "http://";

    private final String host;
    private final int port;

    private final ResteasyClient restClient;

    public RestClient(String host, int port) {
        this.host = host;
        this.port = port;

        this.restClient = (ResteasyClient)ClientBuilder.newClient();
    }

    private String baseUri() {
        return String.format(URL_TEMPLATE, PROTOCOL, host, String.valueOf(port));
    }

    public ResteasyWebTarget target() {
        return restClient.target(baseUri());
    }

    public ResteasyWebTarget newRequest(String uriTemplate) {
        return newRequest(uriTemplate, null, null);
    }

    public ResteasyWebTarget newRequest(String uriTemplate, List<Class> providerClasses) {
        return newRequest(uriTemplate, null, providerClasses);
    }

    public ResteasyWebTarget newRequest(String uriTemplate, List<Object> beans, List<Class> providerClasses) {
        Optional.ofNullable(beans).orElse(new ArrayList<>()).forEach(restClient::register);
        Optional.ofNullable(providerClasses).orElse(new ArrayList<>()).forEach(restClient::register);

        return restClient.target(baseUri() + uriTemplate);
    }


    @Override
    public void close() throws IOException {
        restClient.close();
    }
}
