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
import model.Appointment;
import model.User;

import java.sql.*;
import java.util.Optional;

/**
 * Implementation of {@link DAO.DataAccessObject} to persist Appointment objects from a database.
 * @author Joseph Curtis
 * @version 2022.05.24
 */
public class AppointmentDaoImpl extends DataAccessObject<Appointment, User> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<Appointment> getAll() throws SQLException {
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     """
                             SELECT * FROM client_schedule.appointments\s
                             JOIN customers\s
                                  ON customers.Customer_ID = appointments.Customer_ID\s
                             JOIN contacts\s
                                  ON contacts.Contact_ID = appointments.Contact_ID
                             """)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointmentsList.add(createRecordFromResultSet(resultSet));
            }
        }
        return appointmentsList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Appointment> getById(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                 """
                         SELECT * FROM APPOINTMENTS\s
                         JOIN customers\s
                              ON customers.Customer_ID = appointments.Customer_ID\s
                         JOIN contacts\s
                              ON contacts.Contact_ID = appointments.Contact_ID
                         WHERE Appointment_ID = ?
                         """)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createRecordFromResultSet(resultSet));
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Appointment appointment, User user) throws SQLException {
        if (getById(appointment.id()).isPresent()) {
            return false;
        }

        // TODO:  insert Create_Date (DATETIME) field

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                 """
                         INSERT INTO client_schedule.appointments\s
                         (Title, Description, Location, Type, Start, End,\s
                         Customer_ID, User_ID, Contact_ID,\s
                         Created_By, Last_Updated_By, Create_Date, Last_Update)\s
                         VALUES (?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)
                         """)) {
            statement.setString(1, appointment.title());
            statement.setString(2, appointment.description());
            statement.setString(3, appointment.location());
            statement.setString(4, appointment.type());
            statement.setTimestamp(5, Timestamp.valueOf(appointment.start()));
            statement.setTimestamp(6, Timestamp.valueOf(appointment.end()));
            statement.setInt(7, appointment.customerId());
            statement.setInt(8, appointment.userId());
            statement.setInt(9, appointment.contactId());
            statement.setString(10, user.name());
            statement.setString(11, user.name());

            return statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Appointment appointment, User user) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                 """
                         UPDATE client_schedule.appointments SET\s
                         Title = ?,\s
                         Description = ?,\s
                         Location = ?,\s
                         Type = ?,\s
                         Start = ?,\s
                         End = ?,\s
                         Customer_ID = ?,\s
                         User_ID = ?,\s
                         Contact_ID = ?,\s
                         Last_Updated_By = ?\s
                         Last_Update = CURRENT_TIMESTAMP,\s
                         WHERE Appointment_ID = ?
                         """)) {
            statement.setString(1, appointment.title());
            statement.setString(2, appointment.description());
            statement.setString(3, appointment.location());
            statement.setString(4, appointment.type());
            statement.setTimestamp(5, Timestamp.valueOf(appointment.start()));
            statement.setTimestamp(6, Timestamp.valueOf(appointment.end()));
            statement.setInt(7, appointment.customerId());
            statement.setInt(8, appointment.userId());
            statement.setInt(9, appointment.contactId());
            statement.setString(10, user.name());
            statement.setInt(11, appointment.id());
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
                 "DELETE FROM client_schedule.appointments WHERE Appointment_ID = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Appointment createRecordFromResultSet(ResultSet resultSet) throws SQLException {
        return new Appointment(
                resultSet.getInt("Appointment_ID"),
                resultSet.getString("Title"),
                resultSet.getString("Description"),
                resultSet.getString("Location"),
                resultSet.getString("Type"),
                resultSet.getTimestamp("Start").toLocalDateTime(),
                resultSet.getTimestamp("End").toLocalDateTime(),
                resultSet.getInt("Customer_ID"),
                resultSet.getString("Customer_Name"),
                resultSet.getInt("User_ID"),
                resultSet.getInt("Contact_ID"),
                resultSet.getString("Contact_Name"),
                resultSet.getString("Email"));
    }
}
