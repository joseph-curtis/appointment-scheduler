/*
 Copyright 2022 Joseph Curtis Licensed under the Educational
 Community License, Version 2.0 (the "License"); you may not use this file
 except in compliance with the License. You may obtain a copy of the License at

 http://opensource.org/licenses/ECL-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 License for the specific language governing permissions and limitations under
 the License.

 ******************************************************************************/

package utility;

import com.mysql.cj.jdbc.MysqlDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.FirstLevelDivision;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Database Object (abstract) for executing SQL using Strings as input.
 * <p>Connects to the MySql database using JDBC driver and DriverManager for creating connections.</p>
 * <p>This is the generalized datasource object.</p>
 * <p>ResultSet is stored as static variable, retrieve by calling getResultSet</p>
 * @author Joseph Curtis
 * @version 2022.05.24
 */
public abstract class DBUtil {
    // connection string parameters
    private static final String MYSQL_JDBC_DRIVER = "db.driver.class.name";
    private static final String DB_URL = "db.url";
    private static final String USERNAME = "db.username";
    private static final String PASSWORD = "db.password";

    private static MysqlDataSource dataSource;
    private static Connection connection = null;
    private static Statement statement;
    private static ResultSet resultSet;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/DbConnection.properties"));

            dataSource = new MysqlDataSource();
            dataSource.setUrl(properties.getProperty(DB_URL));
            dataSource.setUser(properties.getProperty(USERNAME));
            dataSource.setPassword(properties.getProperty(PASSWORD));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get DataSource object (MysqlDataSource)
     * @return JDBC DataSource object used to get connections to database
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Open DB connection manually using DriverManager class.
     * <p>Only use if calling directly from this static class.</p>
     */
    public static void openConnection() {
        try {
            Class.forName(MYSQL_JDBC_DRIVER); // Locate Driver
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Connection successful!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySql JDBC Driver not found!\n" + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error creating connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manual implementation to close DB connection.
     * <p>Only use if calling directly from this static class.</p>
     */
    public static void closeConnection() {
        try {
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("Connection closed!");
        } catch(SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes statement from given string.
     * <p>Only use if calling directly from this static class.</p>
     * <p>For better security using Prepared statements, get DataSource instead.</p>
     * @param query SQL string (query, DML or DDL) to execute
     * @return number of rows added/updated
     */
    @Deprecated
    public static int executeSqlString(String query) {
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
        } catch(RuntimeException e) {
            System.out.println(e.getMessage());
        } catch(SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return rowCount;
    }

    /**
     * @return result set from executed SQL query.
     * <p>Use only after calling executeSqlString (static general call for manual SQL execution)</p>
     */
    @Deprecated
    public static ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * Obtain a list of all countries in the database
     * @return list of FirstLevelDivision containers with only country data
     */
    public static ObservableList<FirstLevelDivision> getAllCountries() {
        ObservableList<FirstLevelDivision> countriesList = FXCollections.observableArrayList();
        try {
            if (connection == null || connection.isClosed()) openConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            resultSet = statement.executeQuery(
                """
                        SELECT (Country_ID, Country) \s
                        FROM client_schedule.countries
                        """);

            while (resultSet.next()) {
                countriesList.add(new FirstLevelDivision(0, "",
                        resultSet.getInt("Country_ID"),
                        resultSet.getString("Country") ));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return countriesList;
    }

    /**
     * Get a list of all divisions
     * @return
     */
    public static ObservableList<FirstLevelDivision> getDivisionsByCountry(FirstLevelDivision selectedCountry) {
        ObservableList<FirstLevelDivision> divisionsList = FXCollections.observableArrayList();
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                """
                        SELECT countries.Country_ID, Country, Division_ID, Division\s
                        FROM client_schedule.countries\s
                        INNER JOIN first_level_divisions\s
                             ON countries.Country_ID = first_level_divisions.Country_ID\s
                        WHERE countries.Country_ID = ?
                        """)) {
            preparedStatement.setInt(1, selectedCountry.countryId());
            while (resultSet.next()) {
                divisionsList.add(new FirstLevelDivision(
                        resultSet.getInt("Division_ID"),
                        resultSet.getString("Division"),
                        resultSet.getInt("Country_ID"),
                        resultSet.getString("Country") ));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return divisionsList;
    }

}
