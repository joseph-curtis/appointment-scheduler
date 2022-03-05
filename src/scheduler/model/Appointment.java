package scheduler.model;

import java.util.Date;
import java.util.Optional;

public record Appointment(int id,
                          String title,
                          Optional<String> description,
                          String location,
                          Optional<String> type,
                          Date start,
                          Date end,
                          Optional<Customer> customer,
                          Optional<User> user,
                          Optional<Contact> contact) {
}
