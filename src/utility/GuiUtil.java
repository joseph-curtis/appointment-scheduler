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

package utility;

import DAO.AppointmentDaoImpl;
import controller.AuthenticatedController;
import controller.PrimaryController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.DataTransferObject;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * Contains helper functions for changing scenes and displaying dialog boxes to user.
 * <p>Use for easier maintenance of code
 * instead of code duplication</p>
 * @author Joseph Curtis
 * @version 2022.06.30
 */
public final class GuiUtil {
    public static Locale locale = Locale.getDefault();
    public static ResourceBundle languageRb = ResourceBundle.getBundle("Localization", locale);

    /**
     * Opens a new window for add appointments or customers
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @param user the currently logged-in user
     * @param fxmlFileName the .fxml file holding the next scene
     * @param windowTitle the new window title to set
     * @param modality mode for new window (NONE=unlocked, WINDOW_MODAL=locked to new window)
     * @throws IOException if .fxml filename cannot be found
     */
    public static void newStage(ActionEvent event,
                                DataTransferObject user,
                                String fxmlFileName,
                                String windowTitle,
                                Modality modality) throws IOException {
        newStage(event, null, user, fxmlFileName, windowTitle, modality,
                ResourceBundle.getBundle("Localization", Locale.getDefault()));
    }

    /**
     * Opens a new stage (window). This is used to get the primary window.
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @param user the currently logged-in user
     * @param fxmlFileName the .fxml file holding the next scene
     * @param windowTitle the new window title to set
     * @param modality mode for new window (NONE=unlocked, WINDOW_MODAL=locked to new window)
     * @param resources the resource bundle (like language pack) that goes with the stage
     * @throws IOException if .fxml filename cannot be found
     */
    public static void newStage(ActionEvent event,
                                DataTransferObject user,
                                String fxmlFileName,
                                String windowTitle,
                                Modality modality,
                                ResourceBundle resources) throws IOException {

        newStage(event, null, user, fxmlFileName, windowTitle, modality, resources);
    }

    /**
     * Opens a new window for modify appointments or customers
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @param passedObject the existing database object to modify, or null
     * @param user the currently logged-in user
     * @param fxmlFileName the .fxml file holding the next scene
     * @param windowTitle the new window title to set
     * @param modality mode for new window (NONE=unlocked, WINDOW_MODAL=locked to new window)
     * @throws IOException if .fxml filename cannot be found
     */
    public static void newStage(ActionEvent event,
                                DataTransferObject passedObject,
                                DataTransferObject user,
                                String fxmlFileName,
                                String windowTitle,
                                Modality modality) throws IOException {
        newStage(event, passedObject, user, fxmlFileName, windowTitle, modality,
                ResourceBundle.getBundle("Localization", Locale.getDefault()));
    }

    /**
     * Opens a new stage (window).
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @param passedObject the existing database object to modify, or null
     * @param user the currently logged-in user
     * @param fxmlFileName the .fxml file holding the next scene
     * @param windowTitle the new window title to set
     * @param modality mode for new window (NONE=unlocked, WINDOW_MODAL=locked to new window)
     * @param resources the resource bundle (like language pack) that goes with the stage
     * @throws IOException if .fxml filename cannot be found
     */
    public static void newStage(ActionEvent event,
                                DataTransferObject passedObject,
                                DataTransferObject user,
                                String fxmlFileName,
                                String windowTitle,
                                Modality modality,
                                ResourceBundle resources) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(GuiUtil.class.getResource(fxmlFileName));
        loader.setResources(resources);
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner( ((Node)event.getSource()).getScene().getWindow() );

        if(modality.equals(Modality.NONE))
            ((Node)(event.getSource())).getScene().getWindow().hide();  // close parent window

        AuthenticatedController controller = loader.getController();
        controller.passCurrentUser(user);
        controller.passExistingRecord(passedObject);

        if (controller instanceof PrimaryController) {
            // initialize Appointments and Customers table
            ((PrimaryController) controller).setAppointmentsTable();
            ((PrimaryController) controller).setCustomersTable();
            // display upcoming appointment dialog(s)
            stage.setOnShown((windowEvent) -> showAppointmentAlert((User) user));
        }

        stage.showAndWait();
    }

    /**
     * Quits the application.
     * <p>Displays a confirmation dialog before exiting.</p>
     */
    public static void confirmExitApplication() {
        ButtonType okButton = new ButtonType(languageRb.getString("okButton"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(languageRb.getString("cancelButton"), ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION,
                languageRb.getString("confirmExit.content"),
                okButton, cancelButton);
        confirmExit.setTitle(languageRb.getString("confirmExit.title"));
        confirmExit.setHeaderText(languageRb.getString("confirmExit.header"));
        // set css theme
        confirmExit.getDialogPane().getStylesheets().add(
                GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());

        // Fully lambda approach to showing confirmation dialog:
        confirmExit.showAndWait()
                .filter((ButtonType response) -> response == okButton)
                .ifPresent((ButtonType response) -> System.exit(0));
    }

    /**
     * Show the change language settings dialog. This allows the user to override the system default language.
     * @throws IOException if a Localization properties file cannot be found
     * @return true if language selection was made, false if cancel button was clicked.
     */
    public static boolean showSettings() throws IOException {
        ChoiceDialog<String> changeLangDialog = new ChoiceDialog<>(
                languageRb.getString("lang.choiceLabel"),
                languageRb.getString("lang.en"),
                languageRb.getString("lang.fr"),
                languageRb.getString("lang.de"),
                languageRb.getString("lang.jp")
        );
        changeLangDialog.setTitle(languageRb.getString("changeLangDialog.title"));
        changeLangDialog.setHeaderText(languageRb.getString("changeLangDialog.header"));
        changeLangDialog.setContentText(languageRb.getString("changeLangDialog.content"));
        ((Button) changeLangDialog.getDialogPane().lookupButton(ButtonType.OK)).setText(languageRb.getString("okButton"));
        ((Button) changeLangDialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(languageRb.getString("cancelButton"));
        // set css theme
        changeLangDialog.getDialogPane().getStylesheets().add(
                GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());

        Optional<String> result = changeLangDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get().equals(languageRb.getString("lang.en"))) {
                locale = new Locale("en");
            } else if (result.get().equals(languageRb.getString("lang.fr"))) {
                locale = new Locale("fr");
            } else if (result.get().equals(languageRb.getString("lang.de"))) {
                locale = new Locale("de");
            } else if (result.get().equals(languageRb.getString("lang.jp"))) {
                locale = new Locale("jp");
            } else {
                locale = Locale.getDefault();
            }
            languageRb = ResourceBundle.getBundle("Localization", locale);
            return true;
        }
        return false;
    }

    /**
     * Show the About this Application dialog box
     */
    public static void showAbout() {
        Alert aboutDialog = new Alert(Alert.AlertType.NONE,
                languageRb.getString("aboutDialog.content"),
                new ButtonType(languageRb.getString("closeButton"), ButtonBar.ButtonData.CANCEL_CLOSE));
        aboutDialog.setTitle(languageRb.getString("aboutDialog.title"));
        aboutDialog.setHeaderText(languageRb.getString("aboutDialog.header"));
        // add a graphic to dialog box:
        Image image = new Image(Objects.requireNonNull(GuiUtil.class.getResource("/images/ACME_Catalog.png")).toExternalForm());
        ImageView imageView = new ImageView(image);
        aboutDialog.setGraphic(imageView);
        // set size to preferred height for content to show fully
        aboutDialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        // set css theme
        aboutDialog.getDialogPane().getStylesheets().add(
                GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());

        aboutDialog.showAndWait();
    }

    /**
     * Displays a confirmation dialog for deleting Appointments or Customers.
     * <p>When the user confirms by clicking "OK", the lambda function will execute.
     * If the lambda returns false, this indicates the delete operation was not successful.
     * In this case an error dialog is shown.</p>
     * @param title Dialog box window title
     * @param header Dialog box header text
     * @param content details within confirmation dialog box
     * @param lambda the operation to execute when the user clicks "OK"
     * @return whether the deletion was successful or not (dependent on return state of lambda)
     */
    public static boolean confirmDeletion(String title,
                                       String header,
                                       String content,
                                       BooleanSupplier lambda) {

        Alert confirmRemove = new Alert(Alert.AlertType.CONFIRMATION);
        confirmRemove.setTitle(title);
        confirmRemove.setHeaderText(header);
        confirmRemove.setContentText(content);
        // set css theme
        confirmRemove.getDialogPane().getStylesheets().add(
                GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());

        Optional<ButtonType> result = confirmRemove.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // user confirmed delete operation
            boolean success = lambda.getAsBoolean();

            if (!success) {
                Alert deletionError = new Alert(Alert.AlertType.ERROR);
                deletionError.setHeaderText("Database Error");
                deletionError.setContentText("Unable to delete selected item!");
                // set css theme
                deletionError.getDialogPane().getStylesheets().add(
                        GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());

                deletionError.showAndWait();
                return false;   // an error occurred when trying to delete
            }
            return true;    // delete successful!
        }
        return false;   // user canceled delete operation
    }

    /**
     * Displays a dialog for each upcoming appointment.
     * <p>If the user has no appointments within 15 minutes, dialog shows no upcoming appointments.</p>
     */
    public static void showAppointmentAlert(User user) {
        AppointmentDaoImpl appointmentsDb = new AppointmentDaoImpl();
        try {
            LocalDateTime now = LocalDateTime.now();

            ObservableList<Appointment> upcomingAppointments = appointmentsDb.getAll(user.id())
                    // Using a Stream to filter out only appointments that are upcoming
                    .stream().filter((Appointment appointment) ->
                            appointment.start().isAfter(now.minusMinutes(1))
                                    && (appointment.start().isBefore(now.plusMinutes(15))
                                    || appointment.start().isEqual(now.plusMinutes(15))))
                    // turn stream back into collection
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            if (upcomingAppointments.isEmpty()) {
                // show "no upcoming appointments" dialog
                Alert scheduleClearDialog = new Alert(Alert.AlertType.INFORMATION);
                scheduleClearDialog.setHeaderText("No upcoming appointments");
                scheduleClearDialog.setContentText("Greetings " + user.name()
                        + "!\nYou have no appointments within the next 15 minutes.");
                scheduleClearDialog.getDialogPane().getStylesheets().add(
                        GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());

                scheduleClearDialog.showAndWait();

            } else {
                for (Appointment appointment: upcomingAppointments) {
                    int minutesAway = (int) Duration.between(now, appointment.start()).toMinutes();
                    // Display upcoming appointment alert
                    Alert appointmentAlert = new Alert(Alert.AlertType.WARNING);
                    appointmentAlert.getDialogPane().getStylesheets().add(
                            GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());
                    appointmentAlert.setHeaderText("You have an upcoming appointment in "
                            + minutesAway + " minutes!");
                    appointmentAlert.setContentText("ID: " + appointment.id()
                            + "\nTitle: " + appointment.title()
                            + "\nDate: " + appointment.start().toLocalDate()
                            + "\nStart Time: " + appointment.start().toLocalTime()
                            + "\nCustomer: " + appointment.customerName()
                            + "\nContact: " + appointment.contactName());

                    appointmentAlert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gives user an Error dialog box to warn of a serious problem with the database object.
     * This should appear when there is concurrent activity writing to the database.
     * @param exception thrown exception indicating the problem
     */
    public static void handleDataObjNotFoundException(DataObjNotFoundException exception) {
        Alert dbError = new Alert(Alert.AlertType.ERROR);
        dbError.setHeaderText("Database Error");
        dbError.setContentText(exception.getMessage());
        // set css theme
        dbError.getDialogPane().getStylesheets().add(
                GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());

        dbError.showAndWait();
    }

    /**
     * Pops up dialog box warning user of black input fields.
     * @param exception indicator of a blank field that needs user input
     */
    public static void handleBlankInputException(BlankInputException exception) {
        Alert blankTextInfo = new Alert(Alert.AlertType.INFORMATION);
        blankTextInfo.setHeaderText(exception.getMessage());
        blankTextInfo.setContentText("Please enter data in each field.");
        // set css theme
        blankTextInfo.getDialogPane().getStylesheets().add(
                GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());

        blankTextInfo.showAndWait();
    }

    /**
     * Displays dialog box in response to a logical error (input validation).
     * @param content details given to user about the logical error
     * @throws InvalidInputException for controller to handle by halting its operation
     */
    public static void handleLogicalError(String content) throws InvalidInputException {
        Alert inputWarning = new Alert(Alert.AlertType.WARNING);
        inputWarning.setHeaderText("Input Validation Failed");
        inputWarning.setContentText(content);
        // set css theme
        inputWarning.getDialogPane().getStylesheets().add(
                GuiUtil.class.getResource("/view/modena-red.css").toExternalForm());

        inputWarning.showAndWait();

        throw new InvalidInputException("Logical error check:\n" + content);
    }

}
