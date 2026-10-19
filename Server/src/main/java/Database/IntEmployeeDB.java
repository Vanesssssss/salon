package Database;

import POJO.AddMasterRequest;
import POJO.Employee;
import POJO.Services;

import java.sql.SQLException;
import java.util.List;

public interface IntEmployeeDB {
    boolean isMasterExists(String login) throws SQLException;
    int addUser(String login, String password) throws SQLException;
    boolean addMaster(int userId, AddMasterRequest request) throws SQLException;
    List<Employee> getAllEmployees() throws SQLException;
    boolean assignServicesToMaster(int employeeId, List<Integer> serviceIds) throws SQLException;
    Employee getEmployeeByUserId(int userId) throws SQLException, ClassNotFoundException;
    boolean updateEmployeeInfo(int userId, String lastName, String firstName, int middleName, String phoneNumber) throws SQLException;

}
