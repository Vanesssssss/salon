package Database;

import POJO.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDB implements IntAppointmentDB {
    private static AppointmentDB instance;
    private final Connection connection;

    private AppointmentDB() throws SQLException, ClassNotFoundException {
        DatabaseConnection.getInstance();
        connection = DatabaseConnection.dbConnection;
    }

    AppointmentDB(Connection connection) {
        this.connection = connection;
    }

    public static AppointmentDB getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new AppointmentDB();
        }
        return instance;
    }

    @Override
    public List<String> getAvailableTimes(int employeeId, String date) throws SQLException {
        List<String> availableTimes = new ArrayList<>();

        // Запрос для получения доступных слотов
        String query = "SELECT slot_date, slot_time FROM schedule_slots " +
                "WHERE employee_id = ? AND slot_date = ? AND is_available = 1";

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);  // ID мастера
            stmt.setString(2, date);      // Дата

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String slotDate = rs.getString("slot_date");  // Дата
                    String slotTime = rs.getString("slot_time");  // Время
                    availableTimes.add(slotDate + " " + slotTime);
                }
            }
        }
        return availableTimes;
    }

    @Override
    public boolean createAppointment(AppointmentRequest appointmentRequest) throws SQLException {
        String slotDate = appointmentRequest.getSlotDate();
        String slotTime = appointmentRequest.getSlotTime();
        // The application shares one JDBC connection between client handlers.
        // Synchronize local transactions; row locks also coordinate other connections.
        synchronized (connection) {
            int serviceDuration = getServiceDuration(appointmentRequest.getServiceId());
            if (serviceDuration <= 0 || !isWithinWorkingHours(slotDate, slotTime, serviceDuration)) {
                return false;
            }
            LocalTime start = LocalTime.parse(slotTime);
            LocalTime end = start.plusMinutes(serviceDuration);

            if (!connection.getAutoCommit()) {
                throw new SQLException("Appointment connection already has an active transaction");
            }
            connection.setAutoCommit(false);
            try {
                String lockQuery = "SELECT slot_time, is_available FROM schedule_slots " +
                        "WHERE employee_id = ? AND slot_date = ? AND slot_time >= ? AND slot_time < ? " +
                        "ORDER BY slot_time FOR UPDATE";
                int slotCount = 0;
                boolean startExists = false;
                boolean allAvailable = true;
                boolean continuous = true;
                LocalTime expectedSlot = start;
                try (PreparedStatement stmt = connection.prepareStatement(lockQuery)) {
                    stmt.setInt(1, appointmentRequest.getEmployeeId());
                    stmt.setString(2, slotDate);
                    stmt.setString(3, start.toString());
                    stmt.setString(4, end.toString());
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            slotCount++;
                            LocalTime actualSlot = rs.getTime("slot_time").toLocalTime();
                            startExists |= actualSlot.equals(start);
                            continuous &= actualSlot.equals(expectedSlot);
                            expectedSlot = expectedSlot.plusHours(1);
                            allAvailable &= rs.getBoolean("is_available");
                        }
                    }
                }
                if (!startExists || !allAvailable || !continuous || expectedSlot.isBefore(end)) {
                    connection.rollback();
                    return false;
                }

                String reserveQuery = "UPDATE schedule_slots SET is_available = 0 " +
                        "WHERE employee_id = ? AND slot_date = ? AND slot_time >= ? AND slot_time < ? " +
                        "AND is_available = 1";
                try (PreparedStatement stmt = connection.prepareStatement(reserveQuery)) {
                    stmt.setInt(1, appointmentRequest.getEmployeeId());
                    stmt.setString(2, slotDate);
                    stmt.setString(3, start.toString());
                    stmt.setString(4, end.toString());
                    if (stmt.executeUpdate() != slotCount) {
                        connection.rollback();
                        return false;
                    }
                }

                String insertQuery = "INSERT INTO appointments " +
                        "(client_id, employee_id, servise_id, slot_date, slot_time) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                    stmt.setInt(1, appointmentRequest.getClientId());
                    stmt.setInt(2, appointmentRequest.getEmployeeId());
                    stmt.setInt(3, appointmentRequest.getServiceId());
                    stmt.setString(4, slotDate);
                    stmt.setString(5, slotTime);
                    if (stmt.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }
                connection.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean isWithinWorkingHours(String slotDate, String slotTime, int serviceDuration) {
        try {
            LocalTime appointmentTime = LocalTime.parse(slotTime);
            LocalTime startOfWork = LocalTime.of(9, 0);  // начало рабочего дня
            LocalTime endOfWork = LocalTime.of(18, 0);  // конец рабочего дня

            // Проверяем, чтобы время записи и окончания попало в рабочие часы
            long endSecond = appointmentTime.toSecondOfDay() + serviceDuration * 60L;
            return serviceDuration > 0 && !appointmentTime.isBefore(startOfWork)
                    && endSecond <= endOfWork.toSecondOfDay();
        } catch (Exception e) {
            System.out.println("Ошибка при проверке рабочего времени: " + e.getMessage());
            return false;
        }
    }

    // Получение продолжительности услуги
    public int getServiceDuration(int serviceId) throws SQLException {
        String query = "SELECT duration FROM services WHERE servise_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, serviceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String durationString = rs.getString("duration").toLowerCase(); // Приводим строку к нижнему регистру

                int totalMinutes = 0;
                // Проверяем наличие "часы" и "минуты"
                if (durationString.contains("часы")) {
                    String[] parts = durationString.split("часы");
                    int hours = Integer.parseInt(parts[0].trim()); // Извлекаем количество часов
                    totalMinutes += hours * 60; // Преобразуем в минуты
                }

                if (durationString.contains("минуты")) {
                    String[] parts = durationString.split("минуты");
                    int minutes = Integer.parseInt(parts[0].trim()); // Извлекаем количество минут
                    totalMinutes += minutes; // Добавляем к общим минутам
                }

                return totalMinutes; // Возвращаем продолжительность в минутах
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении продолжительности услуги: " + e.getMessage());
        }

        return -1; // Если услуга не найдена или произошла ошибка
    }

    // Проверка, занят ли слот
    public boolean isSlotOccupied(String slotDate, String slotTime, int serviceDuration, int employeeId) throws SQLException {
        String query = "SELECT COUNT(*) FROM schedule_slots " +
                "WHERE employee_id = ? " +
                "AND slot_date = ? " +
                "AND slot_time >= ? " +
                "AND slot_time < ? " +
                "AND is_available = 0";  // Проверка на доступность

        LocalTime appointmentTime = LocalTime.parse(slotTime);  // Время начала
        LocalTime endTime = appointmentTime.plusMinutes(serviceDuration);  // Время окончания
        System.out.println("Проверка слота: " + slotDate + " " + slotTime + " до " + endTime.toString()); // Логируем начало и конец времени

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            stmt.setString(2, slotDate);
            stmt.setString(3, appointmentTime.toString());  // Вставляем время начала
            stmt.setString(4, endTime.toString());  // Вставляем время окончания
            System.out.println("SQL запрос: " + stmt.toString());  // Логируем сам SQL-запрос

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Количество найденных занятых слотов: " + count);
                return count > 0;   // Если найден хотя бы один занятой слот
            }
        }
        return false;  // Если слот свободен
    }

    // Блокировка слота
    public void blockSlot(String slotDate, String slotTime, int serviceDuration, int employeeId) throws SQLException {
        String query = "UPDATE schedule_slots SET is_available = 0 " +
                "WHERE employee_id = ? AND slot_date = ? AND slot_time >= ? AND slot_time < ?";

        LocalTime appointmentTime = LocalTime.parse(slotTime);  // Время начала
        LocalTime endTime = appointmentTime.plusMinutes(serviceDuration);  // Время окончания

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            stmt.setString(2, slotDate);
            stmt.setString(3, appointmentTime.toString());  // Вставляем время начала
            stmt.setString(4, endTime.toString());  // Вставляем время окончания

            stmt.executeUpdate();
            System.out.println("Слот заблокирован: " + slotDate + " " + slotTime);
        }
    }
    @Override
    public List<Integer> getAllAppointmentIds() throws SQLException {
        List<Integer> appointmentIds = new ArrayList<>();
        String query = "SELECT appointment_id FROM appointments";

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                appointmentIds.add(resultSet.getInt("appointment_id"));
            }
        }
        return appointmentIds;
    }

    @Override
    public AppointmentRecord getAppointmentRecordById(int appointmentId) throws SQLException {
        String query = "SELECT a.client_id, a.employee_id, a.servise_id, c.last_name AS client_last_name, \n" +
                "       c.first_name AS client_first_name, c.phone_number, e.last_name AS employee_last_name, \n" +
                "       e.first_name AS employee_first_name, s.name AS service_name, s.price, \n" +
                "       a.slot_date, a.slot_time \n" +
                "FROM appointments a \n" +
                "JOIN clients c ON a.client_id = c.client_id \n" +
                "JOIN employees e ON a.employee_id = e.employee_id \n" +
                "JOIN services s ON a.servise_id = s.servise_id \n" +
                "WHERE a.appointment_id = ?";

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    // Извлекаем данные из ResultSet
                    int clientId = resultSet.getInt("client_id");  // Извлекаем ID клиента
                    String clientLastName = resultSet.getString("client_last_name");
                    String clientFirstName = resultSet.getString("client_first_name");
                    String clientPhoneNumber = resultSet.getString("phone_number");

                    int employeeId = resultSet.getInt("employee_id");  // Извлекаем ID сотрудника
                    String employeeLastName = resultSet.getString("employee_last_name");
                    String employeeFirstName = resultSet.getString("employee_first_name");

                    int serviseId = resultSet.getInt("servise_id");  // Извлекаем ID услуги
                    String serviceName = resultSet.getString("service_name");
                    double servicePrice = resultSet.getDouble("price");

                    String appointmentDate = resultSet.getString("slot_date");
                    String appointmentTime = resultSet.getString("slot_time");
                    // Возвращаем новый объект AppointmentRecord с добавлением идентификаторов
                    return new AppointmentRecord(appointmentId, clientId, clientLastName, clientFirstName, clientPhoneNumber,
                            employeeId, employeeLastName, employeeFirstName, serviseId, serviceName, servicePrice, appointmentDate, appointmentTime);
                } else {
                    return null;  // Если записи нет, возвращаем null
                }
            }
        }
    }

    @Override
    public Clients getClientById(int clientId) {
        // Ваш код для извлечения данных клиента из базы данных
        String query = "SELECT * FROM clients WHERE client_id = ?";
        try (
                PreparedStatement statement = DatabaseConnection.dbConnection.prepareStatement(query)) {
            statement.setInt(1, clientId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("users_id");
                String lastName = resultSet.getString("last_name");
                String firstName = resultSet.getString("first_name");
                String middleName = resultSet.getString("middle_name");
                String phoneNumber = resultSet.getString("phone_number");

                return new Clients(clientId, userId, lastName, firstName, middleName, phoneNumber);
            } else {
                return null; // Клиент не найден
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public Clients getClientByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM clients WHERE users_id = ?";
        try (PreparedStatement statement = DatabaseConnection.dbConnection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Clients client = new Clients();
                client.setClientId(resultSet.getInt("client_id"));
                client.setUserId(resultSet.getInt("users_id"));
                client.setLastName(resultSet.getString("last_name"));
                client.setFirstName(resultSet.getString("first_name"));
                client.setMiddleName(resultSet.getString("middle_name"));
                client.setPhoneNumber(resultSet.getString("phone_number"));
                return client;
            } else {
                return null;
            }
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
                employee.setEmployeeId(resultSet.getInt("employee_id"));
                employee.setUserId(resultSet.getInt("user_id"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setFirstName(resultSet.getString("first_name"));
                return employee;
            } else {
                return null;
            }
        }
    }

    @Override
    public Employee getEmployeeById(int employeeId) throws SQLException {
        String query = "SELECT * FROM employees WHERE employee_id = ?";

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String lastName = resultSet.getString("last_name");
                    String firstName = resultSet.getString("first_name");
                    int experienceYears = resultSet.getInt("experience_years");
                    String specialization = resultSet.getString("specialization");
                    LocalTime workStartTime = resultSet.getTime("work_start_time").toLocalTime();
                    LocalTime workEndTime = resultSet.getTime("work_end_time").toLocalTime();

                    // Обновленный конструктор должен принимать все поля
                    return new Employee(
                            employeeId, userId, lastName, firstName,
                            experienceYears, specialization, workStartTime, workEndTime
                    );
                } else {
                    return null; // Если мастера нет, возвращаем null
                }
            }
        }
    }
    @Override
    public Services getServiceById(int serviceId) throws SQLException {
        String query = "SELECT * FROM services WHERE servise_id = ?";

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, serviceId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    double price = resultSet.getDouble("price");
                    String duration = resultSet.getString("duration");

                    // Обновленный конструктор должен принимать все поля
                    return new Services(serviceId, name, description, price, duration);
                } else {
                    return null; // Если услуги нет, возвращаем null
                }
            }
        }
    }
    @Override
    public List<Integer> getAppointmentIdsByClientId(int clientId) throws SQLException {
        String query = "SELECT appointment_id FROM appointments WHERE client_id = ?";

        List<Integer> appointmentIds = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    appointmentIds.add(resultSet.getInt("appointment_id"));
                }
            }
        }
        return appointmentIds;
    }
    @Override
    public List<Integer> getAppointmentIdsByEmployeeId(int clientId) throws SQLException {
        String query = "SELECT appointment_id FROM appointments WHERE employee_id = ?";

        List<Integer> appointmentIds = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    appointmentIds.add(resultSet.getInt("appointment_id"));
                }
            }
        }
        return appointmentIds;
    }

    public boolean deleteAppointmentById(int appointmentId) throws SQLException {
        synchronized (connection) {
            if (!connection.getAutoCommit()) {
                throw new SQLException("Appointment connection already has an active transaction");
            }
            connection.setAutoCommit(false);
            try {
                String lookupQuery = "SELECT employee_id, servise_id, slot_date, slot_time " +
                        "FROM appointments WHERE appointment_id = ? FOR UPDATE";
                int employeeId;
                int serviceId;
                String slotDate;
                LocalTime start;
                try (PreparedStatement stmt = connection.prepareStatement(lookupQuery)) {
                    stmt.setInt(1, appointmentId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            connection.rollback();
                            return false;
                        }
                        employeeId = rs.getInt("employee_id");
                        serviceId = rs.getInt("servise_id");
                        slotDate = rs.getDate("slot_date").toString();
                        start = rs.getTime("slot_time").toLocalTime();
                    }
                }

                int duration = getServiceDuration(serviceId);
                if (duration <= 0) {
                    throw new SQLException("Duration not found for service " + serviceId);
                }
                LocalTime end = start.plusMinutes(duration);

                try (PreparedStatement stmt = connection.prepareStatement(
                        "DELETE FROM appointments WHERE appointment_id = ?")) {
                    stmt.setInt(1, appointmentId);
                    if (stmt.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                String releaseQuery = "UPDATE schedule_slots SET is_available = 1 " +
                        "WHERE employee_id = ? AND slot_date = ? AND slot_time >= ? AND slot_time < ? " +
                        "AND is_available = 0";
                try (PreparedStatement stmt = connection.prepareStatement(releaseQuery)) {
                    stmt.setInt(1, employeeId);
                    stmt.setString(2, slotDate);
                    stmt.setString(3, start.toString());
                    stmt.setString(4, end.toString());
                    if (stmt.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }
                connection.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
