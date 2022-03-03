package scheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    /**
     * Launches JavaFX GUI.
     * <p>Grabs container hierarchy from .fxml file</p>
     * @param primaryStage The primary window where the application resides.
     * @throws IOException if the .fxml file cannot be found.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("view/login-view.fxml"));

        primaryStage.setTitle("Appointment Scheduler - Login");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();
    }

    /**
     * The Java main method where execution begins.
     * <p>This has only one statement that starts the application.</p>
     * @param args arguments passed from the OS (not used)
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
