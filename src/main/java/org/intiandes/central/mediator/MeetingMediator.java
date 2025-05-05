package org.intiandes.central.mediator;

import org.intiandes.central.observer.MeetingSubject;
import org.intiandes.central.repository.meeting.MeetingRepository;
import org.intiandes.central.server.ClientHandler;
import org.intiandes.common.model.Meeting;
import org.intiandes.common.response.GetEmployeesResponse;
import org.intiandes.common.response.SendMeetingsResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingMediator {
    private final MeetingRepository meetingRepository;
    private final Map<Integer, MeetingSubject> meetingSubjectPool;

    public MeetingMediator(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingSubjectPool = new HashMap<>();
    }

    public void scheduleMeeting(Meeting newMeeting) {
        final Meeting meeting = meetingRepository.createMeeting(newMeeting);
        final MeetingSubject meetingSubject = new MeetingSubject();

        for (String employeeName : meeting.getGuestEmployees()) {
            final ClientHandler clientHandler = ClientHandler.clientHandlers.get(employeeName);

            if (clientHandler != null) {
                meetingSubject.registerObserver(clientHandler);
            }
        }

        meetingSubject.notifyObservers(String.format("SERVER: A new meeting with the topic %s has been scheduled.", meeting.getTopic()));
        meetingSubjectPool.put(meeting.getId(), meetingSubject);
    }

    public void updateMeeting(Meeting meeting) {
        meetingRepository.updateMeeting(meeting);
        final MeetingSubject meetingSubject = meetingSubjectPool.get(meeting.getId());
        final String message = String.format("The meeting with the topic %s has been updated.", meeting.getTopic());
        meetingSubject.notifyObservers(message);
    }

    public void sendEmployeeNames(ClientHandler clientHandler) {
        clientHandler.sendMessage(new GetEmployeesResponse(getEmployeeNames()));
    }

    public List<String> getEmployeeNames() {
        String employeeNamesEnv = System.getenv("EMPLOYEE_NAMES");

        if (employeeNamesEnv == null) return List.of();

        return Arrays.stream(employeeNamesEnv.split(",")).toList();
    }

    public void sendMeetingsAssociatedToEmployee(String employeeName) {
        sendMeetingsAssociatedToEmployee(employeeName, employeeName);
    }

    public void sendMeetingsAssociatedToEmployee(String employeeNameInterested, String employeeNameToSearch) {
        final List<Meeting> meetings = getMeetings(employeeNameToSearch);
        ClientHandler.clientHandlers.get(employeeNameInterested).sendMessage(new SendMeetingsResponse(meetings));
    }

    public List<Meeting> getMeetings(String employeeName) {
        return meetingRepository.getMeetingsByEmployeeName(employeeName);
    }
}
