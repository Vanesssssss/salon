package POJO;

import java.io.Serializable;
import java.util.List;

public class AssignServicesRequest implements Serializable {
    private int employeeId;
    private List<Integer> serviceIds;

    public AssignServicesRequest(int employeeId, List<Integer> serviceIds) {
        this.employeeId = employeeId;
        this.serviceIds = serviceIds;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    @Override
    public String toString() {
        return "AssignServicesRequest{" +
                "employeeId=" + employeeId +
                ", serviceIds=" + serviceIds +
                '}';
    }
}
