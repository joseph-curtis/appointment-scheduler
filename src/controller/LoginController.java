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
import javafx.scene.control.*;
import javafx.stage.Modality;
import utility.GuiUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The Controller class for the login window.
 * @author Joseph Curtis
 * @version 2022.03.05
 */

public class LoginController implements Initializable {
    /**
     * Initializes the controller class
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("LoginController initialized");
    }

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private Label errorLabel;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private Label locationLabel;

    @FXML
    private Button loginButton;

    @FXML
    private MenuBar loginMenuBar;

    @FXML
    private Label loginText;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTxt;

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

    /**
     * Checks username and password and logs user into application
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @throws IOException if .fxml filename cannot be found
     */
    @FXML
    void onActionLogin(ActionEvent event) throws IOException {

        // TODO: implement username/password lookup and verification

        GuiUtil.changeStage(event,
                "/view/primary-view.fxml",
                "Appointment Scheduler - Calendar View",
                Modality.NONE);
    }
}
