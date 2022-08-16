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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import model.Appointment;

import java.net.URL;
import java.sql.SQLException;
import java.time.Month;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the Appointment-totals Report.
 * @author Joseph Curtis
 * @version 2022.08.15
 */
public class TotalsReportController implements Initializable {

    ObservableList<Appointment> allAppointments;
    HashSet<String> allTypes = new HashSet<>();

    @FXML private ComboBox<Month> monthComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField totalTxtField;

    /**
     * Initializes the controller class, setting the combo-box properties
     * and getting the list of all appointments from the database (will for type later in java method)
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set month combo box
        Month[] months = Month.values();
        for (int x = 0; x < 12; x++) {
            monthComboBox.getItems().add(months[x]);
        }
        // save all appointments as list
        try {
            AppointmentDaoImpl appointmentsDb = new AppointmentDaoImpl();
            allAppointments = appointmentsDb.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // get all unique appointment types
        for (Appointment appointment : allAppointments) {
            allTypes.add(appointment.type());
        }
        // set type combo box
        for (String type : allTypes) {
            typeComboBox.getItems().add(type);
        }
    }

    /**
     * Closes the report.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionOk(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    /**
     * Select the month to filter total. If both inputs are selected, display the total.
     * @param event the user generated event (a combo-box being selected) that caused this to execute
     */
    @FXML
    void onActionChangeMonth(ActionEvent event) {
        if(typeComboBox.getValue() != null)
            showTotal(monthComboBox.getValue(), typeComboBox.getValue());
    }

    /**
     * Select appointment type. If both inputs are selected, display the total.
     * @param event the user generated event (a combo-box being selected) that caused this to execute
     */
    @FXML
    void onActionChangeType(ActionEvent event) {
        if(monthComboBox.getValue() != null)
            showTotal(monthComboBox.getValue(), typeComboBox.getValue());
    }

    /**
     * Calculates total and displays it inside the total text box.
     * This utilizes a data stream to filter out the list of all appointments to only those
     * which match the criteria set by the user; namely by month and appointment type.
     * We must use a lambda to set the conditions for which each appointment item will be filtered out
     * or included in the filtered list.  We then convert back to ta collection so that we may easily count
     * the total filtered items by using <code>size()</code> method.
     * @param month month total filter
     * @param type type total filter
     */
    void showTotal(Month month, String type) {
    ObservableList<Appointment> filteredAppointments =
            allAppointments.stream().filter((Appointment appointment) ->
                appointment.type().equals(type) && appointment.start().getMonth().equals(month))
                    // turn stream back into collection
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

        totalTxtField.setText(String.valueOf((long) filteredAppointments.size()));
    }
}
