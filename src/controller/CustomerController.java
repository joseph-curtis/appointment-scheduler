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
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import model.Customer;
import model.DataTransferObject;

public class CustomerController {
    /**
     * The Appointment in the database to modify
     */
    Customer existingCustomer;

    /**
     * Sets all properties for edited item to populate to corresponding text fields.
     * <p>The existing Customer in DB is passed when changing the scene</p>
     * @see utility.GuiUtil#changeStagePassObj(ActionEvent, DataTransferObject, String, String, Modality)
     * @param passedObject existing appointment to be edited
     */
    public void modifyCustomer(DataTransferObject passedObject) {
        existingCustomer = (Customer) passedObject;

        currentOperationLabel.setText("Edit Customer");

        idTxt.setText(String.valueOf(passedObject.id()));
        nameTxt.setText(((Customer) passedObject).name());
        addressTxt.setText(((Customer) passedObject).address());
        postCodeTxt.setText(((Customer) passedObject).postalCode());
        phoneTxt.setText(((Customer) passedObject).phone());




        // TODO:  set division combo box
        // TODO:  set country combo box
    }

    @FXML
    private Label currentOperationLabel;

    @FXML
    private TextField idTxt;

    @FXML
    private TextField addressTxt;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private TextField postCodeTxt;

    @FXML
    private ComboBox<?> countryComboBox;

    @FXML
    private ComboBox<?> divisionComboBox;

    @FXML
    void onActionCancel(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    void onActionSaveCustomer(ActionEvent event) {

    }

    @FXML
    void onActionListCountries(ActionEvent event) {

    }

    @FXML
    void onActionListDivisions(ActionEvent event) {

    }

}
