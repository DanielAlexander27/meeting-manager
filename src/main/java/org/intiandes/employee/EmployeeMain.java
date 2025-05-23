package org.intiandes.employee;

import org.intiandes.employee.frontend.employeemenu.EmployeeController;
import org.intiandes.employee.frontend.employeemenu.EmployeeMenuConsole;
import org.intiandes.employee.server.EmployeeServer;

import java.io.IOException;
import java.net.Socket;

public class EmployeeMain {
//            private static String HOST_NAME = "localhost";
//            public static String EMPLOYEE_USERNAME = "bob-smith";

    private static String HOST_NAME = "central-server";
    public static String EMPLOYEE_USERNAME = System.getenv("EMPLOYEE_USERNAME");
    public static String EMPLOYEE_NAME = System.getenv("EMPLOYEE_NAME");
    public static String MEETINGS_FILE_PATH = "employee-output/meetings_" + EMPLOYEE_USERNAME + ".txt";

    public static void main(String[] args) throws IOException {
        EmployeeMenuConsole.showWelcome();

        Socket socket = new Socket(HOST_NAME, 9091);
        final EmployeeServer employeeServer = new EmployeeServer(socket);
        employeeServer.sendUsername();
        employeeServer.listenForStringMessages();

        final EmployeeController controller = new EmployeeController(employeeServer);
        final EmployeeMenuConsole consoleProgram = new EmployeeMenuConsole(controller);

        consoleProgram.showMenu();
    }
}
