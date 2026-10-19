package Database;

import POJO.*;

import java.sql.SQLException;
import java.util.List;

public interface IntAppointmentDB {
    boolean createAppointment(AppointmentRequest appointmentRequest) throws SQLException;
    List<String> getAvailableTimes(int employeeId, String date) throws SQLException;
    List<Integer> getAllAppointmentIds() throws SQLException;
    AppointmentRecord getAppointmentRecordById(int appointmentId) throws SQLException;
    Clients getClientById(int clientId);
    Employee getEmployeeById(int employeeId) throws SQLException;
    Services getServiceById(int serviceId) throws SQLException;
    Clients getClientByUserId(int userId) throws SQLException, ClassNotFoundException;
    List<Integer> getAppointmentIdsByClientId(int clientId) throws SQLException;
    boolean deleteAppointmentById(int appointmentId) throws SQLException;
    List<Integer> getAppointmentIdsByEmployeeId(int clientId) throws SQLException;
    Employee getEmployeeByUserId(int userId) throws SQLException, ClassNotFoundException;
}
