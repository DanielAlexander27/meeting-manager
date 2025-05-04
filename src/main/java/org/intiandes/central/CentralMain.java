package org.intiandes.central;

//import org.intiandes.central.server.CentralServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralMain {
    public static void main(String[] args) throws IOException {
        System.out.println("Central server started!");
        ServerSocket serverSocket = new ServerSocket(9091);

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            String hostName =  socket.getInetAddress().getHostName().split("\\.")[0];
            System.out.println(hostName);
//            System.out.println(socket.getInetAddress().getCanonicalHostName());
//            System.out.println(socket.getInetAddress().getHostName());
            System.out.println("Employee from port: " + socket.getPort());
        }

//        CentralServer centralServer = new CentralServer(serverSocket);
//        centralServer.startServer();

//        new H2Database().initializeDB();
    }
}
