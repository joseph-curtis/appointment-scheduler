package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
 * @version 2022.06.01
 */
public record Appointment(Integer id,
                          String title,
                          String description,
                          String location,
                          String type,
                          LocalDateTime start,
                          LocalDateTime end,
                          Integer customerId,
                          String customerName,
                          Integer userId,
                          Integer contactId,
                          String contactName,
                          String contactEmail) implements DataTransferObject {

    /**
     * Property used for Cell Value Factories
     * @return formatted appointment start date & time
     */
    public String getStart() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return start.format(format);
    }

    /**
     * Property used for Cell Value Factories
     * @return formatted appointment start date & time
     */
    public String getEnd() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return end.format(format);
    }
}
