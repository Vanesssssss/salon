package Database;

import POJO.CreateScheduleRequest;

import java.sql.SQLException;

public interface IntScheduleDB {
    boolean addSchedule(CreateScheduleRequest request) throws SQLException;
}
