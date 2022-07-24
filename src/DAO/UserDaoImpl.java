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

package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import utility.DBUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementation of {@link DAO.ImmutableDAO} to read User objects from a database.
 * <p>Datasource constant is used to get connections to database using {@link DBUtil} class.</p>
 * <p>Use <code>getConnection()</code> method within a try-with-resources block.</p>
 * @author Joseph Curtis
 * @version 2022.07.23
 */
public class UserDaoImpl implements ImmutableDAO<User> {

    protected static final DataSource dataSource = DBUtil.getDataSource();

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<User> getAll() {
        ObservableList<User> usersList = FXCollections.observableArrayList();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     """
                             SELECT User_ID, User_Name
                             FROM client_schedule.users
                             """)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                usersList.add(new User(
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return usersList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> getById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     """
                             SELECT User_ID, User_Name
                             FROM client_schedule.users
                             WHERE User_ID = ?
                             """)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // user found, return it:
                return Optional.of(new User(
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();    // user id does not exist!
    }

    /**
     * Checks database to authenticate valid username and password combination
     * @param username login name
     * @param password login password
     * @return the authenticated user, or an empty optional if lookup failed.
     */
    public static Optional<User> authenticateUser(String username, String password) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     """
                             SELECT User_ID, User_Name
                             FROM client_schedule.users
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
