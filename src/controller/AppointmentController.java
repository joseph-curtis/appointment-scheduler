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

import DAO.AppointmentDaoImpl;
import DAO.CustomerDaoImpl;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import model.*;
import utility.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Controller for the add or modify Appointment form.
 * @author Joseph Curtis
 * @version 2022.06.07
 */
public class AppointmentController implements AuthenticatedController, Initializable {

    Appointment existingAppointment;    // The Appointment in the database to modify
    User user;                          // The currently logged-in user

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
     * <p>The existing Appointment in DB is passed when changing the scene</p>
     * @see utility.GuiUtil#newStage(ActionEvent, DataTransferObject, DataTransferObject, String, String, Modality)
     * @param passedObject existing appointment to be edited
     */
    @Override
    public void passExistingRecord(DataTransferObject passedObject) {
        if (passedObject == null) {
            existingAppointment = null;
            return;
        }
        existingAppointment = (Appointment) passedObject;

        currentOperationLabel.setText("Edit Appointment");

        idTxt.setText(String.valueOf(existingAppointment.id()));
        titleTxt.setText(existingAppointment.title());
        descriptionTxt.setText(existingAppointment.description());
        locationTxt.setText(existingAppointment.location());
        typeTxt.setText(existingAppointment.type());

        startHourSpinner.getValueFactory().setValue(existingAppointment.start().getHour());
        startMinuteSpinner.getValueFactory().setValue(existingAppointment.start().getMinute());
        endHourSpinner.getValueFactory().setValue(existingAppointment.end().getHour());
        endMinuteSpinner.getValueFactory().setValue(existingAppointment.end().getMinute());

        startDatePicker.setValue(existingAppointment.start().toLocalDate());
        endDatePicker.setValue(existingAppointment.end().toLocalDate());

        // set customer combo box:
        CustomerDaoImpl dbCustomers = new CustomerDaoImpl();
        try {
            customerComboBox.setValue(dbCustomers.getById(existingAppointment.customerId()).get());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // set contact combo box:
        contactComboBox.setValue(DBUtil.getContactById(existingAppointment.contactId()).get());
    }

    /**
     * Initializes the controller class, formatting the start and end spinners.
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<Integer> startHourSvf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23);
        startHourSvf.setWrapAround(true);
        SpinnerValueFactory<Integer> startMinSvf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59);
        startMinSvf.setWrapAround(true);
        SpinnerValueFactory<Integer> endHourSvf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23);
        endHourSvf.setWrapAround(true);
        SpinnerValueFactory<Integer> endMinSvf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59);
        endMinSvf.setWrapAround(true);

        startHourSpinner.setValueFactory(startHourSvf);
        startMinuteSpinner.setValueFactory(startMinSvf);
        endHourSpinner.setValueFactory(endHourSvf);
        endMinuteSpinner.setValueFactory(endMinSvf);

        // set customer combo-box:
        CustomerDaoImpl dbCustomers = new CustomerDaoImpl();
        try {
            ObservableList<Customer> allCustomersList = dbCustomers.getAll();

            for (Customer customer: allCustomersList) {
                customerComboBox.getItems().add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // set contact combo-box:
        ObservableList<Contact> allContactsList = DBUtil.getAllContacts();

        for (Contact contact: allContactsList) {
            contactComboBox.getItems().add(contact);
        }
    }

    @FXML
    private Label currentOperationLabel;

    @FXML
    private TextField descriptionTxt;

    @FXML
    private TextField idTxt;

    @FXML
    private TextField locationTxt;

    @FXML
    private TextField titleTxt;

    @FXML
    private TextField typeTxt;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private Spinner<Integer> startHourSpinner;

    @FXML
    private Spinner<Integer> startMinuteSpinner;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Spinner<Integer> endHourSpinner;

    @FXML
    private Spinner<Integer> endMinuteSpinner;

    @FXML
    private ComboBox<Contact> contactComboBox;

    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    void onActionCancel(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    void onActionSaveAppointment(ActionEvent event) {
        Appointment savedAppointment;

        // validate all inputs are not blank:
        try {
            if (titleTxt.getText().isBlank()
                    || descriptionTxt.getText().isBlank()
                    || locationTxt.getText().isBlank()
                    || typeTxt.getText().isBlank()
                    || customerComboBox.getValue() == null
                    || contactComboBox.getValue() == null
            )
                throw new BlankInputException("Fields Cannot be Blank");
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null)
                throw new BlankInputException("Please choose a start and end date");
            if (startHourSpinner.getValue() == null
                    || startMinuteSpinner.getValue() == null
                    || endHourSpinner.getValue() == null
                    || endMinuteSpinner.getValue() == null)
                throw new BlankInputException("Please enter hour & minute for start and end times");

            // Acquire ID:
            int id;
            if (existingAppointment != null)
                id = existingAppointment.id();          // get ID of existing customer to edit
            else
                id = 0;  // inserting zero gets the next new ID

            // Get input from fields:
            String title = titleTxt.getText();
            String description = descriptionTxt.getText();
            String location = locationTxt.getText();
            String type = typeTxt.getText();
            int customerId = customerComboBox.getValue().id();
            int contactId = contactComboBox.getValue().id();

            // get LocalDate from date pickers, and convert to LocalDateTime
            // using Integers from spinners as hour and minute
            LocalDateTime startLdt = startDatePicker.getValue().atTime(
                    startHourSpinner.getValue(), startMinuteSpinner.getValue());
            LocalDateTime endLdt = endDatePicker.getValue().atTime(
                    endHourSpinner.getValue(), endMinuteSpinner.getValue());

            // validate input:
            if (title.length() > 50)
                GuiUtil.handleLogicalError("Title cannot exceed 50 characters");
            if (description.length() > 50)
                GuiUtil.handleLogicalError("Description cannot exceed 50 characters");
            if (location.length() > 50)
                GuiUtil.handleLogicalError("Location cannot exceed 50 characters");
            if (type.length() > 50)
                GuiUtil.handleLogicalError("Type cannot exceed 50 characters");

            // create Appointment to save:
            savedAppointment = new Appointment(id, title, description, location, type,
                    startLdt, endLdt,
                    customerId, "", user.id(), contactId, "", "");

            // update database with Appointment (add or modify):
            AppointmentDaoImpl dbAppointments = new AppointmentDaoImpl();
            if (existingAppointment == null) {
                // add new appointment:
                if (!dbAppointments.add(savedAppointment, user))
                    throw new DataObjNotFoundException("Attempt to add Appointment failed!", savedAppointment);
            } else {
                // save modified appointment:
                if (!dbAppointments.update(savedAppointment, user)) {
                    int index = dbAppointments.getAll().indexOf(existingAppointment);
                    // check for record update fail:
                    if (index < 0)
                        throw new DataObjNotFoundException("Existing Appointment to modify no longer exists!", savedAppointment);
                    else
                        throw new SQLException();
                }
            }
            // go back to the Main screen:
            ((Node)(event.getSource())).getScene().getWindow().hide();

        } catch (DataObjNotFoundException e) {
            GuiUtil.handleDataObjNotFoundException(e);
        } catch (BlankInputException e) {
            GuiUtil.handleBlankInputException(e);
        } catch (InvalidInputException e) {
            // Do nothing and return to add/modify appointment screen
        } catch (SQLException e) {
            System.out.println("Database Error! Check connection and SQL");
            e.printStackTrace();
        }

    }

}
