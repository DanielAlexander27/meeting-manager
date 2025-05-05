package org.intiandes.employee.frontend.employeemenu;

import org.intiandes.common.model.Meeting;
import org.intiandes.common.request.CreateMeetingRequest;
import org.intiandes.common.request.GetEmployeesRequest;
import org.intiandes.common.request.GetMeetingsRequest;
import org.intiandes.employee.EmployeeMain;
import org.intiandes.employee.server.EmployeeServer;

import java.util.ArrayList;
import java.util.List;

public class EmployeeController {
    private final EmployeeServer employeeServer;
    private List<String> employeeNames;

    public EmployeeController(EmployeeServer employeeServer) {
        this.employeeServer = employeeServer;
        this.employeeNames = new ArrayList<>();
    }

    public List<String> getEmployeeNames() {
        if (employeeNames.isEmpty()) {
            employeeNames = sendEmployeeRequest();
        }
        return employeeNames;
    }

    public List<String> sendEmployeeRequest() {
        final Object response = employeeServer.sendRequest(new GetEmployeesRequest());

        final List<String> employeeNames = new ArrayList<>();

        if (response instanceof List<?> employeeNamesResponse) {
            for (Object employeeName : employeeNamesResponse) {
                if (employeeName instanceof String employeeNameString) {
                    employeeNames.add(employeeNameString);
                }
            }
        }

        return employeeNames;
    }

    public void sendCreateMeetingRequest(Meeting newMeeting) {
        employeeServer.sendRequest(new CreateMeetingRequest(newMeeting));
    }

    public void viewMyMeetings() {
        final Object response = employeeServer.sendRequest(new GetMeetingsRequest(EmployeeMain.EMPLOYEE_USERNAME));

        final List<String> employeeNames = new ArrayList<>();

        if (response instanceof List<?> employeeNamesResponse) {
            for (Object employeeName : employeeNamesResponse) {
                if (employeeName instanceof String employeeNameString) {
                    employeeNames.add(employeeNameString);
                }
            }
        }


    }

}
