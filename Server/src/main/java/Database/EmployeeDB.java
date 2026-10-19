package Database;

import POJO.AddMasterRequest;
import POJO.Clients;
import POJO.Employee;
import POJO.Services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDB implements IntEmployeeDB {
    private static EmployeeDB instance;
    private DatabaseConnection dbConnection;

    private EmployeeDB() throws SQLException, ClassNotFoundException {
        dbConnection = DatabaseConnection.getInstance();
    }

    public static EmployeeDB getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new EmployeeDB();
        }
        return instance;
    }

    @Override
    public boolean isMasterExists(String login) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE login = ?";
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Если пользователь с таким логином существует
            }
            return false;
        }
    }

    @Override
    public int addUser(String login, String password) throws SQLException {
        String query = "INSERT INTO users (login, password, role) VALUES (?, ?, 'employee')";
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, login);
            stmt.setString(2, password);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Получаем сгенерированный user_id
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);  // Возвращаем user_id
                }
            }
            return -1;  // Если не удалось добавить пользователя
        }
    }

    @Override
    public boolean addMaster(int userId, AddMasterRequest request) throws SQLException {
        String query = "INSERT INTO employees (user_id, first_name, last_name, experience_years, specialization, work_start_time, work_end_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, request.getFirstName());
            stmt.setString(3, request.getLastName());
            stmt.setInt(4, Integer.parseInt(request.getExperience()));  // Преобразуем опыт в число
            stmt.setString(5, request.getSpecialization());
            stmt.setString(6, request.getStartWork());
            stmt.setString(7, request.getEndWork());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // Если данные были успешно добавлены
        }
    }

    @Override
    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "SELECT * FROM employees";
        List<Employee> employeesList = new ArrayList<>();
        try (Statement stmt = DatabaseConnection.dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("experience_years"),
                        rs.getString("specialization"),
                        rs.getTime("work_start_time").toLocalTime(),
                        rs.getTime("work_end_time").toLocalTime()
                );
                employeesList.add(employee);
            }
        }
        return employeesList;
    }
    @Override
    public boolean assignServicesToMaster(int employeeId, List<Integer> serviceIds) throws SQLException {
        String query = "INSERT INTO employee_services (employee_id, servise_id) VALUES (?, ?)";

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            for (int serviceId : serviceIds) {
                stmt.setInt(1, employeeId);
                stmt.setInt(2, serviceId);
                stmt.addBatch(); // Добавляем в пакет
            }
            int[] results = stmt.executeBatch(); // Выполняем пакетное добавление
            return results.length == serviceIds.size(); // Проверяем, что все записи успешно добавлены
        }
    }
    @Override
    public Employee getEmployeeByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM employees WHERE user_id = ?";
        try (PreparedStatement statement = DatabaseConnection.dbConnection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Employee employee = new Employee();
                employee.setUserId(resultSet.getInt("user_id"));
                employee.setFirstName(resultSet.getString("first_name"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setExperienceYears(resultSet.getInt("experience_years"));
                employee.setSpecialization(resultSet.getString("specialization"));
                return employee;
            } else {
                return null;
            }
        }
    }

    @Override
    // Метод для обновления других данных клиента
    public boolean updateEmployeeInfo(int userId, String lastName, String firstName, int middleName, String phoneNumber) throws SQLException {
        String query = "UPDATE employees SET last_name = ?, first_name = ?, experience_years = ?, specialization = ? WHERE user_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            stmt.setInt(3, middleName);
            stmt.setString(4, phoneNumber);
            stmt.setInt(5, userId);
            return stmt.executeUpdate() > 0;  // Если обновлено, возвращаем true
        }
    }
}
