package org.intiandes.common.request;

import java.io.Serial;
import java.io.Serializable;

public class GetMeetingsRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String employeeName;

    public GetMeetingsRequest(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeName() {
        return employeeName;
    }
}
