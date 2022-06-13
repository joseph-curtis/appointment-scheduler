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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;
import utility.DBUtil;
import utility.GuiUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The Controller class for the login window.
 * @author Joseph Curtis
 * @version 2022.06.11
 */

public class LoginController implements Initializable {
    private static ResourceBundle languageRb = ResourceBundle.getBundle("Localization", Locale.getDefault());

    /**
     * Initializes the controller class
     * @param location The location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // pressing enter or return fires the login button!
        loginButton.setDefaultButton(true);
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
    private Label titleLabel;
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
        confirmExit.setTitle(languageRb.getString("confirmExit.title"));
        confirmExit.setHeaderText(languageRb.getString("confirmExit.header"));
        confirmExit.setContentText(languageRb.getString("confirmExit.content"));

        Optional<ButtonType> result = confirmExit.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
            System.exit(0);
    }

    @FXML
    void onActionOpenSettings(ActionEvent event) throws IOException {
        Locale newLocale;

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
        Optional<String> result = changeLangDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get().equals(languageRb.getString("lang.en"))) {
                newLocale = new Locale("en");
                System.out.println("English selected.");
            } else if (result.get().equals(languageRb.getString("lang.fr"))) {
                newLocale = new Locale("fr");
                System.out.println("French selected.");
            } else if (result.get().equals(languageRb.getString("lang.de"))) {
                newLocale = new Locale("de");
                System.out.println("German selected.");
            } else if (result.get().equals(languageRb.getString("lang.jp"))) {
                newLocale = new Locale("jp");
                System.out.println("Japanese selected.");
            } else {
                newLocale = Locale.getDefault();
                System.out.println("DEFAULT was selected?");
            }
            languageRb = ResourceBundle.getBundle("Localization", newLocale);

            // reload stage: //
            Parent root = FXMLLoader.load(getClass().getResource("/view/login-view.fxml"), languageRb);
            Stage stage = (Stage)(loginMenuBar.getScene().getWindow());
            stage.setTitle(languageRb.getString("loginStage.title"));
            stage.setScene(new Scene(root));
            stage.show();
        }
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
        User currentUserLogin;

        // check for blank/empty input fields:
        if (usernameTxt.getText().isBlank() || passwordTxt.getText().isEmpty()) {
            Alert blankTextInfo = new Alert(Alert.AlertType.INFORMATION);
            blankTextInfo.setTitle(languageRb.getString("blankTextInfo.title"));
            blankTextInfo.setHeaderText(languageRb.getString("blankTextInfo.header"));
            blankTextInfo.setContentText(languageRb.getString("blankTextInfo.content"));
            blankTextInfo.showAndWait();
            errorLabel.setText(languageRb.getString("errorLabel.blankInput"));
        } else {
            // Check database for user authentication:
            Optional<User> user = DBUtil.authenticateUser(usernameTxt.getText(), passwordTxt.getText());

            if (user.isPresent()) {
                currentUserLogin = user.get();

                // Go to primary stage
                GuiUtil.newStage(event,
                        currentUserLogin,
                        "/view/primary-view.fxml",
                        "Appointment Scheduler - Calendar View",
                        Modality.NONE);
            } else {
                // Show alert for login failure
                Alert loginFail = new Alert(Alert.AlertType.WARNING);
                loginFail.setTitle(languageRb.getString("loginFail.title"));
                loginFail.setHeaderText(languageRb.getString("loginFail.header"));
                loginFail.setContentText(languageRb.getString("loginFail.content"));
                loginFail.showAndWait();
                errorLabel.setText(languageRb.getString("errorLabel.loginFail"));
            }
        }
    }
}
