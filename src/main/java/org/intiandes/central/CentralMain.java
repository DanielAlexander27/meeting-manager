package org.intiandes.central;

import org.intiandes.central.mediator.MeetingMediator;
import org.intiandes.central.repository.meeting.MeetingRepository;
import org.intiandes.central.repository.meeting.MeetingRepositoryMemoryImpl;
import org.intiandes.central.server.CentralServer;

import java.io.IOException;
import java.net.ServerSocket;

public class CentralMain {
    public static void main(String[] args) throws IOException {
        System.out.println("Central server started!");
        ServerSocket serverSocket = new ServerSocket(9091);

        final MeetingRepository meetingRepository = new MeetingRepositoryMemoryImpl();
        final MeetingMediator meetingMediator = new MeetingMediator(meetingRepository);

        CentralServer centralServer = new CentralServer(serverSocket, meetingMediator);
        centralServer.startServer();
    }
}
