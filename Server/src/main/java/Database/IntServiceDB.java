package Database;


import POJO.Services;
import java.sql.SQLException;
import java.util.List;

public interface IntServiceDB {
    boolean addService(Services service) throws SQLException; // Добавить услугу
    List<Services> getAllServices() throws SQLException; // Получить все услуги
    Services getServiceById(int serviceId) throws SQLException;
    boolean deleteService(int serviceId) throws SQLException;
    boolean updateService(Services service) throws SQLException;
    List<Services> getServicesByEmployeeId(int employeeId) throws SQLException;// Получить услугу по ID
}
