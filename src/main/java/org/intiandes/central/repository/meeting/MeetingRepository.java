package org.intiandes.central.repository.meeting;

import org.intiandes.common.model.Meeting;

public interface MeetingRepository {
    Meeting createMeeting(Meeting meeting);
    Meeting updateMeeting(Meeting meeting);
}
