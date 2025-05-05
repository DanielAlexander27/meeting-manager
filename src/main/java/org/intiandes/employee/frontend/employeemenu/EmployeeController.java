package org.intiandes.employee.frontend.employeemenu;

import org.intiandes.common.model.Meeting;
import org.intiandes.common.request.CreateMeetingRequest;
import org.intiandes.common.request.GetEmployeesRequest;
import org.intiandes.common.request.GetMeetingsRequest;
import org.intiandes.common.response.GetEmployeesResponse;
import org.intiandes.common.response.SendMeetingsResponse;
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

        if (response instanceof GetEmployeesResponse employeesResponse) {
            return employeesResponse.employeeNames;
        }

        return List.of();
    }

    public void sendCreateMeetingRequest(Meeting newMeeting) {
        employeeServer.sendRequest(new CreateMeetingRequest(newMeeting));
    }

    public List<Meeting> getMyMeetings() {
        final Object response = employeeServer.sendRequest(new GetMeetingsRequest(EmployeeMain.EMPLOYEE_USERNAME));

        if (response instanceof SendMeetingsResponse meetingsResponse) {
            return meetingsResponse.meetings;
        }

        return List.of();
    }
}
