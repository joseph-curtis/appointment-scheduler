package model;

/**
 * Represents a Transfer Object used as a data carrier for Customers.
 * <p>Only divisionId is stored in database as foreign key;
 * division and country are saved here for convenience and
 * must be obtained through table joins. Likewise equals does not
 * compare division or country name.</p>
 * @author Joseph Curtis
 * @version 2022.06.08
 */
public record Customer(Integer id,
                       String name,
                       String address,
                       String postalCode,
                       String phone,
                       Integer divisionId,
                       String division,
                       String country) implements DataTransferObject {

    @Override
    public String toString() {
        return id + ":  " + name;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Customer or not.
          "null instanceof [type]" also returns false */
        if (!(o instanceof Customer)) {
            return false;
        }

        // typecast o to Customer so that we can compare data members
        Customer c = (Customer) o;

        // Compare the data members and return accordingly
        return id.equals(c.id())
                && name.equals(c.name())
                && address.equals(c.address())
                && postalCode.equals(c.postalCode())
                && phone.equals(c.phone())
                && divisionId.equals(c.divisionId());
    }
}
