package DAO;

import model.Appointment;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Data Access Object for Appointment data.
 * @author Joseph Curtis
 * @version 2022.03.04
 */
public interface AppointmentDao {

    /**
     * @return all the appointments as a stream. The stream must be closed after use.
     * @throws Exception if any error occurs.
     */
    Stream<Appointment> getAll() throws Exception;

    /**
     * @param id unique identifier of the appointment.
     * @return an optional container with an Appointment object
     * if one with id exists, empty optional otherwise.
     * @throws Exception if any error occurs.
     */
    Optional<Appointment> getById(int id) throws Exception;

    /**
     * @param appointment the appointment to be added.
     * @return true if appointment is added, false if appointment already exists.
     * @throws Exception if any error occurs.
     */
    boolean add(Appointment appointment) throws Exception;

    /**
     * @param appointment the appointment to be updated.
     * @return true if appointment exists and is updated, else false.
     * @throws Exception if any error occurs.
     */
    boolean update(Appointment appointment) throws Exception;

    /**
     * @param appointment the appointment to be deleted.
     * @return true if appointment exists and is deleted, false otherwise.
     * @throws Exception if any error occurs.
     */
    boolean delete(Appointment appointment) throws Exception;
}
