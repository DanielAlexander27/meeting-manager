package org.intiandes.common.model;

public class EmployeeMeeting {
    private final int id;
    private final int employeeId;
    private final int meetingId;

    public EmployeeMeeting(int id, int employeeId, int meetingId) {
        this.id = id;
        this.employeeId = employeeId;
        this.meetingId = meetingId;
    }

    public EmployeeMeeting(int employeeId, int meetingId) {
        this(0, employeeId, meetingId);
    }

    public int getId() {
        return id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getMeetingId() {
        return meetingId;
    }
}
