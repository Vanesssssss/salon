package POJO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class AppointmentRecord implements Serializable {
        private int appointmentId; // Уникальный идентификатор записи
        private int serviseId;
        private int clientId;
        private int employeeId;
        private String clientLastName;
        private String clientFirstName;
        private String clientPhoneNumber;
        private String employeeLastName;
        private String employeeFirstName;
        private String serviceName;
        private double servicePrice;
        private String appointmentDate;
        private String appointmentTime;

        public AppointmentRecord(int appointmentId, int clientId, String clientLastName, String clientFirstName, String clientPhoneNumber,
                                 int employeeId, String employeeLastName, String employeeFirstName, int serviseId,
                                 String serviceName, double servicePrice, String appointmentDate, String appointmentTime) {
            this.appointmentId = appointmentId; // Новый параметр
            this.clientId = clientId;
            this.clientLastName = clientLastName;
            this.clientFirstName = clientFirstName;
            this.clientPhoneNumber = clientPhoneNumber;
            this.employeeId = employeeId;
            this.employeeLastName = employeeLastName;
            this.employeeFirstName = employeeFirstName;
            this.serviseId = serviseId;
            this.serviceName = serviceName;
            this.servicePrice = servicePrice;
            this.appointmentDate = appointmentDate;
            this.appointmentTime = appointmentTime;
        }

        public int getAppointmentId() {
            return appointmentId;
        }

        public void setAppointmentId(int appointmentId) {
            this.appointmentId = appointmentId;
        }

    // Геттеры и сеттеры
    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }


    public int getClientId() { return clientId; }
    public int getEmployeeId() { return employeeId; }
    public int getServiseId() { return serviseId; }

    public AppointmentRecord() {}
    // Геттеры и сеттеры
    public String getClientLastName() {
        return clientLastName;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getServicePrice() {
        return servicePrice;
    }
    public void setServicePrice(double servicePrice) {}
    @Override
    public String toString() {
        return "Запись на услугу: " +
                "Клиент: " + clientFirstName + " " + clientLastName + " (" + clientPhoneNumber + "), " +
                "Мастер: " + employeeFirstName + " " + employeeLastName + ", " +
                "Услуга: " + serviceName + ", " +
                "Стоимость: " + servicePrice + " руб., " +
                "Дата записи: " + appointmentDate + ", " +
                "Время записи: " + appointmentTime;
    }

}
