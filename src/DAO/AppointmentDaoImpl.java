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

import javafx.collections.ObservableList;
import model.Appointment;

import java.sql.*;
import java.util.Optional;

/**
 * Implementation of {@link DAO.DataAccessObject} to persist Appointment objects from a database.
 * @author Joseph Curtis
 * @version 2022.05.19
 */
public class AppointmentDaoImpl extends DataAccessObject<Appointment> {

    /**
     * Gets an ID for newly created Appointment, ensuring no conflicts.
     * @return a new ID unique to the APPOINTMENTS table in database
     */
    public static int getUniqueId() {

        // TODO:  implement method!

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<Appointment> getAll() throws SQLException {

        return null;    // TODO implement method
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Appointment> getById(int id) throws SQLException {
        ResultSet resultSet = null;

        // TODO:  join customers and contacts tables

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "SELECT * FROM APPOINTMENTS WHERE Appointment_ID = ?")) {
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createDtoRecord(resultSet));
            } else {
                return Optional.empty();
            }
        }
        finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Appointment appointment) throws SQLException {
        if (getById(appointment.id()).isPresent()) {
            return false;
        }

        // TODO:  insert Create_Date (DATETIME) field

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "INSERT INTO APPOINTMENTS VALUES (?,?,?,?,?,?,?,?,?,?)")) {
            statement.setInt(1, appointment.id());
            statement.setString(2, appointment.title());
            statement.setString(3, appointment.description());
            statement.setString(4, appointment.location());
            statement.setString(5, appointment.type());
            statement.setTimestamp(6, Timestamp.valueOf(appointment.start()));
            statement.setTimestamp(7, Timestamp.valueOf(appointment.end()));
            statement.setInt(8, appointment.customerId());
            statement.setInt(9, appointment.userId());
            statement.setInt(10, appointment.contactId());
            return statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Appointment appointment) throws SQLException {

        //TODO:  insert Last_Update (TIMESTAMP) field

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "UPDATE APPOINTMENTS SET " +
                             "Title = ?, " +
                             "Description = ?, " +
                             "Location = ?, " +
                             "Type = ?, " +
                             "Start = ? " +
                             "End = ? " +
                             "Customer_ID = ? " +
                             "User_ID = ? " +
                             "Contact_ID = ? " +
                             "WHERE Appointment_ID = ?")) {
            statement.setString(1, appointment.title());
            statement.setString(2, appointment.description());
            statement.setString(3, appointment.location());
            statement.setString(4, appointment.type());
            statement.setTimestamp(5, Timestamp.valueOf(appointment.start()));
            statement.setTimestamp(6, Timestamp.valueOf(appointment.end()));
            statement.setInt(7, appointment.customerId());
            statement.setInt(8, appointment.userId());
            statement.setInt(9, appointment.contactId());
            statement.setInt(10, appointment.id());
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "DELETE FROM APPOINTMENTS WHERE Appointment_ID = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Appointment createDtoRecord(ResultSet resultSet) throws SQLException {

        // TODO:  get contact name and email from table JOIN

        return new Appointment(
                resultSet.getInt("Appointment_ID"),
                resultSet.getString("Title"),
                resultSet.getString("Description"),
                resultSet.getString("Location"),
                resultSet.getString("Type"),
                resultSet.getTimestamp("Start").toLocalDateTime(),
                resultSet.getTimestamp("End").toLocalDateTime(),
                resultSet.getInt("Customer_ID"),
                resultSet.getInt("User_ID"),
                resultSet.getInt("Contact_ID"),
                resultSet.getString("Contact_Name"),
                resultSet.getString("Email"));
    }
}
