package org.intiandes.employee;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class EmployeeMain {
    private static String hostName = "localhost";

    public static void main(String[] args) throws IOException {
        System.out.println(InetAddress.getLocalHost());

        Socket socket = new Socket(hostName, 9091);

        System.out.println(socket.getLocalAddress().getHostName());
        while (!socket.isClosed()) {
        }
//        Socket socket = new Socket("central-server", 9091);
    }
}
