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
import javafx.scene.control.*;

public class EditAppointmentController {

    @FXML
    private Label currentOperationLabel;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button saveAppointmentBtn;

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
