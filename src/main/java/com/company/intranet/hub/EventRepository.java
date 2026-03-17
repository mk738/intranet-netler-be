package com.company.intranet.hub;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    // Upcoming events (on or after a given date), sorted by date
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate from);
}
