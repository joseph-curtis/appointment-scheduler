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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of {@link DAO.DataAccessObject} to persist Appointment objects from a database.
 * @author Joseph Curtis
 * @version 2022.07.23
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
                             SELECT Appointment_ID, Title, Description, Location, Type,
                                    Start, End, appointments.Customer_ID, Customer_Name,
                                    User_ID, appointments.Contact_ID, Contact_Name, Email
                             FROM client_schedule.appointments
                             INNER JOIN customers
                                  ON customers.Customer_ID = appointments.Customer_ID
                             INNER JOIN contacts
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
     * Get all the given user's appointments.
     * @param user the current user (the one logged-in)
     * @return list of all appointments assigned to the user
     * @throws SQLException if any error occurs.
     */
    public ObservableList<Appointment> getAllByUser(User user) throws SQLException {
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     """
                             SELECT Appointment_ID, Title, Description, Location, Type,
                                    Start, End, appointments.Customer_ID, Customer_Name,
                                    User_ID, appointments.Contact_ID, Contact_Name, Email
                             FROM client_schedule.appointments
                             INNER JOIN customers
                                  ON customers.Customer_ID = appointments.Customer_ID
                             INNER JOIN contacts
                                  ON contacts.Contact_ID = appointments.Contact_ID
                             WHERE User_ID = ?
                             """)) {
            statement.setInt(1, user.id());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                appointmentsList.add(createRecordFromResultSet(resultSet));
            }
        }
        return appointmentsList;
    }

    /**
     * Gets all DTO records that fall between the start and end dates (inclusive) for given user
     * @param startDate beginning of date range
     * @param endDate end of date range
     * @return list of all appointments between date range
     * @throws SQLException if any error occurs.
     */
    public ObservableList<Appointment> getAllBetweenDates(LocalDate startDate,
                                                          LocalDate endDate) throws SQLException {
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
        LocalDateTime startDateTime = startDate.atTime(0, 0, 0);
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 999999999);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     """
                             SELECT Appointment_ID, Title, Description, Location, Type,
                                    Start, End, appointments.Customer_ID, Customer_Name,
                                    User_ID, appointments.Contact_ID, Contact_Name, Email
                             FROM client_schedule.appointments
                             INNER JOIN customers
                                  ON customers.Customer_ID = appointments.Customer_ID
                             INNER JOIN contacts
                                  ON contacts.Contact_ID = appointments.Contact_ID
                             WHERE Start BETWEEN ? AND ?
                             OR End BETWEEN ? AND ?
                             """)) {
            statement.setTimestamp(1, Timestamp.valueOf(startDateTime));
            statement.setTimestamp(2, Timestamp.valueOf(endDateTime));
            statement.setTimestamp(3, Timestamp.valueOf(startDateTime));
            statement.setTimestamp(4, Timestamp.valueOf(endDateTime));
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
                         SELECT Appointment_ID, Title, Description, Location, Type,
                                    Start, End, appointments.Customer_ID, Customer_Name,
                                    User_ID, appointments.Contact_ID, Contact_Name, Email
                         FROM client_schedule.appointments
                         INNER JOIN customers
                              ON customers.Customer_ID = appointments.Customer_ID
                         INNER JOIN contacts
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
        if (getById(appointment.id()).isPresent())
            return false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                 """
                         INSERT INTO client_schedule.appointments
                         (Title, Description, Location, Type, Start, End,
                         Customer_ID, User_ID, Contact_ID,
                         Created_By, Last_Updated_By, Create_Date, Last_Update)
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

            return statement.executeUpdate() > 0;
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
                         UPDATE client_schedule.appointments SET
                         Title = ?,
                         Description = ?,
                         Location = ?,
                         Type = ?,
                         Start = ?,
                         End = ?,
                         Customer_ID = ?,
                         User_ID = ?,
                         Contact_ID = ?,
                         Last_Updated_By = ?,
                         Last_Update = CURRENT_TIMESTAMP
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

    /**
     * Get all Associated Appointments for a customer
     * @param id Target customer's id
     * @return list of all Appointments for customer
     * @throws SQLException if an error occurs.
     */
    public ObservableList<Appointment> getAllByCustomerId(int id) throws SQLException {
        ObservableList<Appointment> allAppointmentsByCustomer = FXCollections.observableArrayList();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     """
                             SELECT Appointment_ID, Title, Description, Location, Type,
                                        Start, End, appointments.Customer_ID, Customer_Name,
                                        User_ID, appointments.Contact_ID, Contact_Name, Email
                             FROM client_schedule.appointments
                             INNER JOIN customers
                                  ON customers.Customer_ID = appointments.Customer_ID
                             INNER JOIN contacts
                                  ON contacts.Contact_ID = appointments.Contact_ID
                             WHERE appointments.Customer_ID = ?
                             """)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                allAppointmentsByCustomer.add(createRecordFromResultSet(resultSet));
            }
        }
        return allAppointmentsByCustomer;
    }

}
