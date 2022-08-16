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
import DAO.ContactDaoImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Appointment;
import model.Contact;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the Contacts Schedule reports screen.
 * @author Joseph Curtis
 * @version 2022.08.16
 */
public class ScheduleReportController implements Initializable {

    ObservableList<Appointment> allAppointments;
    ObservableList<Contact> allContacts;
    ObservableList<Appointment> filteredAppointments;

    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, Integer> appointment_id_col;
    @FXML private TableColumn<Appointment, String> title_col;
    @FXML private TableColumn<Appointment, String> type_col;
    @FXML private TableColumn<Appointment, String> description_col;
    @FXML private TableColumn<Appointment, ?> start_datetime_col;
    @FXML private TableColumn<Appointment, ?> end_datetime_col;
    @FXML private TableColumn<Appointment, Integer> appointment_cust_id_col;
    @FXML private TableColumn<Appointment, String> appointment_cust_name_col;
    @FXML private ComboBox<Contact> contactComboBox;

    /**
     * Initializes the controller class, setting the combo-box properties
     * and getting the list of all contacts from the database (filtering is done later in java method)
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // save all appointments as list
        try {
            AppointmentDaoImpl appointmentsDb = new AppointmentDaoImpl();
            allAppointments = appointmentsDb.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // save all contacts as list
        ContactDaoImpl contactsDb = new ContactDaoImpl();
        allContacts = contactsDb.getAll();
        // set type combo box
        for (Contact contact : allContacts) {
            contactComboBox.getItems().add(contact);
        }
    }

    /**
     * Closes the report.
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionClose(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    /**
     * Changes the view to display the schedule for the selected contact.
     * @param event the user generated event (a combo-box being selected) that caused this to execute
     */
    @FXML
    void onActionShowSchedule(ActionEvent event) {
        filteredAppointments = allAppointments.stream()
                .filter((Appointment appointment) -> appointment.contactId().equals(contactComboBox.getValue().id()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        appointmentsTable.setItems(filteredAppointments);

        appointment_id_col.setCellValueFactory(a -> new SimpleIntegerProperty(a.getValue().id()).asObject());
        title_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().title()));
        description_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().description()));
        type_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().type()));
        start_datetime_col.setCellValueFactory(new PropertyValueFactory<>("start"));
        end_datetime_col.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointment_cust_id_col.setCellValueFactory(a -> new SimpleIntegerProperty(a.getValue().customerId()).asObject());
        appointment_cust_name_col.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().customerName()));
    }

}
