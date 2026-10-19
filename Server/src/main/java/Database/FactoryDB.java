package Database;

import java.sql.SQLException;

public class FactoryDB extends Factory {
    public UsersDB getUsers() throws SQLException, ClassNotFoundException {
        return UsersDB.getInstance();
    }

    public AuthDB getRole() throws SQLException, ClassNotFoundException {
        return AuthDB.getInstance();
    }
    public IntServiceDB getServiceDB() throws SQLException, ClassNotFoundException {
        return ServiceDB.getInstance();
    }
    public  IntClientsDB getClientsDB() throws SQLException, ClassNotFoundException{
        return ClientsDB.getInstance();
    }
    public IntReviewDB getReviewDB() throws SQLException, ClassNotFoundException{
        return ReviewDB.getInstance();
    }
    public IntEmployeeDB getEmployeeDB() throws SQLException, ClassNotFoundException{
        return EmployeeDB.getInstance();
    }
    public IntScheduleDB getScheduleDB() throws SQLException, ClassNotFoundException{
        return ScheduleDB.getInstance();
    }
    public  IntAppointmentDB getAppointmentDB() throws SQLException, ClassNotFoundException{
        return AppointmentDB.getInstance();
    }

}
