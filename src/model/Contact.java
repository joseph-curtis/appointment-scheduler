package model;

/**
 * Represents a Transfer Object used as a data carrier for Contacts.
 * @author Joseph Curtis
 * @version 2022.06.08
 */
public record Contact(Integer id,
                      String name,
                      String email) implements DataTransferObject {

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

        /* Check if o is an instance of Contact or not.
          "null instanceof [type]" also returns false */
        if (!(o instanceof Contact)) {
            return false;
        }

        // typecast o to Contact so that we can compare data members
        Contact c = (Contact) o;

        // Compare the data members and return accordingly
        return id.equals(c.id())
                && name.equals(c.name())
                && email.equals(c.email());
    }
}
