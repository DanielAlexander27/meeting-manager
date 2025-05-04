package org.intiandes.central.mediator;

import org.intiandes.central.domain.service.MeetingService;
import org.intiandes.central.observer.MeetingSubject;
import org.intiandes.central.server.ClientHandler;
import org.intiandes.common.model.Meeting;
import org.intiandes.common.request.CreateMeetingRequest;

import java.util.HashMap;
import java.util.Map;

public class MeetingMediator {
    private final MeetingService meetingService;
    private final Map<Integer, MeetingSubject> meetingSubjectPool;

    MeetingMediator(MeetingService meetingService) {
        this.meetingService = meetingService;
        this.meetingSubjectPool = new HashMap<>();
    }

    void scheduleMeeting(CreateMeetingRequest createMeetingRequest) {
        final Meeting meeting = createMeetingRequest.meeting;

        meetingService.scheduleMeeting(meeting, createMeetingRequest.employeeGuestsId);

        final MeetingSubject meetingSubject = new MeetingSubject();

        for (String employeeName : meeting.getGuestEmployees()) {
            final ClientHandler clientHandler = ClientHandler.clientHandlers.get(employeeName);

            if (clientHandler != null) {
                meetingSubject.registerObserver(clientHandler);
            }
        }

        meetingSubjectPool.put(meeting.getId(), meetingSubject);
    }

    void updateMeeting(Meeting meeting) {
        meetingService.updateMeeting(meeting);

        final MeetingSubject meetingSubject = meetingSubjectPool.get(meeting.getId());

        final String message = String.format("The meeting with the topic %s has been updated.", meeting.getTopic());
        meetingSubject.notifyObservers(message);
    }
}
