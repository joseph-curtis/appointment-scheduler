package model;

/**
 * Represents a Transfer Object used as a data carrier for Country names.
 * @author Joseph Curtis
 * @version 2022.03.05
 */
public record FirstLevelDivision(int id,
                                 String name,
                                 Country country) {
}
