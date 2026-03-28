package com.company.intranet.hub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    // Upcoming events (on or after a given date), sorted by date
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate from);

    // Upcoming events where the given employee has RSVP status GOING
    @Query("""
            SELECT e FROM Event e
            JOIN EventRsvp r ON r.event = e
            WHERE r.employee.id = :employeeId
              AND r.status = com.company.intranet.hub.EventRsvp.RsvpStatus.GOING
              AND e.eventDate >= :from
            ORDER BY e.eventDate ASC
            """)
    List<Event> findAttendingEvents(@Param("employeeId") UUID employeeId, @Param("from") LocalDate from);
}
