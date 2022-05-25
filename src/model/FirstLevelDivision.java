package model;

/**
 * Represents a Transfer Object used as a data carrier for First-Level Divisions.
 * <p>Only countryId is stored in database as foreign key;
 * country is saved here for convenience and
 * must be obtained through a table join</p>
 * @author Joseph Curtis
 * @version 2022.05.24
 */
public record FirstLevelDivision(Integer id,
                                 String division,
                                 Integer countryId,
                                 String country) implements DataTransferObject {
}
