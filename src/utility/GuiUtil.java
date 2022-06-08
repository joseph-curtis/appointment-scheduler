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

import controller.AuthenticatedController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DataTransferObject;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * Contains helper functions for changing scenes and displaying dialog boxes to user.
 * <p>Use for easier maintenance of code
 * instead of code duplication</p>
 * @author Joseph Curtis
 * @version 2022.06.07
 */
public final class GuiUtil {
    /**
     * Opens a new stage (window)
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @param user the currently logged-in user
     * @param fxmlFileName the .fxml file holding the next scene
     * @param windowTitle the new window title to set
     * @param modality mode for new window (NONE=unlocked, WINDOW_MODAL=locked to new window)
     * @return FXML Loader for use when getting the controller
     * @throws IOException if .fxml filename cannot be found
     */
    public static void newStage(ActionEvent event,
                                DataTransferObject user,
                                String fxmlFileName,
                                String windowTitle,
                                Modality modality) throws IOException {

        newStage(event, null, user, fxmlFileName, windowTitle, modality);
    }

    /**
     * Opens a new window for add/modify appointments or customers
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
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(GuiUtil.class.getResource(fxmlFileName));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner( ((Node)event.getSource()).getScene().getWindow() );

        if(modality.equals(Modality.NONE))
            ((Node)(event.getSource())).getScene().getWindow().hide();  // close parent window

        stage.show();

        AuthenticatedController controller = loader.getController();
        controller.passCurrentUser(user);
        controller.passExistingRecord(passedObject);
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
     */
    public static void confirmDeletion(String title,
                                       String header,
                                       String content,
                                       BooleanSupplier lambda) {
        boolean success = true;

        Alert confirmRemove = new Alert(Alert.AlertType.CONFIRMATION);
        confirmRemove.setTitle(title);
        confirmRemove.setHeaderText(header);
        confirmRemove.setContentText(content);

        Optional<ButtonType> result = confirmRemove.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
            success = lambda.getAsBoolean();

        if (!success) {
            Alert deletionError = new Alert(Alert.AlertType.ERROR);
            deletionError.setHeaderText("Database Error");
            deletionError.setContentText("Unable to delete selected item!");
            deletionError.showAndWait();
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
        inputWarning.showAndWait();

        throw new InvalidInputException("Logical error check:\n" + content);
    }

}
