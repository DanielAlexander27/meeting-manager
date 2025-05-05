package org.intiandes.common.request;

import org.intiandes.common.model.Meeting;

import java.io.Serial;
import java.io.Serializable;

public class UpdateMeetingRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public final Meeting meeting;

    public UpdateMeetingRequest(Meeting meeting) {
        this.meeting = meeting;
    }
}
