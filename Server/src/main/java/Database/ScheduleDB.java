package Database;

import POJO.CreateScheduleRequest;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleDB implements IntScheduleDB {
    private static ScheduleDB instance;
    private DatabaseConnection dbConnection;

    private ScheduleDB() throws SQLException, ClassNotFoundException {
        dbConnection = DatabaseConnection.getInstance();
    }

    public static ScheduleDB getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new ScheduleDB();
        }
        return instance;
    }

    @Override
    public boolean addSchedule(CreateScheduleRequest request) throws SQLException {
        String query = "INSERT INTO schedule_slots (employee_id, slot_date, slot_time, is_available) VALUES (?, ?, ?, TRUE)";

        try (PreparedStatement stmt = DatabaseConnection.dbConnection.prepareStatement(query)) {
            // Преобразуем startDate и endDate в LocalDate
            LocalDate currentDate = request.getStartDate(); // Начальная дата
            LocalDate endDate = request.getEndDate();       // Конечная дата

            // Перебираем все дни с startDate по endDate
            while (!currentDate.isAfter(endDate)) {
                // Для каждого дня создаем слоты времени
                LocalTime currentTime = request.getStartTime(); // Начальное время

                // Пока время меньше или равно конечному времени
                while (!currentTime.isAfter(request.getEndTime())) {
                    // Устанавливаем параметры запроса
                    stmt.setInt(1, request.getEmployeeId());
                    stmt.setDate(2, Date.valueOf(currentDate));  // Дата
                    stmt.setTime(3, Time.valueOf(currentTime));  // Время

                    // Выполняем запрос
                    stmt.addBatch();  // Добавляем запрос в пакет

                    // Увеличиваем время на 1 час
                    currentTime = currentTime.plusHours(1);
                }
                // Переходим к следующему дню
                currentDate = currentDate.plusDays(1);
            }

            // Выполняем все добавленные запросы
            int[] rowsAffected = stmt.executeBatch();

            // Если вставлено хотя бы одно расписание, то считаем успешным
            return rowsAffected.length > 0;
        }
    }

}
