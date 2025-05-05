package org.intiandes.employee.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EmployeeServer {
    private Socket socket;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public EmployeeServer(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            closeEverything();
        }
    }

    public Object sendRequest(Object request) {
        try {
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            Object response = objectInputStream.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("There was an error sending the request. Please try again.");
            return null;
        }
    }

    public void listenForStringMessages() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    Object objectReceived = objectInputStream.readObject();

                    if (objectReceived instanceof String message) {
                        System.out.println(message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    closeEverything();
                    break;
                }
            }
        }).start();
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
