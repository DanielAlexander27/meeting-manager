package org.intiandes.common.response;

import org.intiandes.common.model.Meeting;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SendMeetingsResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public final List<Meeting> meetings;

    public SendMeetingsResponse(List<Meeting> meetings) {
        this.meetings = meetings;
    }
}
