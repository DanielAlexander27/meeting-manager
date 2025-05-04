package org.intiandes.central.server;

import org.intiandes.central.domain.service.MeetingService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralServer {
    private ServerSocket serverSocket;
    private final MeetingService meetingService;

    public CentralServer(ServerSocket serverSocket, MeetingService meetingService) {
        this.serverSocket = serverSocket;
        this.meetingService = meetingService;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, meetingService)).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
