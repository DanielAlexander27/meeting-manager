package org.intiandes.central.server;

import org.intiandes.central.mediator.MeetingMediator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralServer {
    private ServerSocket serverSocket;
    private final MeetingMediator meetingMediator;

    public CentralServer(ServerSocket serverSocket, MeetingMediator meetingService) {
        this.serverSocket = serverSocket;
        this.meetingMediator = meetingService;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, meetingMediator)).start();
            }
        } catch (IOException e) {
            closeServer();
        }
    }

    private void closeServer() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
