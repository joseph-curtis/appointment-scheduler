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
import model.Contact;
import utility.DBUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementation of {@link DAO.ImmutableDAO} to read Contact objects from a database.
 * <p>Datasource constant is used to get connections to database using {@link DBUtil} class.</p>
 * <p>Use <code>getConnection()</code> method within a try-with-resources block.</p>
 * @author Joseph Curtis
 * @version 2022.07.23
 */
public class ContactDaoImpl implements ImmutableDAO<Contact> {

    protected static final DataSource dataSource = DBUtil.getDataSource();

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<Contact> getAll() {
        ObservableList<Contact> contactsList = FXCollections.observableArrayList();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     """
                             SELECT Contact_ID, Contact_Name, Email
                             FROM client_schedule.contacts
                             """)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                contactsList.add(new Contact(
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name"),
                        resultSet.getString("Email")
                ));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return contactsList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Contact> getById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     """
                             SELECT Contact_ID, Contact_Name, Email
                             FROM client_schedule.contacts
                             WHERE Contact_ID = ?
                             """)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // contact found, return it:
                return Optional.of(new Contact(
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name"),
                        resultSet.getString("Email")
                ));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();    // contact id does not exist!
    }

}
