package model;

/**
 * Represents a Transfer Object used as a data carrier for Users.
 * @author Joseph Curtis
 * @version 2022.03.05
 */
public record User(Integer id,
                   String name,
                   String password) {
}
