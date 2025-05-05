package org.intiandes.common.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class GetEmployeesResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public final List<String> employeeNames;

    public GetEmployeesResponse(List<String> employeeNames) {
        this.employeeNames = employeeNames;
    }
}
