package org.intiandes.central.observer;

import org.intiandes.central.server.ClientHandler;

import java.util.HashSet;
import java.util.Set;

public class MeetingSubject {
    private Set<ClientHandler> observers = new HashSet<>();

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
