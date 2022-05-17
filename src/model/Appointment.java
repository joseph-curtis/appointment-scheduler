package model;

import java.util.Date;
import java.util.Optional;

/**
 * Represents a Transfer Object used as a data carrier for Appointments.
 * @author Joseph Curtis
 * @version 2022.03.05
 */
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
