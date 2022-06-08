package model;

/**
 * Represents a Transfer Object used as a data carrier for First-Level Divisions.
 * <p>Only countryId is stored in database as foreign key;
 * country is saved here for convenience and
 * must be obtained through a table join. Likewise when comparing for equality,
 * country string is not compared.</p>
 * @author Joseph Curtis
 * @version 2022.06.08
 */
public record FirstLevelDivision(Integer id,
                                 String division,
                                 Integer countryId,
                                 String country) implements DataTransferObject {

    @Override
    public String toString() {
        return id + ":  " + division;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Country or not.
          "null instanceof [type]" also returns false */
        if (!(o instanceof FirstLevelDivision)) {
            return false;
        }

        // typecast o to Country so that we can compare data members
        FirstLevelDivision d = (FirstLevelDivision) o;

        // Compare the data members and return accordingly
        return id.equals(d.id()) && division.equals(d.division()) && countryId.equals(d.countryId);
    }
}
