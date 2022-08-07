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

import DAO.UserDaoImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;
import utility.GuiUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.*;

/**
 * The Controller class for the login window.
 * @author Joseph Curtis
 * @version 2022.08.06
 */

public class LoginController implements Initializable {

    protected static final Logger activityLog = Logger.getLogger(LoginController.class.getName());

    @FXML private MenuBar topMenuBar;
    @FXML private Label errorLabel;
    @FXML private Label timeZoneLabel;
    @FXML private TextField usernameTxt;
    @FXML private PasswordField passwordTxt;

    /**
     * Initializes the controller class
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // pressing enter or return fires the login button (also can be set in Scene Builder):
//        loginButton.setDefaultButton(true);
        // set label to show current time zone (system time)
        timeZoneLabel.setText(TimeZone.getDefault().getDisplayName(GuiUtil.locale));

        // read logger properties file
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("src/resources/LoginActivityLogger.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quits the application.
     * <p>Displays a confirmation dialog before exiting.</p>
     * @param event the user generated event (a button being clicked) that caused this to execute
     */
    @FXML
    void onActionExit(ActionEvent event) {
        GuiUtil.confirmExitApplication();
    }

    /**
     * Show the change language settings dialog. This allows the user to override the system default language.
     * @param event the user generated event (a menu being selected) that caused this to execute
     * @throws IOException if a Localization properties file cannot be found
     */
    @FXML
    void onActionSettings(ActionEvent event) throws IOException {
        if (GuiUtil.showSettings()) {
            // reload stage: //
            Parent root = FXMLLoader.load(getClass().getResource("/view/login-view.fxml"), GuiUtil.languageRb);
            Stage loginStage = (Stage) (topMenuBar.getScene().getWindow());
            loginStage.setTitle(GuiUtil.languageRb.getString("loginStage.title"));
            loginStage.setScene(new Scene(root));
            loginStage.show();
        }
    }

    /**
     * Show the About this Application dialog box
     * @param event the user generated event (a menu being selected) that caused this to execute
     */
    @FXML
    void onActionAbout(ActionEvent event) {
        GuiUtil.showAbout();
    }

    @FXML
    void imageOnMouse(MouseEvent event) {
        System.out.println(GuiUtil.languageRb.getString("easterEgg"));
    }

    /**
     * Checks username and password and logs user into application
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @throws IOException if .fxml filename cannot be found
     */
    @FXML
    void onActionLogin(ActionEvent event) throws IOException {
        User currentUserLogin;
        ButtonType okButton = new ButtonType(GuiUtil.languageRb.getString("okButton"), ButtonBar.ButtonData.OK_DONE);

        // check for blank/empty input fields:
        if (usernameTxt.getText().isBlank() || passwordTxt.getText().isEmpty()) {
            Alert blankTextInfo = new Alert(Alert.AlertType.INFORMATION,
                    GuiUtil.languageRb.getString("blankTextInfo.content"),
                    okButton);
            blankTextInfo.setTitle(GuiUtil.languageRb.getString("blankTextInfo.title"));
            blankTextInfo.setHeaderText(GuiUtil.languageRb.getString("blankTextInfo.header"));
            // set css theme
            blankTextInfo.getDialogPane().getStylesheets().add(
                    getClass().getResource("/view/modena-red.css").toExternalForm());

            blankTextInfo.showAndWait();
            errorLabel.setText(GuiUtil.languageRb.getString("errorLabel.blankInput"));
        } else {
            // Check database for user authentication:
            Optional<User> user = UserDaoImpl.authenticateUser(usernameTxt.getText(), passwordTxt.getText());

            if (user.isPresent()) {
                currentUserLogin = user.get();

                // log the login activity
                activityLog.info("user [" + usernameTxt.getText().toLowerCase()
                        + "] successfully logged in.");

                // Go to primary stage
                GuiUtil.newStage(event,
                        currentUserLogin,
                        "/view/primary-view.fxml",
                        GuiUtil.languageRb.getString("primaryStage.title"),
                        Modality.NONE,
                        GuiUtil.languageRb);
            } else {
                // log the user login attempt
                activityLog.warning("username [" + usernameTxt.getText().toLowerCase()
                        + "] attempted login and was unsuccessful.");

                // Show alert for login failure
                Alert loginFail = new Alert(Alert.AlertType.WARNING,
                        GuiUtil.languageRb.getString("loginFail.content"),
                        okButton);
                loginFail.setTitle(GuiUtil.languageRb.getString("loginFail.title"));
                loginFail.setHeaderText(GuiUtil.languageRb.getString("loginFail.header"));
                // set css theme
                loginFail.getDialogPane().getStylesheets().add(
                        getClass().getResource("/view/modena-red.css").toExternalForm());

                loginFail.showAndWait();
                errorLabel.setText(GuiUtil.languageRb.getString("errorLabel.loginFail"));
            }
        }
    }

}
