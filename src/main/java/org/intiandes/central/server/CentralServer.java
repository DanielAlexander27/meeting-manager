package org.intiandes.central.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralServer {
    private ServerSocket serverSocket;

    public CentralServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getPort()); // port associated with the user
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
