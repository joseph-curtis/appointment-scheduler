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

import controller.AppointmentController;
import controller.CustomerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.DataTransferObject;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * Contains helper functions for changing scenes and displaying dialog boxes to user.
 * <p>Use for easier maintenance of code
 * instead of code duplication</p>
 * @author Joseph Curtis
 * @version 2022.05.09
 */
public final class GuiUtil {
    /**
     * Switches scene to Create New Part or Product form.
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @param fxmlFileName the .fxml file holding the next scene
     * @param windowTitle the new window title to set
     * @return FXML Loader for use when getting the controller
     * @throws IOException if .fxml filename cannot be found
     */
    public static FXMLLoader changeStage(ActionEvent event,
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

        if(modality.equals(Modality.NONE)) {
            ((Node)(event.getSource())).getScene().getWindow().hide();
        }

        stage.show();

        return loader;
    }

    public static void changeStagePassObj(ActionEvent event,
                                          DataTransferObject passedObject,
                                          String fxmlFileName,
                                          String windowTitle,
                                          Modality modality) throws IOException {
        FXMLLoader loader = changeStage(event, fxmlFileName, windowTitle, modality);

        if (passedObject instanceof Appointment) {
            AppointmentController appointmentController = loader.getController();
            appointmentController.modifyAppointment(passedObject);
        } else if (passedObject instanceof Customer) {
            CustomerController customerController = loader.getController();
            customerController.modifyCustomer(passedObject);
        }
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
            Alert inventoryError = new Alert(Alert.AlertType.ERROR);
            inventoryError.setHeaderText("Database Error");
            inventoryError.setContentText("Unable to delete selected item!");
            inventoryError.showAndWait();
        }
    }

}
