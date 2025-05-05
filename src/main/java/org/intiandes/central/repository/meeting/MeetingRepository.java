package org.intiandes.central.repository.meeting;

import org.intiandes.common.model.Meeting;

import java.util.List;

public interface MeetingRepository {
    Meeting createMeeting(Meeting meeting);
    Meeting updateMeeting(Meeting meeting);
    List<Meeting> getMeetingsByEmployeeName(String employeeName);
}
