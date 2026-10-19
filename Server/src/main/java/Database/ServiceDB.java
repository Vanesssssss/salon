package Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import POJO.Services;
public class ServiceDB implements IntServiceDB{
    private static ServiceDB instance;
    private DatabaseConnection dbConnection;

    private ServiceDB() throws SQLException, ClassNotFoundException {
        dbConnection = DatabaseConnection.getInstance();
    }

    public static ServiceDB getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new ServiceDB();
        }
        return instance;
    }

    @Override
    public boolean addService(Services service) throws SQLException {
        String sql = "INSERT INTO services (name, description, price, duration) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(sql)) {
            stmt.setString(1, service.getName());
            stmt.setString(2, service.getDescription());
            stmt.setDouble(3, service.getPrice());
            stmt.setString(4, service.getDuration());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Services> getAllServices() throws SQLException {
        String sql = "SELECT * FROM services";
        List<Services> servicesList = new ArrayList<>();
        try (Statement stmt = DatabaseConnection.dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Services service = new Services(
                        rs.getInt("servise_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("duration")
                );
                servicesList.add(service);
            }
        }
        return servicesList;
    }
    @Override
    public boolean updateService(Services service) throws SQLException {
        String sql = "UPDATE services SET name = ?, description = ?, price = ?, duration = ? WHERE servise_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(sql)) {
            stmt.setString(1, service.getName());
            stmt.setString(2, service.getDescription());
            stmt.setDouble(3, service.getPrice());
            stmt.setString(4, service.getDuration());
            stmt.setInt(5, service.getServiseId());
            return stmt.executeUpdate() > 0;
        }
    }
    @Override
    public Services getServiceById(int serviceId) throws SQLException {
        String sql = "SELECT * FROM services WHERE servise_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Services(
                            rs.getInt("servise_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getString("duration")
                    );
                }
            }
        }
        return null;
    }
    @Override
    public boolean deleteService(int serviceId) throws SQLException {
        String sql = "DELETE FROM services WHERE servise_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            return stmt.executeUpdate() > 0;
        }
    }
    @Override
    public List<Services> getServicesByEmployeeId(int employeeId) throws SQLException {
        List<Services> servicesList = new ArrayList<>();
        String query = "SELECT * FROM services WHERE servise_id IN " +
                "(SELECT servise_id FROM employee_services WHERE employee_id = ?)";
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Services service = new Services(
                        rs.getInt("servise_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("duration")
                );
                servicesList.add(service);
            }
        }
        return servicesList;
    }

}
