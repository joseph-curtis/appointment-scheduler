package scheduler.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Database  {
    // connection string parameters
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String protocol = "jdbc:";
    private static final String subprotocol = "mysql://";
    private static final String hostName = "localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + subprotocol + hostName + databaseName
            + "?connectionTimeZone = SERVER";
    private static final String userName = "sqlUser";
    private static final String password = "Passw0rd!";

    // The one and only connection object
    public static Connection connection = null;

    public static void openConnection() {
        if (connection != null) return;

        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            System.out.println("Connection successful!");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Database Driver not found!\n" + e.getMessage());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        if (connection == null) return;

        try {
            connection.close();
            connection = null;
            System.out.println("Connection closed!");
        }
        catch(SQLException e){
            System.out.println("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
