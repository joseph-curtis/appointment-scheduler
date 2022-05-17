package model;

/**
 * Represents a Transfer Object used as a data carrier for Customers.
 * <p>Only divisionId is stored in database as foreign key;
 * division and country are saved here for convenience and
 * must be obtained through table joins</p>
 * @author Joseph Curtis
 * @version 2022.05.17
 */
public record Customer(Integer id,
                       String name,
                       String address,
                       String postalCode,
                       String phone,
                       Integer divisionId,
                       String division,
                       String country) {
}
