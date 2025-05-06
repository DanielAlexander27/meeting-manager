package org.intiandes.employee.server;

import org.intiandes.employee.EmployeeMain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EmployeeServer {
    private Socket socket;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final BlockingQueue<Object> responseQueue = new LinkedBlockingQueue<>();

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

            return responseQueue.take();
        } catch (IOException | InterruptedException | RuntimeException e) {
            System.out.println("There was an error sending the request. Please try again.");
            return null;
        }
    }

    public void sendUsername() {
        try {
            objectOutputStream.writeObject(EmployeeMain.EMPLOYEE_USERNAME);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenForStringMessages() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    Object objectReceived = objectInputStream.readObject();

                    if (objectReceived instanceof String message) {
                        System.out.println(message);
                    } else {
                        System.out.println(objectReceived);
                        responseQueue.put(objectReceived);
                    }
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
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
