package app.appointment.repository;

import app.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.mechanic.id = :mechanicId " +
            "AND DATE(a.start) = CURRENT_DATE " +
            "ORDER BY a.start ASC")
    List<Appointment> findTodayAppointmentsByMechanic(
            @Param("mechanicId") UUID mechanicId);

    @Query(value = "SELECT * FROM appointment a " +
            "WHERE a.mechanic_id = :mechanicId " +
            "AND DATE(a.start) BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 6 DAY) " +
            "ORDER BY a.start ASC", nativeQuery = true)
    List<Appointment> findAppointmentsForNextWeekByMechanic(
            @Param("mechanicId") UUID mechanicId);
}
