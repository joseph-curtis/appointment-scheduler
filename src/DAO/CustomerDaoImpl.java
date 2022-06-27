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
import model.Customer;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementation of {@link DAO.DataAccessObject} to persist Customer objects from a database.
 * @author Joseph Curtis
 * @version 2022.05.24
 */
public class CustomerDaoImpl extends DataAccessObject<Customer, User> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<Customer> getAll() throws SQLException {
        ObservableList<Customer> customersList = FXCollections.observableArrayList();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                 """
                         SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone,
                                customers.Division_ID, Division, Country
                         FROM client_schedule.customers
                         INNER JOIN first_level_divisions
                              ON customers.Division_ID = first_level_divisions.Division_ID
                         INNER JOIN countries
                              ON first_level_divisions.Country_ID = countries.Country_ID
                         """)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                customersList.add(createRecordFromResultSet(resultSet));
            }
        }
        return customersList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> getById(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                 """
                         SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone,
                                customers.Division_ID, Division, Country
                         FROM client_schedule.customers
                         INNER JOIN first_level_divisions
                              ON customers.Division_ID = first_level_divisions.Division_ID
                         INNER JOIN countries
                              ON first_level_divisions.Country_ID = countries.Country_ID
                         WHERE Customer_ID = ?
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
    public boolean add(Customer customer, User user) throws SQLException {
        if (getById(customer.id()).isPresent())
            return false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                 """
                         INSERT INTO client_schedule.customers
                         (Customer_Name, Address, Postal_Code, Phone, Division_ID,
                         Created_By, Last_Updated_By, Create_Date, Last_Update)
                         VALUES (?,?,?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)
                         """)) {
            statement.setString(1, customer.name());
            statement.setString(2, customer.address());
            statement.setString(3, customer.postalCode());
            statement.setString(4, customer.phone());
            statement.setInt(5, customer.divisionId());
            statement.setString(6, user.name());
            statement.setString(7, user.name());
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Customer customer, User user) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                 """
                         UPDATE client_schedule.customers SET
                         Customer_Name = ?,
                         Address = ?,
                         Postal_Code = ?,
                         Phone = ?,
                         Division_ID = ?,
                         Last_Update = CURRENT_TIMESTAMP,
                         Last_Updated_By = ?
                         WHERE Customer_ID = ?
                         """)) {
            statement.setString(1, customer.name());
            statement.setString(2, customer.address());
            statement.setString(3, customer.postalCode());
            statement.setString(4, customer.phone());
            statement.setInt(5, customer.divisionId());
            statement.setString(6, user.name());
            statement.setInt(7, customer.id());
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
                 "DELETE FROM client_schedule.customers WHERE Customer_ID = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Customer createRecordFromResultSet(ResultSet resultSet) throws SQLException {
        return new Customer(resultSet.getInt("Customer_ID"),
                resultSet.getString("Customer_Name"),
                resultSet.getString("Address"),
                resultSet.getString("Postal_Code"),
                resultSet.getString("Phone"),
                resultSet.getInt("Division_ID"),
                resultSet.getString("Division"),
                resultSet.getString("Country"));
    }
}
