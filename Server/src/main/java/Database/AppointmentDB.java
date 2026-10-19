package Database;

import POJO.*;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDB implements IntAppointmentDB {
    private static AppointmentDB instance;
    private DatabaseConnection dbConnection;

    private AppointmentDB() throws SQLException, ClassNotFoundException {
        dbConnection = DatabaseConnection.getInstance();
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
        int serviceId = appointmentRequest.getServiceId();  // ID услуги

        // Получаем продолжительность услуги из таблицы services
        int serviceDuration = getServiceDuration(serviceId);

        if (serviceDuration == -1) {
            System.out.println("Продолжительность услуги не найдена для serviceId: " + serviceId);
            return false; // Ошибка, если не найдена продолжительность услуги
        }

        // Проверка, не выходит ли время записи за пределы рабочего времени
        if (!isWithinWorkingHours(slotDate, slotTime, serviceDuration)) {
            System.out.println("Время записи выходит за пределы рабочего времени: " + slotDate + " " + slotTime);
            return false; // если не в рабочем времени
        }

        // Проверка, занят ли выбранный слот
        if (isSlotOccupied(slotDate, slotTime, serviceDuration, appointmentRequest.getEmployeeId())) {
            System.out.println("Слот занят: " + slotDate + " " + slotTime);
            return false; // если слот занят
        }

        // Если слот свободен и время в пределах рабочего времени, добавляем запись в базу
        String query = "INSERT INTO appointments (client_id, employee_id, servise_id, slot_date, slot_time) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, appointmentRequest.getClientId());
            stmt.setInt(2, appointmentRequest.getEmployeeId());
            stmt.setInt(3, serviceId);
            stmt.setString(4, appointmentRequest.getSlotDate());
            stmt.setString(5, slotTime);  // Используем только время

            int rowsAffected = stmt.executeUpdate();

            // Если запись успешно добавлена, блокируем слот на соответствующее время
            if (rowsAffected > 0) {
                blockSlot(slotDate, slotTime, serviceDuration, appointmentRequest.getEmployeeId()); // блокируем слот
                System.out.println("Запись успешно добавлена для клиента " + appointmentRequest.getClientId() + " на слот: " + slotDate + " " + slotTime);
                return true;
            } else {
                System.out.println("Не удалось добавить запись для клиента " + appointmentRequest.getClientId() + " на слот: " + slotDate + " " + slotTime);
            }
        }
        return false;
    }

    public boolean isWithinWorkingHours(String slotDate, String slotTime, int serviceDuration) {
        try {
            LocalTime appointmentTime = LocalTime.parse(slotTime);
            LocalTime endTime = appointmentTime.plusMinutes(serviceDuration);

            LocalTime startOfWork = LocalTime.of(9, 0);  // начало рабочего дня
            LocalTime endOfWork = LocalTime.of(18, 0);  // конец рабочего дня

            // Проверяем, чтобы время записи и окончания попало в рабочие часы
            return !appointmentTime.isBefore(startOfWork) && !endTime.isAfter(endOfWork);
        } catch (Exception e) {
            System.out.println("Ошибка при проверке рабочего времени: " + e.getMessage());
            return false;
        }
    }

    // Получение продолжительности услуги
    public int getServiceDuration(int serviceId) throws SQLException {
        String query = "SELECT duration FROM services WHERE servise_id = ?";

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
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
        String query = "DELETE FROM appointments WHERE appointment_id = ?";

        // Получение информации о записи перед удалением
        AppointmentRecord record = getAppointmentRecordById(appointmentId);
        if (record == null) {
            System.out.println("Запись с ID " + appointmentId + " не найдена.");
            return false;
        }

        // Разблокировка слотов, связанных с записью
        unblockSlot(record.getAppointmentDate(), record.getAppointmentTime(), record.getServiseId(), record.getEmployeeId());

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Метод для разблокировки занятых слотов
    private void unblockSlot(String slotDate, String slotTime, int serviceId, int employeeId) throws SQLException {
        int serviceDuration = getServiceDuration(serviceId);
        if (serviceDuration == -1) {
            System.out.println("Ошибка: Не удалось получить продолжительность услуги для разблокировки слотов.");
            return;
        }

        String query = "UPDATE schedule_slots SET is_available = 1 " +
                "WHERE employee_id = ? AND slot_date = ? AND slot_time >= ? AND slot_time < ?";

        LocalTime appointmentTime = LocalTime.parse(slotTime);
        LocalTime endTime = appointmentTime.plusMinutes(serviceDuration);

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            stmt.setString(2, slotDate);
            stmt.setString(3, appointmentTime.toString());
            stmt.setString(4, endTime.toString());
            stmt.executeUpdate();
        }
    }
}