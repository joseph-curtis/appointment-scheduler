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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.*;
import utility.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for the add or modify Customer form.
 * @author Joseph Curtis
 * @version 2022.06.08
 */
public class CustomerController implements AuthenticatedController, Initializable {

    Customer existingCustomer;  // The Customer in the database to modify
    User user;           // The currently logged-in user

    @FXML private Label currentOperationLabel;
    @FXML private TextField idTxt;
    @FXML private TextField addressTxt;
    @FXML private TextField nameTxt;
    @FXML private TextField phoneTxt;
    @FXML private TextField postCodeTxt;
    @FXML private ComboBox<Country> countryComboBox;
    @FXML private ComboBox<FirstLevelDivision> divisionComboBox;

    /**
     * {@inheritDoc}
     */
    @Override
    public void passCurrentUser(DataTransferObject user) {
        this.user = (User) user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passExistingRecord(DataTransferObject passedObject) {
        if (passedObject == null) {
            existingCustomer = null;
            return;
        }
        existingCustomer = (Customer) passedObject;

        currentOperationLabel.setText("Edit Customer");

        idTxt.setText(String.valueOf(existingCustomer.id()));
        nameTxt.setText(existingCustomer.name());
        addressTxt.setText(existingCustomer.address());
        postCodeTxt.setText(existingCustomer.postalCode());
        phoneTxt.setText(existingCustomer.phone());

        countryComboBox.setValue(DBUtil.getCountryByDivisionId(existingCustomer.divisionId()).get());
        setDivisionComboBox();
        divisionComboBox.setValue(DBUtil.getDivisionById(existingCustomer.divisionId()).get());
    }

    /**
     * Initializes the controller class, setting the combo-box properties
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Country> allCountriesList = DBUtil.getAllCountries();

        for (Country country: allCountriesList) {
            countryComboBox.getItems().add(country);
        }
    }

    /**
     * Cancels add or modify operation and returns to the main menu.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionCancel(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    /**
     * Save this new or modified Customer.
     * <p>Updates existing customer, or adds new customer to database.</p>
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionSaveCustomer(ActionEvent event) {
        Customer savedCustomer;

        try {
            if (nameTxt.getText().isBlank()
                    || addressTxt.getText().isBlank()
                    || postCodeTxt.getText().isBlank()
                    || phoneTxt.getText().isBlank()
                    || countryComboBox.getValue() == null
                    || divisionComboBox.getValue() == null
            )
                throw new BlankInputException("Fields Cannot be Blank");

            // Acquire ID:
            int id;
            if (existingCustomer != null)
                id = existingCustomer.id();          // get ID of existing customer to edit
            else
                id = 0;  // inserting zero gets the next new ID

            // Get input from fields:
            String name = nameTxt.getText();
            String address = addressTxt.getText();
            String postCode = postCodeTxt.getText();
            String phone = phoneTxt.getText();
            int divisionId = divisionComboBox.getValue().id();

            // validate input:
            if (name.length() > 50)
                GuiUtil.handleLogicalError("Name cannot exceed 50 characters");
            if (address.length() > 100)
                GuiUtil.handleLogicalError("Address cannot exceed 100 characters");
            if (postCode.length() > 50)
                GuiUtil.handleLogicalError("Postal code is too long!");
            if (phone.length() > 50)
                GuiUtil.handleLogicalError("Phone number is too long!");

            // create Customer to save:
            savedCustomer = new Customer(id, name, address, postCode, phone, divisionId, "", "");

            // update database with Customer (add or modify):
            CustomerDaoImpl dbCustomers = new CustomerDaoImpl();
            if (existingCustomer == null) {
                // add new customer:
                if (!dbCustomers.add(savedCustomer, user))
                    throw new DataObjNotFoundException("Attempt to add Customer failed!", savedCustomer);
            } else {
                // save modified customer:
                if (!dbCustomers.update(savedCustomer, user)) {
                    int index = dbCustomers.getAll().indexOf(existingCustomer);
                    // check for record update fail:
                    if (index < 0)
                        throw new DataObjNotFoundException("Existing Customer to modify no longer exists!", savedCustomer);
                    else
                        throw new SQLException();
                }
            }
            // go back to the Main screen:
            ((Node)(event.getSource())).getScene().getWindow().hide();

        } catch(DataObjNotFoundException e) {
            GuiUtil.handleDataObjNotFoundException(e);
        } catch(BlankInputException e) {
            GuiUtil.handleBlankInputException(e);
        } catch(InvalidInputException e) {
            // Do nothing and return to add/modify customer screen
        } catch (SQLException e) {
            System.out.println("Database Error! Check connection and SQL");
            e.printStackTrace();
        }
    }

    /**
     * Populates the divisions in divisionComboBox for the selected country
     * @param event the user generated event (a selection made from countryComboBox)
     *              that caused this to execute
     */
    @FXML
    void onActionListCountries(ActionEvent event) {
        setDivisionComboBox();
    }

    private void setDivisionComboBox() {
        Country selectedCountry = countryComboBox.getValue();
        ObservableList<FirstLevelDivision> divisionList = DBUtil.getDivisionsByCountry(selectedCountry);
        divisionComboBox.getItems().clear();

        for (FirstLevelDivision division: divisionList) {
            divisionComboBox.getItems().add(division);
        }
    }

}
