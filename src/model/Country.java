package model;

/**
 * Represents a Transfer Object used as a data carrier for Countries.
 * @author Joseph Curtis
 * @version 2022.06.08
 */
public record Country(Integer id,
                      String country) implements DataTransferObject {

    @Override
    public String toString() {
        return id + ":  " + country;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Country or not.
          "null instanceof [type]" also returns false */
        if (!(o instanceof Country)) {
            return false;
        }

        // typecast o to Country so that we can compare data members
        Country c = (Country) o;

        // Compare the data members and return accordingly
        return id.equals(c.id()) && country.equals(c.country());
    }
}
