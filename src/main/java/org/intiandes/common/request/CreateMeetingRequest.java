package org.intiandes.common.request;

import org.intiandes.common.model.Meeting;

import java.io.Serializable;
import java.util.List;

public class CreateMeetingRequest implements Serializable {
    public final Meeting meeting;
    public final List<Integer> employeeGuestsId;

    public CreateMeetingRequest(Meeting meeting, List<Integer> employeeGuestsId) {
        this.meeting = meeting;
        this.employeeGuestsId = employeeGuestsId;
    }
}
