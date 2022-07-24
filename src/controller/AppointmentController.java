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
import DAO.ContactDaoImpl;
import DAO.CustomerDaoImpl;
import DAO.UserDaoImpl;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import model.*;
import utility.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;

/**
 * Controller for the add or modify Appointment form.
 * @author Joseph Curtis
 * @version 2022.07.23
 */
public class AppointmentController implements AuthenticatedController, Initializable {

    Appointment existingAppointment;    // The Appointment in the database to modify
    User user;                          // The currently logged-in user

    @FXML private Label currentOperationLabel;
    @FXML private TextField descriptionTxt;
    @FXML private TextField idTxt;
    @FXML private TextField locationTxt;
    @FXML private TextField titleTxt;
    @FXML private TextField typeTxt;
    @FXML private DatePicker startDatePicker;
    @FXML private Spinner<Integer> startHourSpinner;
    @FXML private Spinner<Integer> startMinuteSpinner;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> endHourSpinner;
    @FXML private Spinner<Integer> endMinuteSpinner;
    @FXML private ComboBox<Contact> contactComboBox;
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<User> userComboBox;

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
        ContactDaoImpl dbContacts = new ContactDaoImpl();
        contactComboBox.setValue(dbContacts.getById(existingAppointment.contactId()).get());

        // set user combo box:
        UserDaoImpl dbUsers = new UserDaoImpl();
        userComboBox.setValue(dbUsers.getById(existingAppointment.userId()).get());
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
        ContactDaoImpl dbContacts = new ContactDaoImpl();
        ObservableList<Contact> allContactsList = dbContacts.getAll();

        for (Contact contact: allContactsList) {
            contactComboBox.getItems().add(contact);
        }

        // set user combo-box:
        UserDaoImpl dbUsers = new UserDaoImpl();
        ObservableList<User> allUsersList = dbUsers.getAll();

        for (User user: allUsersList) {
            userComboBox.getItems().add(user);
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
     * Save this new or modified Appointment.
     * <p>Updates existing appointment, or adds new appointment to database.</p>
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
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
                    || userComboBox.getValue() == null
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
            int inputUserId = userComboBox.getValue().id();

            // get LocalDate from date pickers, and convert to LocalDateTime
            // using Integers from spinners as hour and minute
            LocalDateTime startLocalDT = startDatePicker.getValue().atTime(
                    startHourSpinner.getValue(), startMinuteSpinner.getValue());
            LocalDateTime endLocalDT = endDatePicker.getValue().atTime(
                    endHourSpinner.getValue(), endMinuteSpinner.getValue());

            // validate input string lengths:
            if (title.length() > 50)
                throw new InvalidInputException("Title cannot exceed 50 characters");
            if (description.length() > 50)
                throw new InvalidInputException("Description cannot exceed 50 characters");
            if (location.length() > 50)
                throw new InvalidInputException("Location cannot exceed 50 characters");
            if (type.length() > 50)
                throw new InvalidInputException("Type cannot exceed 50 characters");



            // setup AppointmentDaoImpl and Appointment List for Logical error checks:
            AppointmentDaoImpl dbAppointments = new AppointmentDaoImpl();
            ObservableList<Appointment> overlappingAppointmentList =
                    dbAppointments.getAllByCustomerId(customerId);
            // skip over the current appointment we are editing (using lambda)
            overlappingAppointmentList.removeIf(a -> a.id().equals(id));

            // // // // // // // // // // // // // // // // // // // // // // //
            // ======  Validate start/end input logical error checks: ======= //
            // // // // // // // // // // // // // // // // // // // // // // //

            ////////  convert LDT to ZDT with user's default date zone
            ZonedDateTime startZonedDT = startLocalDT.atZone(ZoneId.systemDefault());
            ZonedDateTime endZonedDT = endLocalDT.atZone(ZoneId.systemDefault());
            ////////  convert to EST
            ZonedDateTime startEstZonedDT = startZonedDT.withZoneSameInstant(ZoneId.of("US/Eastern"));
            ZonedDateTime endEstZonedDT = endZonedDT.withZoneSameInstant(ZoneId.of("US/Eastern"));
            ////////  extract LocalTime(EST) for comparison
            LocalTime startEst = startEstZonedDT.toLocalTime();
            LocalTime endEst = endEstZonedDT.toLocalTime();
            ////////  set business hours start and end times
            LocalTime businessStartEst = LocalTime.of(8,0,0);
            LocalTime businessEndEst = LocalTime.of(22,0,0);
            ////////  Duration of appointment and total business hours (allowed) per day
            LocalDate startDate = startZonedDT.toLocalDate();
            LocalDate endDate = endZonedDT.toLocalDate();

            //// check for incorrect start dateTime
            if (startLocalDT.isAfter(endLocalDT))
                throw new InvalidInputException("Appointment start time must be BEFORE end time!");

            //// check for outside of business hours
            if (!startDate.isEqual(endDate)) {      // appointment spans several days
                throw new InvalidInputException("Appointment cannot be longer than one full business day!\n"
                        + "Must be between business hours (8:00am - 10:00pm EST)");
            }
            if (startEst.isBefore(businessStartEst)
                    || startEst.isAfter(businessEndEst)
                    || startEst.equals(businessEndEst)
                    || endEst.isAfter(businessEndEst)
                    || endEst.isBefore(businessStartEst))
                throw new InvalidInputException("Appointment must be between business hours\n (8:00am - 10:00pm EST)");

            //// check for customer overlapping appointments
            {
                LocalDateTime start = startLocalDT;
                LocalDateTime end = endLocalDT;
                //// check for overlapping appointments
                for (Appointment a : overlappingAppointmentList) {
                    if (start.isEqual(a.start()) || end.isEqual(a.end())  // edge cases
                            // when start is in the window
                            || (start.isAfter(a.start()) && start.isBefore(a.end()))
                            // when end is in the window
                            || (end.isAfter(a.start()) && end.isBefore(a.end()))
                            // when start AND end are both outside of the window
                            || (start.isBefore(a.start()) && end.isAfter(a.end()))
                    ) {
                        throw new InvalidInputException("Proposed Appointment Time conflicts with an existing appointment!");
                    }
                }
            }


            // create Appointment to save:
            savedAppointment = new Appointment(id, title, description, location, type,
                    startLocalDT, endLocalDT,
                    customerId, "", inputUserId, contactId, "", "");

            // update database with Appointment (add or modify):
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
            GuiUtil.handleLogicalError(e);
        } catch (SQLException e) {
            System.out.println("Database Error! Check connection and SQL");
            e.printStackTrace();
        }

    }

}
