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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import model.Customer;
import model.DataTransferObject;
import model.User;
import utility.BlankInputException;
import utility.DataObjNotFoundException;
import utility.GuiUtil;
import utility.InvalidInputException;

import java.sql.SQLException;

/**
 * Controller for the add or modify Customer form.
 * @author Joseph Curtis
 * @version 2022.05.25
 */
public class CustomerController implements AuthenticatedController {

    Customer existingCustomer;  // The Customer in the database to modify
    User user;           // The currently logged-in user

    /**
     * Authenticates user that is signed in.
     * @param user currently logged-in user
     */
    @Override
    public void passCurrentUser(DataTransferObject user) {
        this.user = (User) user;
    }

    /**
     * Sets all properties for edited item to populate to corresponding text fields.
     * <p>The existing Customer in DB is passed when changing the scene</p>
     * @see utility.GuiUtil#newStage(ActionEvent, DataTransferObject, DataTransferObject, String, String, Modality)
     * @param passedObject existing customer to be edited
     */
    @Override
    public void passExistingRecord(DataTransferObject passedObject) {
        if (passedObject == null) {
            existingCustomer = null;
            return;
        }
        existingCustomer = (Customer) passedObject;

        currentOperationLabel.setText("Edit Customer");

        idTxt.setText(String.valueOf(passedObject.id()));
        nameTxt.setText(((Customer) passedObject).name());
        addressTxt.setText(((Customer) passedObject).address());
        postCodeTxt.setText(((Customer) passedObject).postalCode());
        phoneTxt.setText(((Customer) passedObject).phone());




        // TODO:  set division combo box
        // TODO:  set country combo box
    }

    @FXML
    private Label currentOperationLabel;

    @FXML
    private TextField idTxt;

    @FXML
    private TextField addressTxt;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private TextField postCodeTxt;

    @FXML
    private ComboBox<?> countryComboBox;

    @FXML
    private ComboBox<?> divisionComboBox;

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
//                    || divisionComboBox.isBlank()
//                    || countryComboBox.isBlank()
                    // TODO check for blank combobox selection
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

            // TODO get combo box to display division names, but also get the ID ....
            int divisionId = 0;

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
        }
        catch(DataObjNotFoundException exception) {
            GuiUtil.handleDataObjNotFoundException(exception);
        }
        catch(BlankInputException exception) {
            GuiUtil.handleBlankInputException(exception);
        }
        catch(InvalidInputException exception) {

            // TODO use InvalidInputException for parsing DateTime fields

        } catch (SQLException e) {
            System.out.println("Database Error! Check connection and SQL");
            e.printStackTrace();
        }
    }

    @FXML
    void onActionListCountries(ActionEvent event) {

        // TODO : implement on click method
    }

    @FXML
    void onActionListDivisions(ActionEvent event) {

        // TODO : implement on click method
    }

}
