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
import model.Country;
import model.FirstLevelDivision;
import model.User;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;

/**
 * Database Object (abstract) for executing SQL using Strings as input.
 * <p>Connects to the MySql database using JDBC driver and DriverManager for creating connections.</p>
 * <p>This is the generalized datasource object.</p>
 * <p>ResultSet is stored as static variable, retrieve by calling getResultSet</p>
 * @author Joseph Curtis
 * @version 2022.06.08
 */
public abstract class DBUtil {
    // connection string parameters
    private static final String MYSQL_JDBC_DRIVER = "db.driver.class.name";
    private static final String DB_URL = "db.url";
    private static final String USERNAME = "db.username";
    private static final String PASSWORD = "db.password";

    private static final MysqlDataSource dataSource = new MysqlDataSource();

    private static Connection connection = null;
    private static Statement statement;
    private static ResultSet resultSet;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/DbConnection.properties"));

            dataSource.setUrl(properties.getProperty(DB_URL));
            dataSource.setUser(properties.getProperty(USERNAME));
            dataSource.setPassword(properties.getProperty(PASSWORD));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get DataSource object (MysqlDataSource)
     *
     * @return JDBC DataSource object used to get connections to database
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Open DB connection manually using DriverManager class.
     * <p>Only use if calling directly from this static class.</p>
     */
    @Deprecated
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
    @Deprecated
    public static void closeConnection() {
        try {
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes statement from given string.
     * <p>Only use if calling directly from this static class.</p>
     * <p>For better security using Prepared statements, get DataSource instead.</p>
     *
     * @param query SQL string (query, DML or DDL) to execute
     * @return number of rows added/updated
     */
    @Deprecated
    public static int executeSqlString(String query) {
        int rowCount = -1;
        try {
            if (connection == null || connection.isClosed()) openConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);

            if (query.toUpperCase().startsWith("SELECT")) {
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
            } else if (query.toUpperCase().startsWith("INSERT")
                    || query.toUpperCase().startsWith("UPDATE")
                    || query.toUpperCase().startsWith("DELETE")) {
                System.out.println("Executing DML statement");
                rowCount = statement.executeUpdate(query);
            } else {
                System.out.println("Executing DDL statement");
                rowCount = statement.executeUpdate(query);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
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
    public static ObservableList<Country> getAllCountries() {
        ObservableList<Country> countriesList = FXCollections.observableArrayList();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     """
                             SELECT Country_ID, Country \s
                             FROM client_schedule.countries
                             """)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                countriesList.add(new Country(
                        resultSet.getInt("Country_ID"),
                        resultSet.getString("Country")));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return countriesList;
    }

    /**
     * Trade a first-level division id for the country that it is located in.
     * @param id the selected first-level division id
     * @return the country associated with the first-level-division
     */
    public static Optional<Country> getCountryByDivisionId(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     """
                             SELECT countries.Country_ID, Country\s
                             FROM client_schedule.countries\s
                             INNER JOIN first_level_divisions\s
                                  ON countries.Country_ID = first_level_divisions.Country_ID\s
                             WHERE first_level_divisions.Division_ID = ?
                             """)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // search successful, return associated country:
                return Optional.of(new Country(
                        resultSet.getInt("Country_ID"),
                        resultSet.getString("Country")
                ));
            } else {
                System.out.println("Orphaned Division! No associated country!");
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();    // empty container means division id did not find related country
    }

    public static Optional<FirstLevelDivision> getDivisionById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     """
                             SELECT Division_ID, Division, first_level_divisions.Country_ID, Country\s
                             FROM client_schedule.first_level_divisions\s
                             INNER JOIN countries\s
                                  ON countries.Country_ID = first_level_divisions.Country_ID\s
                             WHERE first_level_divisions.Division_ID = ?
                             """)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // division found, return it:
                return Optional.of(new FirstLevelDivision(
                        resultSet.getInt("Division_ID"),
                        resultSet.getString("Division"),
                        resultSet.getInt("Country_ID"),
                        resultSet.getString("Country")
                ));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();    // division id does not exist!
    }

    /**
     * Get a list of all divisions
     * @param country selected DTO with country data
     * @return all divisions per the specified country in selectedCountry
     */
    public static ObservableList<FirstLevelDivision> getDivisionsByCountry(Country country) {
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
            preparedStatement.setInt(1, country.id());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                divisionsList.add(new FirstLevelDivision(
                        resultSet.getInt("Division_ID"),
                        resultSet.getString("Division"),
                        resultSet.getInt("Country_ID"),
                        resultSet.getString("Country")));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return divisionsList;
    }

    /**
     * Checks database to authenticate valid username and password combination
     * @param username
     * @param password
     * @return the authenticated user, or an empty optional if lookup failed.
     */
    public static Optional<User> authenticateUser(String username, String password) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     """
                             SELECT User_ID, User_Name\s
                             FROM client_schedule.users\s
                             WHERE User_Name = ? AND Password = ?
                             """)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // authentication success! return user that logged in
                return Optional.of(new User(
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();    // authentication fail, return empty container
    }

}
