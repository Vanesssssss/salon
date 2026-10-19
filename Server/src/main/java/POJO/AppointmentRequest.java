package POJO;

import java.io.Serializable;

public class AppointmentRequest implements Serializable {
    private int clientId;      // ID клиента
    private int employeeId;    // ID мастера
    private int serviceId;     // ID услуги
    private String slotDate;   // Дата слота
    private String slotTime;   // Время слота

    // Конструктор
    public AppointmentRequest(int clientId, int employeeId, int serviceId, String slotDate, String slotTime) {
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.serviceId = serviceId;
        this.slotDate = slotDate;
        this.slotTime = slotTime;
    }

    // Геттеры и сеттеры
    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(String slotDate) {
        this.slotDate = slotDate;
    }

    public String getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }

    @Override
    public String toString() {
        return "AppointmentRequest{" +
                "clientId=" + clientId +
                ", employeeId=" + employeeId +
                ", serviceId=" + serviceId +
                ", slotDate='" + slotDate + '\'' +
                ", slotTime='" + slotTime + '\'' +
                '}';
    }
}
