package org.intiandes.employee;

import org.intiandes.common.model.Meeting;
import org.intiandes.common.request.CreateMeetingRequest;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class EmployeeMain {
        private static String hostName = "localhost";
//    private static String hostName = "central-server";

    public static void main(String[] args) throws IOException {
        System.out.println(InetAddress.getLocalHost());

        Socket socket = new Socket(hostName, 9091);

        ObjectOutputStream ous = new ObjectOutputStream(socket.getOutputStream());

        ous.writeObject(
                new CreateMeetingRequest(
                        new Meeting("TOPIC", List.of("alice-white", "bob-smith"), "PLACE", System.currentTimeMillis(), System.currentTimeMillis(), "alice-white")
                )
        );
        ous.flush();
    }
}
