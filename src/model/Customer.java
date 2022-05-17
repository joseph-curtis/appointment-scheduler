package model;

/**
 * Represents a Transfer Object used as a data carrier for Customers.
 * @author Joseph Curtis
 * @version 2022.03.05
 */
public record Customer(int id,
                       String name,
                       String address,
                       String postalCode,
                       String phone,
                       FirstLevelDivision division) {
}
