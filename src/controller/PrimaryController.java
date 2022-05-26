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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;
import model.Appointment;
import model.Customer;
import model.DataTransferObject;
import model.User;
import utility.GuiUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the main menu.
 * @author Joseph Curtis
 * @version 2022.05.25
 */
public class PrimaryController implements Initializable, AuthenticatedController {

    User user;      // The currently logged-in user

    /**
     * Authenticates user that is signed in.
     * @param user currently logged-in user
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
        // No object to pass in here.
    }

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
    private Label custDeleteConfirmLabel;

    @FXML
    private Tab appointmentsTab;

    @FXML
    private Tab customersTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

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
    void onActionViewAll(ActionEvent event) {

    }

    @FXML
    void onActionViewByMonth(ActionEvent event) {

    }

    @FXML
    void onActionViewByWeek(ActionEvent event) {

    }

    /**
     * Opens the "Add New Customer" window.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionAddAppointment(ActionEvent event) {
        String fxmlFile = "/view/editAppointment-view.fxml";
        try {
            GuiUtil.newStage(event,
                    user,
                    fxmlFile,
                    "Add Appointment",
                    Modality.WINDOW_MODAL);
        } catch (IOException e) {
            System.out.println("Error finding file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    /**
     * Opens the "Add New Customer" window.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionAddCustomer(ActionEvent event) {
        String fxmlFile = "/view/editCustomer-view.fxml";
        try {
            GuiUtil.newStage(event,
                    user,
                    fxmlFile,
                    "Add Appointment",
                    Modality.WINDOW_MODAL);
        } catch (IOException e) {
            System.out.println("Error finding file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    /**
     * Opens the "Edit Appointment" window.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionUpdateAppointment(ActionEvent event) {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null)
            return;     // if nothing is selected, do nothing


    }

    /**
     * Opens the "Edit Customer" window.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionUpdateCustomer(ActionEvent event) {
    }

    /**
     * Removes selected Appointment from database.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionDeleteAppointment(ActionEvent event) {
        Appointment deletedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (deletedAppointment == null)
            return;     // no selection means nothing to delete or confirm

        AppointmentDaoImpl dbAppointments = new AppointmentDaoImpl();
        GuiUtil.confirmDeletion(
                "Delete Customer Confirmation",
                "Delete Selected Customer \"" + deletedAppointment.title() + "\" ?" ,
                "Customer will be deleted.  This CANNOT be undone!" ,
                ()-> {
                    try {
                        return dbAppointments.delete(deletedAppointment.id());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
        );
    }

    /**
     * Removes selected Customer from database.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionDeleteCustomer(ActionEvent event) {
        Customer deletedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (deletedCustomer == null)
            return;     // no selection means nothing to delete or confirm

        CustomerDaoImpl dbCustomers = new CustomerDaoImpl();
        GuiUtil.confirmDeletion(
                "Delete Customer Confirmation",
                "Delete Selected Customer \"" + deletedCustomer.name() + "\" ?" ,
                "Customer will be deleted.  This CANNOT be undone!" ,
                ()-> {
                    try {
                        return dbCustomers.delete(deletedCustomer.id());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
        );
    }

}
