package controller;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import model.DataTransferObject;

/**
 * Interface for controllers that make use of a user object for authenticated access.
 * @author Joseph Curtis
 * @version 2022.05.25
 */
public interface AuthenticatedController {
    /**
     * Authenticates user that is signed in.
     * @param user currently logged-in user
     */
    void passCurrentUser(DataTransferObject user);

    /**
     * Sets all properties for edited item to populate to corresponding text fields.
     * <p>The existing DTO in database is passed when changing the scene</p>
     * @see utility.GuiUtil#newStage(ActionEvent, DataTransferObject, DataTransferObject, String, String, Modality)
     * @param passedObject existing Data Transfer Object to be edited
     */
    void passExistingRecord(DataTransferObject passedObject);
}
