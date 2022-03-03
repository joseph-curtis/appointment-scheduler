package scheduler.model;

import java.util.Date;

public record Appointment(int id,
                          String title,
                          String description,
                          String location,
                          String type,
                          Date start,
                          Date end,
                          Customer customer,
                          User user,
                          Contact contact) {
}
