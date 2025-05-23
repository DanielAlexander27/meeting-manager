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

            String username = (String) this.objectInputStream.readObject();
            System.out.println("SERVER: The employee with username " + username + " has connected to the server!");
            clientHandlers.put(username, this);
        } catch (IOException | ClassNotFoundException e) {
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
                        meetingMediator.scheduleMeeting(this, createMeetingRequest.meeting);
                        break;
                    case GetMeetingsRequest getMeetingsRequest:
                        meetingMediator.sendMeetingsAssociatedToEmployee(this, getMeetingsRequest.getEmployeeName());
                        break;
                    case UpdateMeetingRequest updateMeetingRequest:
                        meetingMediator.updateMeeting(this, updateMeetingRequest.meeting);
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
            this.objectOutputStream.reset();
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
