package org.intiandes.central.domain.service;

import org.intiandes.central.repository.employeemeeting.EmployeeMeetingRepository;
import org.intiandes.central.repository.meeting.MeetingRepository;
import org.intiandes.common.model.EmployeeMeeting;
import org.intiandes.common.model.Meeting;

import java.util.List;

public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final EmployeeMeetingRepository employeeMeetingRepository;

    public MeetingService(MeetingRepository meetingRepository, EmployeeMeetingRepository employeeMeetingRepository) {
        this.meetingRepository = meetingRepository;
        this.employeeMeetingRepository = employeeMeetingRepository;
    }

    public void scheduleMeeting(Meeting meeting, List<Integer> employeeGuestsId) {
        final int meetingId = meetingRepository.createMeeting(meeting).getId();

        employeeGuestsId.forEach(employeeId ->
                employeeMeetingRepository.createEmployeeMeeting(new EmployeeMeeting(employeeId, meetingId))
        );
    }

    public Meeting updateMeeting(Meeting meeting) {
        return meetingRepository.updateMeeting(meeting);
    }

    public List<Meeting> getMeetings(String employeeName) {
        return meetingRepository.getMeetingsByEmployeeName(employeeName);
    }
}
