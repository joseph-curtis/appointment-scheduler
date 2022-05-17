package DAO;

import javafx.collections.ObservableList;
import model.Customer;

import java.util.Optional;

/**
 * Data Access Object for Customer data.
 * @author Joseph Curtis
 * @version 2022.03.04
 */
public interface CustomerDao {

    /**
     * @return all the customers as an observable list.
     * @throws Exception if any error occurs.
     */
    ObservableList<Customer> getAll() throws Exception;

    /**
     * @param id unique identifier of the customer.
     * @return an optional container with a Customer object
     * if one with id exists, empty optional otherwise.
     * @throws Exception if any error occurs.
     */
    Optional <Customer> getById(int id) throws Exception;

    /**
     * @param customer the customer to be added.
     * @return true if customer is added, false if customer already exists.
     * @throws Exception if any error occurs.
     */
    boolean add(Customer customer) throws Exception;

    /**
     * @param customer the customer to be updated.
     * @return true if customer exists and is updated, else false.
     * @throws Exception if any error occurs.
     */
    boolean update(Customer customer) throws Exception;

    /**
     * @param customer the customer to be deleted.
     * @return true if customer exists and is deleted, false otherwise.
     * @throws Exception if any error occurs.
     */
    boolean delete(Customer customer) throws Exception;
}
