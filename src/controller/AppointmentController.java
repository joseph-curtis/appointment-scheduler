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

public class AppointmentController {
    /**
     * The Appointment in the database to modify
     */
    Appointment existingAppointment;

    /**
     * Sets all properties for edited item to populate to corresponding text fields.
     * <p>The existing Appointment in DB is passed when changing the scene</p>
     * @see utility.GuiUtil#changeStagePassObj(ActionEvent, DataTransferObject, String, String, Modality)
     * @param passedObject existing appointment to be edited
     */
    public void passExistingAppointment(DataTransferObject passedObject) {
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

    /**
     * Get existing ID or new unique ID if Appointment is new
     * @see DAO.AppointmentDaoImpl#getUniqueId()
     * @return a unique appointment ID
     */
    protected int acquireId() {
        if (existingAppointment != null) {
            return existingAppointment.id();          // get ID of existing part to edit
        } else {
            return AppointmentDaoImpl.getUniqueId();  // get new ID for new part
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
