package org.intiandes.central.repository.employeemeeting;

import org.intiandes.common.model.EmployeeMeeting;

import java.util.HashMap;
import java.util.Map;

public class EmployeeMeetingMemoryImpl implements EmployeeMeetingRepository {
    private static int currentId = 0;
    private final Map<Integer, EmployeeMeeting> meetingsTable = new HashMap<>();

    @Override
    public EmployeeMeeting createEmployeeMeeting(EmployeeMeeting employeeMeeting) {
        currentId++;
        meetingsTable.put(currentId, new EmployeeMeeting(currentId, employeeMeeting.getEmployeeId(), employeeMeeting.getMeetingId()));
        return meetingsTable.get(currentId);
    }
}
