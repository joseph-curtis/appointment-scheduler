package DAO;

import javafx.collections.ObservableList;
import model.Customer;
import utility.DBUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Implementation of {@link DAO.CustomerDao} to persist Customer objects from a database.
 * @author Joseph Curtis
 * @version 2022.04.21
 */
public class CustomerDaoImpl implements CustomerDao {

    private static final Logger log = Logger.getLogger("log.txt");

    private final DataSource dataSource;

    /**
     * Creates an instance of {@link CustomerDaoImpl} with provided <code>dataSource</code> object.
     * Datasource is used to get connections to database.
     * <p>Use getConnection method within a try-with-resources block.</p>
     */
    public CustomerDaoImpl() {
        dataSource = DBUtil.getDataSource();
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
                     "SELECT * FROM CUSTOMERS WHERE ID = ?")) {
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createCustomerRecord(resultSet));
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
    public boolean delete(Customer customer) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "DELETE FROM CUSTOMERS WHERE Customer_ID = ?")) {
            statement.setInt(1, customer.id());
            return statement.executeUpdate() > 0;
        }
    }

    private Customer createCustomerRecord(ResultSet resultSet) throws SQLException {
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
