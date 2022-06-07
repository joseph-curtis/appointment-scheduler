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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import model.Appointment;
import model.DataTransferObject;
import model.User;
import utility.BlankInputException;
import utility.DataObjNotFoundException;
import utility.GuiUtil;
import utility.InvalidInputException;

import java.sql.SQLException;

/**
 * Controller for the add or modify Appointment form.
 * @author Joseph Curtis
 * @version 2022.06.06
 */
public class AppointmentController implements AuthenticatedController {

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

        idTxt.setText(String.valueOf(passedObject.id()));
        titleTxt.setText(((Appointment) passedObject).title());
        descriptionTxt.setText(((Appointment) passedObject).description());
        locationTxt.setText(((Appointment) passedObject).location());
        typeTxt.setText(((Appointment) passedObject).type());



        // TODO:  set start/end times
        // TODO:  set customer_ID combo box
        // TODO:  set Contact_ID combo box
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
    private Spinner<?> startHourSpinner;

    @FXML
    private Spinner<?> startMinuteSpinner;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Spinner<?> endHourSpinner;

    @FXML
    private Spinner<?> endMinuteSpinner;

    @FXML
    private ComboBox<?> contactIdComboBox;

    @FXML
    private ComboBox<?> customerIdComboBox;

    @FXML
    void onActionCancel(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    void onActionSaveAppointment(ActionEvent event) {
        Appointment savedAppointment;

        try {
            if (titleTxt.getText().isBlank()
                    || descriptionTxt.getText().isBlank()
                    || locationTxt.getText().isBlank()
                    || typeTxt.getText().isBlank()
//                    || customerIdComboBox.isBlank()
//                    || contactIdComboBox.isBlank()
                // TODO check for blank combobox selection
               //  TODO check for start & end date & time selection
            )
                throw new BlankInputException("Fields Cannot be Blank");

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

            // TODO get combo box to display customer and contact ids
            //  TODO  also get the ID ....
            int customerId = 1;
            int contactId = 1;

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

            // TODO:: parse start and end date and times

            savedAppointment = new Appointment(id, title, description, location, type,
                    null, null,
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

    @FXML
    void onActionListContacts(ActionEvent event) {

        // TODO : implement on click method
    }

    @FXML
    void onActionListCustomers(ActionEvent event) {

        // TODO : implement on click method
    }
}
