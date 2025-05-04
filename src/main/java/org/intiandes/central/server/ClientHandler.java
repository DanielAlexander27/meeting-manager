package org.intiandes.central.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    public static final Map<String, ClientHandler> clientHandlers = new HashMap<>();

    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());

            String hostName = socket.getInetAddress().getHostName().split("\\.")[0];
            clientHandlers.put(hostName, this);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                Object receivedObject = this.objectInputStream.readObject();

            } catch (Exception e) {
                break;
            }
        }
    }
}
