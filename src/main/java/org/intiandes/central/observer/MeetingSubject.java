package org.intiandes.central.observer;

import org.intiandes.central.server.ClientHandler;

import java.util.ArrayList;
import java.util.List;

public class MeetingSubject {
    private List<ClientHandler> observers = new ArrayList<>();

    public void registerObserver(ClientHandler clientHandler) {
        observers.add(clientHandler);
    }

    public void removeObserver(ClientHandler clientHandler) {
        observers.remove(clientHandler);
    }

    public void notifyObservers(Object message) {
        observers.forEach(clientHandler -> clientHandler.sendMessage(message));
    }

}
