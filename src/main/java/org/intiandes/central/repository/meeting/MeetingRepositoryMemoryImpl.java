package org.intiandes.central.repository.meeting;

import org.intiandes.common.model.Meeting;

import java.util.HashMap;
import java.util.Map;

public class MeetingRepositoryMemoryImpl implements MeetingRepository {
    private static int currentId = 0;
    private final Map<Integer, Meeting> meetingsTable = new HashMap<>();

    @Override
    public Meeting createMeeting(Meeting meeting) {
        currentId++;
        meetingsTable.put(currentId, new Meeting(currentId, meeting));
        return meetingsTable.get(currentId);
    }

    @Override
    public Meeting updateMeeting(Meeting meeting) {
        if (meetingsTable.containsKey(meeting.getId())) {
            meetingsTable.put(meeting.getId(), meeting);
            return meetingsTable.get(meeting.getId());
        } else {
            throw new IllegalArgumentException("Meeting with id " + meeting.getId() + " does not exist!");
        }
    }
}
