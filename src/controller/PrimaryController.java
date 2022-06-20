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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.DataTransferObject;
import model.User;
import utility.GuiUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for the main menu.
 * @author Joseph Curtis
 * @version 2022.06.19
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

    /**
     * Initializes the controller class, formatting the TableView columns
     * <p>This uses Lambdas in order to make use of Java Records classes.</p>
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAppointmentsTable();
        initCustomersTable();

        // set listener for tab selection change:
        userOperationTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == appointmentsTab)
                appDeleteConfirmLabel.setText("");
            if(newTab == customersTab)
                custDeleteConfirmLabel.setText("");
        });
    }

    @FXML
    private MenuBar topMenuBar;
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
    private TableColumn<Appointment, String> appointment_cust_name_col;
    @FXML
    private TableColumn<Appointment, Integer> user_id_col;
    @FXML
    private RadioButton radioViewAll;
    @FXML
    private RadioButton radioViewMonth;
    @FXML
    private RadioButton radioViewWeek;
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
    private Label appDeleteConfirmLabel;
    @FXML
    private Label custDeleteConfirmLabel;
    @FXML
    private TabPane userOperationTabPane;
    @FXML
    private Tab appointmentsTab;
    @FXML
    private Tab customersTab;

    /**
     * Quits the application.
     * <p>Displays a confirmation dialog before exiting.</p>
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionExit(ActionEvent event) {
        GuiUtil.confirmExitApplication();
    }

    @FXML
    void onActionSettings(ActionEvent event) throws IOException {
        if (GuiUtil.showSettings()) {
            // reload stage: //
            Parent root = FXMLLoader.load(getClass().getResource("/view/primary-view.fxml"), GuiUtil.languageRb);
            Stage stage = (Stage)(topMenuBar.getScene().getWindow());
            stage.setTitle(GuiUtil.languageRb.getString("primaryStage.title"));
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    @FXML
    void onActionAbout(ActionEvent event) {
        GuiUtil.showAbout();
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
        appDeleteConfirmLabel.setText("");      // clear any previous deletion notification
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
        // refresh the tableview to reflect possible changes
//        appointmentsTable.getColumns().get(0).setVisible(false);
//        appointmentsTable.getColumns().get(0).setVisible(true);
        initAppointmentsTable();
//        appointmentsTable.refresh();
    }

    /**
     * Opens the "Add New Customer" window.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionAddCustomer(ActionEvent event) {
        custDeleteConfirmLabel.setText("");      // clear any previous deletion notification
        String fxmlFile = "/view/editCustomer-view.fxml";
        try {
            GuiUtil.newStage(event,
                    user,
                    fxmlFile,
                    "Add Customer",
                    Modality.WINDOW_MODAL);
        } catch (IOException e) {
            System.out.println("Error finding file: " + fxmlFile);
            e.printStackTrace();
        }
        // refresh the tableview to reflect possible changes
//        customersTable.getColumns().get(0).setVisible(false);
//        customersTable.getColumns().get(0).setVisible(true);
        initCustomersTable();
//        customersTable.refresh();
    }

    /**
     * Opens the "Edit Appointment" window.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionUpdateAppointment(ActionEvent event) {
        appDeleteConfirmLabel.setText("");      // clear any previous deletion notification
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null)
            return;     // if nothing is selected, do nothing

        String fxmlFile = "/view/editAppointment-view.fxml";
        try {
            GuiUtil.newStage(event,
                    selectedAppointment,
                    user,
                    fxmlFile,
                    "Update Appointment",
                    Modality.WINDOW_MODAL);
        } catch (IOException e) {
            System.out.println("Error finding file: " + fxmlFile);
            e.printStackTrace();
        }
        // refresh the tableview to reflect possible changes
//        appointmentsTable.getColumns().get(0).setVisible(false);
//        appointmentsTable.getColumns().get(0).setVisible(true);
        initAppointmentsTable();
//        appointmentsTable.refresh();
    }

    /**
     * Opens the "Edit Customer" window.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionUpdateCustomer(ActionEvent event) {
        custDeleteConfirmLabel.setText("");      // clear any previous deletion notification
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null)
            return;     // if nothing is selected, do nothing

        String fxmlFile = "/view/editCustomer-view.fxml";
        try {
            GuiUtil.newStage(event,
                    selectedCustomer,
                    user,
                    fxmlFile,
                    "Update Customer",
                    Modality.WINDOW_MODAL);
        } catch (IOException e) {
            System.out.println("Error finding file: " + fxmlFile);
            e.printStackTrace();
        }
        // refresh the tableview to reflect possible changes
//        customersTable.getColumns().get(0).setVisible(false);
//        customersTable.getColumns().get(0).setVisible(true);
        initCustomersTable();
//        customersTable.refresh();
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
        if (GuiUtil.confirmDeletion(
                "Delete Appointment Confirmation",
                "Delete Selected Appointment \"" + deletedAppointment.title() + "\" ?" ,
                "Appointment will be deleted.  This CANNOT be undone!" ,
                ()-> {
                    try {
                        return dbAppointments.delete(deletedAppointment.id());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
        )) {
            appDeleteConfirmLabel.setTextFill(Paint.valueOf("RED"));
            appDeleteConfirmLabel.setText("Appointment: \"" + deletedAppointment.title() + "\" was deleted.");
        } else {
            appDeleteConfirmLabel.setTextFill(Paint.valueOf("BLACK"));
            appDeleteConfirmLabel.setText("Canceled delete appointment.");
        }
        // refresh the tableview to reflect possible changes
//        appointmentsTable.getColumns().get(0).setVisible(false);
//        appointmentsTable.getColumns().get(0).setVisible(true);
        initAppointmentsTable();
//        appointmentsTable.refresh();
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

        AppointmentDaoImpl dbAppointments = new AppointmentDaoImpl();
        try {
            ObservableList<Appointment> appointmentsList = dbAppointments.getAllByCustomerId(deletedCustomer.id());

            if (appointmentsList.isEmpty()) {
                CustomerDaoImpl dbCustomers = new CustomerDaoImpl();
                if (GuiUtil.confirmDeletion(
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
                )) {
                    custDeleteConfirmLabel.setTextFill(Paint.valueOf("RED"));
                    custDeleteConfirmLabel.setText("Customer: \"" + deletedCustomer.name() + "\" was deleted.");
                } else {
                    custDeleteConfirmLabel.setTextFill(Paint.valueOf("BLACK"));
                    custDeleteConfirmLabel.setText("Canceled delete customer.");
                }
            }
            else {
                // customer with associated appointments cannot be deleted
                Alert warningDelete = new Alert(Alert.AlertType.WARNING);
                // set css theme
                warningDelete.getDialogPane().getStylesheets().add(
                        getClass().getResource("/view/modena-red.css").toExternalForm());
                warningDelete.setHeaderText("Unable to Delete \"" + deletedCustomer.name() + "\"");
                warningDelete.setContentText("This Customer has " + appointmentsList.size()
                        + " associated Appointments.\nPlease remove all associated Appointments first.");
                warningDelete.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // refresh the tableview to reflect possible changes
//        customersTable.getColumns().get(0).setVisible(false);
//        customersTable.getColumns().get(0).setVisible(true);
        initCustomersTable();
//        customersTable.refresh();
    }

    private void initAppointmentsTable() {
        AppointmentDaoImpl appointmentsDb = new AppointmentDaoImpl();
        try {
            appointmentsTable.setItems(appointmentsDb.getAll());
            appointment_id_col.setCellValueFactory(a -> new SimpleIntegerProperty(a.getValue().id()).asObject());
            title_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().title()));
            description_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().description()));
            location_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().location()));
            type_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().type()));
            start_datetime_col.setCellValueFactory(new PropertyValueFactory<>("start"));
            end_datetime_col.setCellValueFactory(new PropertyValueFactory<>("end"));
            appointment_cust_id_col.setCellValueFactory(a -> new SimpleIntegerProperty(a.getValue().customerId()).asObject());
            appointment_cust_name_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().customerName()));
            contact_name_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().contactName()));
            contact_email_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().contactEmail()));
            user_id_col.setCellValueFactory(a -> new SimpleIntegerProperty(a.getValue().userId()).asObject());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initCustomersTable() {
        CustomerDaoImpl customersDb = new CustomerDaoImpl();
        try {
            customersTable.setItems(customersDb.getAll());
            customer_id_col.setCellValueFactory(a -> new SimpleIntegerProperty(a.getValue().id()).asObject());
            name_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().name()));
            address_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().address()));
            postalcode_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().postalCode()));
            phone_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().phone()));
            division_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().division()));
            country_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().country()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
