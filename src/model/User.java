package model;

/**
 * Represents a Transfer Object used as a data carrier for Users.
 * <p>Password is not stored here for security concerns. Pass as reference.</p>
 * @author Joseph Curtis
 * @version 2022.05.25
 */
public record User(Integer id,
                   String name) implements DataTransferObject {
}
