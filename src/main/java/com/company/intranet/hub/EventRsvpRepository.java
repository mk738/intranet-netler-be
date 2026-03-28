package com.company.intranet.hub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRsvpRepository extends JpaRepository<EventRsvp, UUID> {

    @Query("SELECT r FROM EventRsvp r WHERE r.event.id = :eventId AND r.employee.id = :employeeId")
    Optional<EventRsvp> findByEventAndEmployee(
            @Param("eventId")     UUID eventId,
            @Param("employeeId")  UUID employeeId);

    @Query("SELECT r FROM EventRsvp r WHERE r.employee.id = :employeeId AND r.event.id IN :eventIds")
    List<EventRsvp> findByEmployeeAndEventIds(
            @Param("employeeId") UUID employeeId,
            @Param("eventIds")   List<UUID> eventIds);

    @Query("SELECT COUNT(r) FROM EventRsvp r WHERE r.event.id = :eventId AND r.status = :status")
    long countByEventIdAndStatus(
            @Param("eventId") UUID eventId,
            @Param("status")  EventRsvp.RsvpStatus status);

    /**
     * Atomic upsert via PostgreSQL ON CONFLICT.
     * Inserts a new RSVP row or updates the status if one already exists
     * for the (event_id, employee_id) pair, avoiding any lost-update race.
     */
    @Modifying
    @Query(value = """
            INSERT INTO event_rsvp (id, event_id, employee_id, status, created_at, updated_at)
            VALUES (gen_random_uuid(), :eventId, :employeeId, :status, NOW(), NOW())
            ON CONFLICT ON CONSTRAINT uq_rsvp_event_employee
            DO UPDATE SET status = :status, updated_at = NOW()
            """, nativeQuery = true)
    void upsert(
            @Param("eventId")    UUID   eventId,
            @Param("employeeId") UUID   employeeId,
            @Param("status")     String status);
}
