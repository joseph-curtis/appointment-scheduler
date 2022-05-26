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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import model.Appointment;
import model.DataTransferObject;
import model.User;

/**
 * Controller for the add or modify Appointment form.
 * @author Joseph Curtis
 * @version 2022.05.25
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

    }

    @FXML
    void onActionListContacts(ActionEvent event) {

    }

    @FXML
    void onActionListCustomers(ActionEvent event) {

    }
}
