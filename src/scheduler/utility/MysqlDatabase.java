package scheduler.utility;

import java.sql.*;

/**
 * Generic Database Object (abstract) for executing
 * SQL using Strings as input.
 * <p>Connects to the MySql database using JDBC driver and DriverManager for creating connections.</p>
 * <p>ResultSet is stored as static variable, retrieve by calling getResultSet</p>
 * @author Joseph Curtis
 * @version 2022.03.05
 */
public abstract class MysqlDatabase {
    // connection string parameters
    private static final String MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String PROTOCOL = "jdbc:";
    private static final String SUBPROTOCOL = "mysql://";
    private static final String HOST_NAME = "localhost/";
    private static final String DATABASE_NAME = "client_schedule";
    private static final String JDBC_URL = PROTOCOL + SUBPROTOCOL + HOST_NAME + DATABASE_NAME
            + "?connectionTimeZone = SERVER";
    private static final String USERNAME = "sqlUser";
    private static final String PASSWORD = "Passw0rd!";

    private static Connection connection = null;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void openConnection() {
        try {
            Class.forName(MYSQL_JDBC_DRIVER); // Locate Driver
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connection successful!");
        }
        catch (ClassNotFoundException e) {
            System.out.println("MySql JDBC Driver not found!\n" + e.getMessage());
            e.printStackTrace();
        }
        catch (SQLException e) {
            System.out.println("Error creating connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int executeSqlString(String query) {
        int rowCount = -1;
        try {
            if(connection == null || connection.isClosed()) openConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);

            if(query.toUpperCase().startsWith("SELECT")) {
                System.out.println("Executing SELECT statement");
                resultSet = statement.executeQuery(query);
                if (resultSet != null) {
                    resultSet.last();              // move cursor to the last row
                    rowCount = resultSet.getRow();
                    resultSet.beforeFirst();       // move cursor back to start for data processing later
                    if (rowCount == 0)
                        throw new RuntimeException("WARNING: SELECT statement returned zero records.\n" +
                                "Check if record(s) exist, or check query syntax.");
                }
            } else if(query.toUpperCase().startsWith("INSERT")
                    ||query.toUpperCase().startsWith("UPDATE")
                    ||query.toUpperCase().startsWith("DELETE")) {
                System.out.println("Executing DML statement");
                rowCount = statement.executeUpdate(query);
            } else {
                System.out.println("Executing DDL statement");
                rowCount = statement.executeUpdate(query);
            }
        }
        catch(RuntimeException e) {
            System.out.println(e.getMessage());
        }
        catch(SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return rowCount;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

}
