package scheduler.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import scheduler.DAO.Database;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Controller class for the login window.
 * @author Joseph Curtis
 * @version 2022.03.02
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
        Database.openConnection();
    }

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        Database.closeConnection();
    }

}
