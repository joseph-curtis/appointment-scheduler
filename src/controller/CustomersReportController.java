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

package controller;

import DAO.CustomerDaoImpl;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import model.Customer;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the Custom Report (customer total by country).
 * @author Joseph Curtis
 * @version 2022.08.15
 */
public class CustomersReportController implements Initializable {

    ObservableList<Customer> allCustomers;
    List<Customer> usCustomers;
    List<Customer> ukCustomers;
    List<Customer> canadaCustomers;
    List<Customer> otherCustomers;

    @FXML private TextField usTxtField;
    @FXML private TextField ukTxtField;
    @FXML private TextField canadaTxtField;
    @FXML private TextField otherTxtField;

    /**
     * Initializes the controller class, getting the list of all customers from the database,
     * and setting a filtered list for each output and displaying the totals for each list.
     * Stream is used here to filter out a collection in order to make a new collection.  Streams utilize
     * lambda functions to determine what to keep and what to discard from the output stream.
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // save all appointments as list
        try {
            CustomerDaoImpl customersDb = new CustomerDaoImpl();
            allCustomers = customersDb.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // save each filtered list :
        usCustomers = allCustomers.stream()
                .filter((Customer customer) -> customer.country().equalsIgnoreCase("us")
                        || customer.country().toLowerCase().contains("u.s"))
                .collect(Collectors.toList());
        usTxtField.setText(String.valueOf((long) usCustomers.size()));

        ukCustomers = allCustomers.stream()
                .filter((Customer customer) -> customer.country().equalsIgnoreCase("uk")
                        || customer.country().toLowerCase().contains("u.k"))
                .collect(Collectors.toList());
        ukTxtField.setText(String.valueOf((long) ukCustomers.size()));

        canadaCustomers = allCustomers.stream()
                .filter((Customer customer) -> customer.country().equalsIgnoreCase("canada"))
                .collect(Collectors.toList());
        canadaTxtField.setText(String.valueOf((long) canadaCustomers.size()));

        otherCustomers = allCustomers.stream()
                .filter(item -> !usCustomers.contains(item))
                .filter(item -> !ukCustomers.contains(item))
                .filter(item -> !canadaCustomers.contains(item))
                .collect(Collectors.toList());
        otherTxtField.setText(String.valueOf((long) otherCustomers.size()));
    }

    /**
     * Closes the report.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionOk(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
