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
import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementation of {@link DAO.DataAccessObject} to persist Customer objects from a database.
 * @author Joseph Curtis
 * @version 2022.05.19
 */
public class CustomerDaoImpl extends DataAccessObject<Customer> {

    /**
     * Gets an ID for newly created Customer, ensuring no conflicts.
     * @return an ID unique to the CUSTOMERS table in database
     */
    public static int acquireNewId() {

        // TODO:  implement method!

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<Customer> getAll() throws SQLException {

        return null;    // TODO implement method
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> getById(int id) throws SQLException {
        ResultSet resultSet = null;

        // TODO:  join countries and first-level divisions tables

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "SELECT * FROM CUSTOMERS WHERE Customer_ID = ?")) {
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
    public boolean add(Customer customer) throws SQLException {
        if (getById(customer.id()).isPresent()) {
            return false;
        }

        // TODO:  insert Create_Date (DATETIME) field

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "INSERT INTO CUSTOMERS VALUES (?,?,?,?,?,?)")) {
            statement.setInt(1, customer.id());
            statement.setString(2, customer.name());
            statement.setString(3, customer.address());
            statement.setString(4, customer.postalCode());
            statement.setString(5, customer.phone());
            statement.setInt(6, customer.divisionId());
            return statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Customer customer) throws SQLException {

        //TODO:  insert Last_Update (TIMESTAMP) field

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "UPDATE CUSTOMERS SET " +
                             "Customer_Name = ?, " +
                             "Address = ?, " +
                             "Postal_Code = ?, " +
                             "Phone = ?, " +
                             "Division_ID = ? " +
                         "WHERE Customer_ID = ?")) {
            statement.setString(1, customer.name());
            statement.setString(2, customer.address());
            statement.setString(3, customer.postalCode());
            statement.setString(4, customer.phone());
            statement.setInt(5, customer.divisionId());
            statement.setInt(6, customer.id());
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
                     "DELETE FROM CUSTOMERS WHERE Customer_ID = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Customer createDtoRecord(ResultSet resultSet) throws SQLException {

        // TODO:  get division and country from table JOIN

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
