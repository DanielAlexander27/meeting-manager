package org.intiandes.central;

import org.intiandes.central.database.H2Database;
import org.intiandes.central.server.CentralServer;

import java.io.IOException;
import java.net.ServerSocket;

public class CentralMain {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        CentralServer centralServer = new CentralServer(serverSocket);
        centralServer.startServer();

//        new H2Database().initializeDB();
    }
}
