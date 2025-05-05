package org.intiandes.central.server;

import org.intiandes.central.domain.service.MeetingService;
import org.intiandes.common.request.CreateMeetingRequest;

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
    private final MeetingService meetingService;

    public ClientHandler(Socket socket, MeetingService meetingService) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.meetingService = meetingService;

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

                if (receivedObject instanceof CreateMeetingRequest) {
//                    this.meetingService.scheduleMeeting((CreateMeetingRequest) receivedObject);
                }
            } catch (Exception e) {
                closeEverything();
                break;
            }
        }
    }

    public void sendMessage(Object contentToSend) {
        try {
            this.objectOutputStream.writeObject(contentToSend);
            this.objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void closeEverything() {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }

            if (objectInputStream != null) {
                objectInputStream.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
