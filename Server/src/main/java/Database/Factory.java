package Database;

import java.sql.SQLException;

public abstract class Factory {
    public abstract IntAuthDB getRole() throws SQLException, ClassNotFoundException;
    public abstract UsersDB getUsers() throws SQLException, ClassNotFoundException;
    public abstract IntServiceDB getServiceDB() throws SQLException, ClassNotFoundException;
    public abstract IntClientsDB getClientsDB() throws SQLException, ClassNotFoundException;
    public abstract IntReviewDB getReviewDB() throws SQLException, ClassNotFoundException;
    public abstract IntEmployeeDB getEmployeeDB() throws SQLException, ClassNotFoundException;
    public abstract IntScheduleDB getScheduleDB() throws SQLException, ClassNotFoundException;
    public abstract IntAppointmentDB getAppointmentDB() throws SQLException, ClassNotFoundException;
}
