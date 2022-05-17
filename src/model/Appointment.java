package model;

import java.time.LocalDateTime;

/**
 * Represents a Transfer Object used as a data carrier for Appointments.
 * <p>Only userId is stored in database as foreign key;
 * contactName and contactEmail are saved here for convenience and
 * must be obtained through a table JOIN.</p>
 * <p>Appointment start and end times are stored in database as UTC. Extract
 * as {@link java.sql.Timestamp} object, then convert to LocalDateTime object.
 * MySQL driver v8.0.x automatically converts to SystemDefault time
 * when extracting from ResultSet.</p>
 * @author Joseph Curtis
 * @version 2022.05.17
 */
public record Appointment(Integer id,
                          String title,
                          String description,
                          String location,
                          String type,
                          LocalDateTime start,
                          LocalDateTime end,
                          Integer customerId,
                          Integer userId,
                          Integer contactId,
                          String contactName,
                          String contactEmail) {
}
