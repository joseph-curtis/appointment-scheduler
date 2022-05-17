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

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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
    public static FXMLLoader changeScene(ActionEvent event,
                                         String fxmlFileName,
                                         String windowTitle) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        //loader.load(GuiUtil.class.);
        loader.setLocation(GuiUtil.class.getResource(fxmlFileName));
        Parent root = loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(root));
        stage.show();

        return loader;
    }

}
