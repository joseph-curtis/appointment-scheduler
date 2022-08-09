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
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.Month;
import java.util.ResourceBundle;

/**
 * Controller for the Appointment-totals Report.
 * @author Joseph Curtis
 * @version 2022.08.08
 */
public class TotalsReportController implements Initializable {



    @FXML
    private Label currentOperationLabel;
    @FXML
    private ComboBox<Month> monthComboBox;
    @FXML
    private TextField totalTxtField;
    @FXML
    private ComboBox<?> typeComboBox;

    /**
     * Initializes the controller class, setting the combo-box properties
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Month[] months = Month.values();

        for (int x = 0; x < 12; x++) {
            monthComboBox.getItems().add(months[x]);
        }
    }

    @FXML
    void onActionListAppointmentTypes(ActionEvent event) {

    }

    @FXML
    void onActionOk(ActionEvent event) {

    }

    @FXML
    void onActionShowTotal(ActionEvent event) {

    }
}
