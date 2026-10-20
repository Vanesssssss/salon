package Database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppointmentCancellationTest {
    private Connection connection;
    private AppointmentDB appointments;

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:cancellation;MODE=MySQL");
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE services (servise_id INT PRIMARY KEY, duration VARCHAR(100))");
            statement.execute("CREATE TABLE schedule_slots (employee_id INT, slot_date DATE, slot_time TIME, is_available BOOLEAN)");
            statement.execute("CREATE TABLE appointments (appointment_id INT PRIMARY KEY, employee_id INT, " +
                    "servise_id INT, slot_date DATE, slot_time TIME)");
            statement.execute("INSERT INTO services VALUES (3, '2 часы')");
            statement.execute("INSERT INTO appointments VALUES (1, 2, 3, DATE '2026-10-20', TIME '10:00:00')");
            statement.execute("INSERT INTO schedule_slots VALUES (2, DATE '2026-10-20', TIME '10:00:00', FALSE)");
            statement.execute("INSERT INTO schedule_slots VALUES (2, DATE '2026-10-20', TIME '11:00:00', FALSE)");
        }
        appointments = new AppointmentDB(connection);
    }

    @AfterEach
    void tearDown() throws Exception {
        connection.close();
    }

    @Test
    void deletesAppointmentAndReleasesItsSlots() throws Exception {
        assertTrue(appointments.deleteAppointmentById(1));
        assertEquals(0, appointmentCount());
        assertEquals(2, availableSlotCount());
        assertTrue(connection.getAutoCommit());
    }

    @Test
    void leavesDataUntouchedForUnknownAppointment() throws Exception {
        assertFalse(appointments.deleteAppointmentById(99));
        assertEquals(1, appointmentCount());
        assertEquals(0, availableSlotCount());
    }

    @Test
    void rollsBackDeletionWhenSlotsCannotBeReleased() throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE schedule_slots ADD CONSTRAINT blocked_only CHECK (is_available = FALSE)");
        }

        assertThrows(SQLException.class, () -> appointments.deleteAppointmentById(1));
        assertEquals(1, appointmentCount());
        assertEquals(0, availableSlotCount());
        assertTrue(connection.getAutoCommit());
    }

    @Test
    void retainsBookingWhenItsSlotsAreMissing() throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM schedule_slots");
        }

        assertFalse(appointments.deleteAppointmentById(1));
        assertEquals(1, appointmentCount());
    }

    private int appointmentCount() throws Exception {
        return count("SELECT COUNT(*) FROM appointments");
    }

    private int availableSlotCount() throws Exception {
        return count("SELECT COUNT(*) FROM schedule_slots WHERE is_available = TRUE");
    }

    private int count(String query) throws Exception {
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(query)) {
            result.next();
            return result.getInt(1);
        }
    }
}
