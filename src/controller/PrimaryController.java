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
import model.Appointment;
import model.Customer;

import java.util.Optional;

public class PrimaryController {

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private TableView<Appointment> appointmentsTable;

    @FXML
    private TableColumn<Appointment, Integer> appointment_id_col;

    @FXML
    private TableColumn<Appointment, String> title_col;

    @FXML
    private TableColumn<Appointment, String> description_col;

    @FXML
    private TableColumn<Appointment, String> location_col;

    @FXML
    private TableColumn<Appointment, String> contact_name_col;

    @FXML
    private TableColumn<Appointment, String> contact_email_col;

    @FXML
    private TableColumn<Appointment, String> type_col;

    @FXML
    private TableColumn<Appointment, ?> start_datetime_col;

    @FXML
    private TableColumn<Appointment, ?> end_datetime_col;

    @FXML
    private TableColumn<Appointment, Integer> appointment_cust_id_col;

    @FXML
    private TableColumn<Appointment, Integer> user_id_col;

    @FXML
    private RadioButton radioViewAll;

    @FXML
    private RadioButton radioViewMonth;

    @FXML
    private RadioButton radioViewWeek;

    @FXML
    private Button addAppointmentBtn;

    @FXML
    private Button updateAppointmentBtn;

    @FXML
    private Button deleteAppointmentBtn;

    @FXML
    private Label appDeleteConfirmLabel;

    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<Customer, Integer> customer_id_col;

    @FXML
    private TableColumn<Customer, String> name_col;

    @FXML
    private TableColumn<Customer, String> address_col;

    @FXML
    private TableColumn<Customer, String> postalcode_col;

    @FXML
    private TableColumn<Customer, String> phone_col;

    @FXML
    private TableColumn<Customer, String> division_col;

    @FXML
    private TableColumn<Customer, String> country_col;

    @FXML
    private Button addCustomerBtn;

    @FXML
    private Button updateCustomerBtn;

    @FXML
    private Button deleteCustomerBtn;

    @FXML
    private Label custDeleteConfirmLabel;

    /**
     * Quits the application.
     * <p>Displays a confirmation dialog before exiting.</p>
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionExitApplication(ActionEvent event) {
        Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION);
        confirmExit.setTitle("Confirm Exit");
        confirmExit.setHeaderText("Exit Application?");
        confirmExit.setContentText("Are you sure you want to quit?");

        Optional<ButtonType> result = confirmExit.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
            System.exit(0);
    }

    @FXML
    void onActionOpenSettings(ActionEvent event) {

    }

    @FXML
    void onActionShowAbout(ActionEvent event) {

    }

    @FXML
    void onAppointmentsTabChanged(ActionEvent event) {

    }

    @FXML
    void onCustomersTabChanged(ActionEvent event) {

    }

    @FXML
    void onActionViewAll(ActionEvent event) {

    }

    @FXML
    void onActionViewByMonth(ActionEvent event) {

    }

    @FXML
    void onActionViewByWeek(ActionEvent event) {

    }

    @FXML
    void onActionAddAppointment(ActionEvent event) {

    }

    @FXML
    void onActionUpdateAppointment(ActionEvent event) {

    }

    @FXML
    void onActionDeleteAppointment(ActionEvent event) {

    }

    @FXML
    void onActionAddCustomer(ActionEvent event) {

    }

    @FXML
    void onActionUpdateCustomer(ActionEvent event) {

    }

    @FXML
    void onActionDeleteCustomer(ActionEvent event) {

    }

}
