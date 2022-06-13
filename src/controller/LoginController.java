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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;
import utility.DBUtil;
import utility.GuiUtil;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The Controller class for the login window.
 * @author Joseph Curtis
 * @version 2022.06.13
 */

public class LoginController implements Initializable {
    private static Locale locale = Locale.getDefault();
    private static ResourceBundle languageRb = ResourceBundle.getBundle("Localization", locale);

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
        // set label to show current time zone (system time)
        timeZoneLabel.setText(TimeZone.getDefault().getDisplayName(locale));
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
    private Label timeZoneLabel;
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
        ButtonType okButton = new ButtonType(languageRb.getString("okButton"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(languageRb.getString("cancelButton"), ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION,
                languageRb.getString("confirmExit.content"),
                okButton, cancelButton);
        confirmExit.setTitle(languageRb.getString("confirmExit.title"));
        confirmExit.setHeaderText(languageRb.getString("confirmExit.header"));

        // Fully lambda approach to showing confirmation dialog:
        confirmExit.showAndWait()
                .filter(response -> response == okButton)
                .ifPresent(response -> System.exit(0));
    }

    /**
     * Show the change language settings dialog. This allows the user to override the system default language.
     * @param event the user generated event (a menu being selected) that caused this to execute
     * @throws IOException if a Localization properties file cannot be found
     */
    @FXML
    void onActionOpenSettings(ActionEvent event) throws IOException {
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

            // reload stage: //
            Parent root = FXMLLoader.load(getClass().getResource("/view/login-view.fxml"), languageRb);
            Stage stage = (Stage)(loginMenuBar.getScene().getWindow());
            stage.setTitle(languageRb.getString("loginStage.title"));
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    /**
     * Show the About this Application dialog box
     * @param event the user generated event (a menu being selected) that caused this to execute
     */
    @FXML
    void onActionShowAbout(ActionEvent event) {
        Alert aboutDialog = new Alert(Alert.AlertType.NONE,
                languageRb.getString("aboutDialog.content"),
                new ButtonType(languageRb.getString("closeButton"), ButtonBar.ButtonData.CANCEL_CLOSE));
        aboutDialog.setTitle(languageRb.getString("aboutDialog.title"));
        aboutDialog.setHeaderText(languageRb.getString("aboutDialog.header"));
        // add a graphic to dialog box:
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/resources/ACME_Catalog.png")).toExternalForm());
        ImageView imageView = new ImageView(image);
        aboutDialog.setGraphic(imageView);
        // set size to preferred height for content to show fully
        aboutDialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        aboutDialog.showAndWait();
    }

    @FXML
    void imageOnMouse(MouseEvent event) {
        System.out.println(languageRb.getString("easterEgg"));
    }

    /**
     * Checks username and password and logs user into application
     * @param event the user generated event (a button being clicked) that caused this to execute
     * @throws IOException if .fxml filename cannot be found
     */
    @FXML
    void onActionLogin(ActionEvent event) throws IOException {
        User currentUserLogin;
        ButtonType okButton = new ButtonType(languageRb.getString("okButton"), ButtonBar.ButtonData.OK_DONE);

        // check for blank/empty input fields:
        if (usernameTxt.getText().isBlank() || passwordTxt.getText().isEmpty()) {
            Alert blankTextInfo = new Alert(Alert.AlertType.INFORMATION,
                    languageRb.getString("blankTextInfo.content"),
                    okButton);
            blankTextInfo.setTitle(languageRb.getString("blankTextInfo.title"));
            blankTextInfo.setHeaderText(languageRb.getString("blankTextInfo.header"));
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
                        "ACME Appointment Scheduler - Calendar view",
                        Modality.NONE,
                        languageRb);
            } else {
                // Show alert for login failure
                Alert loginFail = new Alert(Alert.AlertType.WARNING,
                        languageRb.getString("loginFail.content"),
                        okButton);
                loginFail.setTitle(languageRb.getString("loginFail.title"));
                loginFail.setHeaderText(languageRb.getString("loginFail.header"));
                loginFail.showAndWait();
                errorLabel.setText(languageRb.getString("errorLabel.loginFail"));
            }
        }
    }
}
