package org.intiandes.central.server;

import org.intiandes.central.mediator.MeetingMediator;
import org.intiandes.common.request.CreateMeetingRequest;
import org.intiandes.common.request.GetEmployeesRequest;
import org.intiandes.common.request.GetMeetingsRequest;
import org.intiandes.common.request.UpdateMeetingRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    public static final Map<String, ClientHandler> clientHandlers = new HashMap<>();

    private Socket socket;
    private MeetingMediator meetingMediator;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public ClientHandler(Socket socket, MeetingMediator meetingMediator) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.meetingMediator = meetingMediator;

            String hostName = socket.getInetAddress().getHostName().split("\\.")[0];
            clientHandlers.put(hostName, this);
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                Object receivedObject = this.objectInputStream.readObject();

                switch (receivedObject) {
                    case CreateMeetingRequest createMeetingRequest:
                        System.out.println("Received request to create a meeting");
                        meetingMediator.scheduleMeeting(createMeetingRequest.meeting);
                        break;
                    case GetMeetingsRequest getMeetingsRequest:
                        meetingMediator.sendMeetingsAssociatedToEmployee(getMeetingsRequest.getEmployeeName());
                        break;
                    case UpdateMeetingRequest updateMeetingRequest:
                        meetingMediator.updateMeeting(updateMeetingRequest.meeting);
                        break;
                    case GetEmployeesRequest ignored:
                        meetingMediator.sendEmployeeNames(this);
                        break;
                    default:
                        break;
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
