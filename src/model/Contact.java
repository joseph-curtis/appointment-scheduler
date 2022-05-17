package model;

/**
 * Represents a Transfer Object used as a data carrier for Contacts.
 * @author Joseph Curtis
 * @version 2022.03.05
 */
public record Contact(int id,
                      String name,
                      String email) {
}
