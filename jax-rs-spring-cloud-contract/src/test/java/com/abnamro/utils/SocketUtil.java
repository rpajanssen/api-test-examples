package com.abnamro.utils;

import java.io.IOException;
import java.net.ServerSocket;

public final class SocketUtil {
    public final static int findFreePort() throws IOException {
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();

        return port;
    }
}
