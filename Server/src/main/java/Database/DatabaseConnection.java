package Database;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {
    private final String DB_URL = System.getenv().getOrDefault("SALON_DB_URL", "jdbc:mysql://localhost:3306/hairsalon");
    private final String DB_USER = requiredEnv("SALON_DB_USER");
    private final String DB_PASSWORD = requiredEnv("SALON_DB_PASSWORD");
    private static DatabaseConnection instance;
    public static Connection dbConnection;
    private Statement statement;
    private ResultSet resultSet;
    private ArrayList<String[]> Result;

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be set");
        }
        return value;
    }

    public DatabaseConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        statement = dbConnection.createStatement();
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public ArrayList<String[]> getArrayResult(String str) {
        Result = new ArrayList<String[]>();
        try {
            resultSet = statement.executeQuery(str);
            int count = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                String[] arrayString = new String[count];
                for (int i = 1;  i <= count; i++)
                    arrayString[i - 1] = resultSet.getString(i);

                Result.add(arrayString);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return Result;
    }

}
