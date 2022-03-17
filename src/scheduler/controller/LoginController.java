package scheduler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
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

    @FXML
    void onActionExitApplication(ActionEvent event) {

    }

    @FXML
    void onActionLogin(ActionEvent event) {

    }

    @FXML
    void onActionOpenSettings(ActionEvent event) {

    }

    @FXML
    void onActionShowAbout(ActionEvent event) {

    }

}
