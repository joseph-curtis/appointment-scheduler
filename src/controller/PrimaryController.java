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
import javafx.scene.image.Image;
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
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.BooleanSupplier;

/**
 * Controller for the main menu.
 * @author Joseph Curtis
 * @version 2022.08.08
 */
public class PrimaryController implements Initializable, AuthenticatedController {

    User user;      // The currently logged-in user

    @FXML private MenuBar topMenuBar;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, Integer> appointment_id_col;
    @FXML private TableColumn<Appointment, String> title_col;
    @FXML private TableColumn<Appointment, String> description_col;
    @FXML private TableColumn<Appointment, String> location_col;
    @FXML private TableColumn<Appointment, String> contact_name_col;
    @FXML private TableColumn<Appointment, String> contact_email_col;
    @FXML private TableColumn<Appointment, String> type_col;
    @FXML private TableColumn<Appointment, ?> start_datetime_col;
    @FXML private TableColumn<Appointment, ?> end_datetime_col;
    @FXML private TableColumn<Appointment, Integer> appointment_cust_id_col;
    @FXML private TableColumn<Appointment, String> appointment_cust_name_col;
    @FXML private TableColumn<Appointment, Integer> user_id_col;
    @FXML private RadioButton radioViewAll;
    @FXML private RadioButton radioViewMonth;
    @FXML private RadioButton radioViewWeek;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> customer_id_col;
    @FXML private TableColumn<Customer, String> name_col;
    @FXML private TableColumn<Customer, String> address_col;
    @FXML private TableColumn<Customer, String> postalcode_col;
    @FXML private TableColumn<Customer, String> phone_col;
    @FXML private TableColumn<Customer, String> division_col;
    @FXML private TableColumn<Customer, String> country_col;
    @FXML private Label appointmentCanceledLabel;
    @FXML private Label customerDeletedLabel;
    @FXML private TabPane userOperationTabPane;
    @FXML private Tab appointmentsTab;
    @FXML private Tab customersTab;

    /**
     * {@inheritDoc}
     */
    @Override
    public void passCurrentUser(DataTransferObject user) {
        this.user = (User) user;
    }

    /**
     * {@inheritDoc}
     * <p><em>This controller has no object to pass in. As such, this method does nothing.</em></p>
     */
    @Override
    public void passExistingRecord(DataTransferObject passedObject) {
        // No object to pass in here.
    }

    /**
     * Initializes the controller class, formatting the TableView columns.
     * <p>This uses a lambda in order to add a listener to an FXML object.</p>
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set listener for tab selection change:
        userOperationTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == appointmentsTab)
                appointmentCanceledLabel.setText("");
            if(newTab == customersTab)
                customerDeletedLabel.setText("");
        });
    }

    /**
     * Quits the application.
     * <p>Displays a confirmation dialog before exiting.</p>
     * @param event the user generated event (a menu item being clicked) that caused this to execute
     */
    @FXML
    void onActionExit(ActionEvent event) {
        GuiUtil.confirmExitApplication();
    }

    /**
     * Show the change language settings dialog. This allows the user to override the system default language.
     * @param event the user generated event (a menu item being clicked) that caused this to execute
     * @throws IOException if a Localization properties file cannot be found
     */
    @FXML
    void onActionSettings(ActionEvent event) throws IOException {
        if (GuiUtil.showSettings()) {
            // reload stage: //
            Parent root = FXMLLoader.load(getClass().getResource("/view/primary-view.fxml"), GuiUtil.languageRb);
            Stage primaryStage = (Stage)(topMenuBar.getScene().getWindow());
            primaryStage.setTitle(GuiUtil.languageRb.getString("primaryStage.title"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    }

    /**
     * Show the About this Application dialog box
     * @param event the user generated event (a menu item being clicked) that caused this to execute
     */
    @FXML
    void onActionAbout(ActionEvent event) {
        GuiUtil.showAbout();
    }

    /**
     * Changes the Appointment table to show all appointments
     * @param event the user generated event (a radio button being clicked) that caused this to execute
     */
    @FXML
    void onActionViewAll(ActionEvent event) {
        setAppointmentsTable();
    }

    /**
     * Changes the Appointment table to show appointments for the following week
     * @param event the user generated event (a radio button being clicked) that caused this to execute
     */
    @FXML
    void onActionViewByMonth(ActionEvent event) {
        setAppointmentsTable();
    }

    /**
     * Changes the Appointment table to show appointments for the following month
     * @param event the user generated event (a radio button being clicked) that caused this to execute
     */
    @FXML
    void onActionViewByWeek(ActionEvent event) {
        setAppointmentsTable();
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
        // refresh the tableview to reflect possible changes
        setAppointmentsTable();
        appointmentCanceledLabel.setText("");      // clear any previous deletion notification
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
                    "Add Customer",
                    Modality.WINDOW_MODAL);
        } catch (IOException e) {
            System.out.println("Error finding file: " + fxmlFile);
            e.printStackTrace();
        }
        // refresh the tableview to reflect possible changes
        setCustomersTable();
        customerDeletedLabel.setText("");      // clear any previous deletion notification
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
        setAppointmentsTable();
        appointmentCanceledLabel.setText("");      // clear any previous deletion notification
    }

    /**
     * Opens the "Edit Customer" window.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionUpdateCustomer(ActionEvent event) {
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
        setCustomersTable();
        customerDeletedLabel.setText("");      // clear any previous deletion notification
    }

    /**
     * Removes selected Appointment from database.
     * <p>This uses a lambda to specify what happens after a user clicks "OK" to confirm the deletion.
     * By using a lambda here, we can re-use the method <code>GuiUtil.confirmDeletion</code> to specify
     * other use cases where confirming deleting some other kind of object has a different effect
     * (different code to be run in response).</p>
     * <p>For example, here we declare a different lambda depending on if it is a Customer or an Appointment that
     * the user is confirming the deletion of. Both supply a boolean to declare if the deletion was successful
     * or not.</p>
     * <p>Check under "See Also:" below for the method where the lambda is declared.</p>
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @see utility.GuiUtil#confirmDeletion(String, String, String, BooleanSupplier) utility.GuiUtil #confirmDeletion(String, String, String, BooleanSupplier)
     */
    @FXML
    void onActionDeleteAppointment(ActionEvent event) {
        Appointment deletedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (deletedAppointment == null)
            return;     // no selection means nothing to delete or confirm

        AppointmentDaoImpl dbAppointments = new AppointmentDaoImpl();
        if (GuiUtil.confirmDeletion(
                "Delete Appointment Confirmation",
                "Delete Selected Appointment (" + deletedAppointment.title() + ") ?" ,
                "Appointment ID: " + deletedAppointment.id() +
                        "\nType: " + deletedAppointment.type() +
                        "\nAppointment will be deleted.  This CANNOT be undone!" ,
                ()-> {
                    try {
                        return dbAppointments.delete(deletedAppointment.id());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
        )) {
            appointmentCanceledLabel.setTextFill(Paint.valueOf("RED"));
            appointmentCanceledLabel.setText("Appointment (" + deletedAppointment.title() + ") ID: "
                    + deletedAppointment.id() + ", type: " + deletedAppointment.type() + " -- CANCELED.");
        } else {
            appointmentCanceledLabel.setTextFill(Paint.valueOf("BLACK"));
            appointmentCanceledLabel.setText("interrupted delete appointment.");
        }
        // refresh the tableview to reflect possible changes
        setAppointmentsTable();
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
                    customerDeletedLabel.setTextFill(Paint.valueOf("RED"));
                    customerDeletedLabel.setText("Customer: \"" + deletedCustomer.name() + "\" was deleted.");
                } else {
                    customerDeletedLabel.setTextFill(Paint.valueOf("BLACK"));
                    customerDeletedLabel.setText("canceled delete customer.");
                }
            }
            else {
                // customer with associated appointments cannot be deleted
                Alert warningDelete = new Alert(Alert.AlertType.WARNING);
                warningDelete.setHeaderText("Unable to Delete \"" + deletedCustomer.name() + "\"");
                warningDelete.setContentText("This Customer has " + appointmentsList.size()
                        + " associated Appointments.\nPlease remove all associated Appointments first.");
                // set css theme
                warningDelete.getDialogPane().getStylesheets().add(
                        getClass().getResource("/view/modena-red.css").toExternalForm());
                // add window icon:
                Stage stage = (Stage) warningDelete.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("/images/wile-e.png"));
                warningDelete.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // refresh the tableview to reflect possible changes
        setCustomersTable();
    }

    /**
     * Display the report menu showing a schedule for each contact in the organization
     * @param event the user generated event (a menu item being clicked) that caused this to execute
     */
    @FXML
    void onActionReportContactSchedule(ActionEvent event) {
        String fxmlFile = "/view/reportContactSchedule-view.fxml";
        try {
            GuiUtil.showReport(fxmlFile,
                    "REPORT: Contact's Schedule");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display the report menu showing the total number of customer appointments by type and month
     * @param event the user generated event (a menu item being clicked) that caused this to execute
     */
    @FXML
    void onActionReportTotals(ActionEvent event) {
        String fxmlFile = "/view/reportAppointmentTotals-view.fxml";
        try {
            GuiUtil.showReport(fxmlFile,
                    "REPORT: appointment type totals by month");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * -----------
     * @param event the user generated event (a menu item being clicked) that caused this to execute
     */
    @FXML
    void onActionCustomReport(ActionEvent event) {

    }

    /**
     * Initializes or updates the Appointments table.
     * <p>This uses Lambdas in order to make use of Java Records classes
     * when setting table cell-value factories.</p>
     */
    public void setAppointmentsTable() {
        AppointmentDaoImpl appointmentsDb = new AppointmentDaoImpl();
        try {
            if (radioViewMonth.isSelected()) {

                appointmentsTable.setItems(appointmentsDb.getAllBetweenDates(
                        LocalDate.now(), LocalDate.now().plusMonths(1)
                ));
            }
            else if (radioViewWeek.isSelected()) {
                appointmentsTable.setItems(appointmentsDb.getAllBetweenDates(
                        LocalDate.now(), LocalDate.now().plusWeeks(1)
                ));
            }
            else appointmentsTable.setItems(appointmentsDb.getAll());

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

    /**
     * Initializes or updates the Customers table.
     * <p>This uses Lambdas in order to make use of Java Records classes
     *      * when setting table cell-value factories.</p>
     */
    public void setCustomersTable() {
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
