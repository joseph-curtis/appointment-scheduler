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

package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main class for the Appointment Scheduler Desktop Application.
 * This is where the JVM starts program execution.
 * Contains the Java <code>main</code> method.
 * <br>
 * <h1>JavaDocs folder is located within the root folder, ie: acme-ims\javadocs\</h1>
 * @author Joseph Curtis
 * @version 2022.03.02
 */

public class MainApplication extends Application {
    /**
     * Launches JavaFX GUI.
     * <p>Grabs container hierarchy from .fxml file</p>
     * @param primaryStage The primary window where the application resides.
     * @throws IOException if the .fxml file cannot be found.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/login-view.fxml"));

        primaryStage.setTitle("Appointment Scheduler - Login");
        primaryStage.setScene(new Scene(root, 400, 315));
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
