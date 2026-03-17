package com.company.intranet.hub;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.hub.dto.EventRsvpDto;
import com.company.intranet.hub.dto.RsvpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventRsvpService {

    private final EventRsvpRepository rsvpRepository;
    private final EventRepository     eventRepository;

    @Transactional(readOnly = true)
    public EventRsvpDto getRsvp(UUID eventId, Employee me) {
        ensureEventExists(eventId);
        return buildDto(eventId, me.getId());
    }

    @Transactional
    public EventRsvpDto submitRsvp(UUID eventId, RsvpRequest request, Employee me) {
        ensureEventExists(eventId);

        rsvpRepository.upsert(eventId, me.getId(), request.status().name());

        return buildDto(eventId, me.getId());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void ensureEventExists(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found");
        }
    }

    private EventRsvpDto buildDto(UUID eventId, UUID employeeId) {
        String myRsvp = rsvpRepository
                .findByEventAndEmployee(eventId, employeeId)
                .map(r -> r.getStatus().name())
                .orElse(null);

        long going     = rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.GOING);
        long maybe     = rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.MAYBE);
        long notGoing  = rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.NOT_GOING);

        return new EventRsvpDto(myRsvp, going, maybe, notGoing);
    }
}
