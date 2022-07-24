package model;

/**
 * Represents a Transfer Object used as a data carrier for Users.
 * <p>Password is not stored here for security concerns. Pass it as a parameter.</p>
 * @author Joseph Curtis
 * @version 2022.07.23
 */
public record User(Integer id,
                   String name) implements DataTransferObject {

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

        /* Check if o is an instance of User or not.
          "null instanceof [type]" also returns false */
        if (!(o instanceof User)) {
            return false;
        }

        // typecast o to User so that we can compare data members
        User u = (User) o;

        // Compare the data members and return accordingly
        return id.equals(u.id()) && name.equals(u.name());
    }
}
