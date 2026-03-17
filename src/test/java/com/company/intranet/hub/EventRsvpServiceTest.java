package com.company.intranet.hub;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.hub.dto.EventRsvpDto;
import com.company.intranet.hub.dto.RsvpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRsvpServiceTest {

    @Mock EventRsvpRepository rsvpRepository;
    @Mock EventRepository     eventRepository;

    @InjectMocks EventRsvpService rsvpService;

    private Employee employee(UUID id) {
        return Employee.builder().id(id).email("e@x.com").role(Employee.Role.EMPLOYEE).build();
    }

    private EventRsvp rsvp(UUID eventId, UUID employeeId, EventRsvp.RsvpStatus status) {
        Employee emp = employee(employeeId);
        EventRsvp r = new EventRsvp();
        r.setEmployee(emp);
        r.setStatus(status);
        return r;
    }

    // ── getRsvp ───────────────────────────────────────────────────────────────

    @Test
    void getRsvp_eventNotFound_throws404() {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThatThrownBy(() -> rsvpService.getRsvp(eventId, employee(UUID.randomUUID())))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getRsvp_noRsvpYet_returnsNullMyRsvpWithCounts() {
        UUID eventId    = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        Employee emp    = employee(employeeId);

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(rsvpRepository.findByEventAndEmployee(eventId, employeeId)).thenReturn(Optional.empty());
        when(rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.GOING)).thenReturn(2L);
        when(rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.MAYBE)).thenReturn(1L);
        when(rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.NOT_GOING)).thenReturn(0L);

        EventRsvpDto result = rsvpService.getRsvp(eventId, emp);

        assertThat(result.myRsvp()).isNull();
        assertThat(result.goingCount()).isEqualTo(2);
        assertThat(result.maybeCount()).isEqualTo(1);
        assertThat(result.notGoingCount()).isEqualTo(0);
    }

    @Test
    void getRsvp_existingRsvp_returnsMyStatus() {
        UUID eventId    = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        Employee emp    = employee(employeeId);

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(rsvpRepository.findByEventAndEmployee(eventId, employeeId))
                .thenReturn(Optional.of(rsvp(eventId, employeeId, EventRsvp.RsvpStatus.GOING)));
        when(rsvpRepository.countByEventIdAndStatus(any(), any())).thenReturn(3L);

        EventRsvpDto result = rsvpService.getRsvp(eventId, emp);

        assertThat(result.myRsvp()).isEqualTo("GOING");
    }

    // ── submitRsvp ────────────────────────────────────────────────────────────

    @Test
    void submitRsvp_eventNotFound_throws404() {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThatThrownBy(() -> rsvpService.submitRsvp(
                eventId, new RsvpRequest(EventRsvp.RsvpStatus.GOING), employee(UUID.randomUUID())))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void submitRsvp_callsUpsertAndReturnsUpdatedCounts() {
        UUID eventId    = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        Employee emp    = employee(employeeId);

        when(eventRepository.existsById(eventId)).thenReturn(true);
        doNothing().when(rsvpRepository).upsert(eventId, employeeId, "MAYBE");
        when(rsvpRepository.findByEventAndEmployee(eventId, employeeId))
                .thenReturn(Optional.of(rsvp(eventId, employeeId, EventRsvp.RsvpStatus.MAYBE)));
        when(rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.GOING)).thenReturn(1L);
        when(rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.MAYBE)).thenReturn(2L);
        when(rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.NOT_GOING)).thenReturn(0L);

        EventRsvpDto result = rsvpService.submitRsvp(
                eventId, new RsvpRequest(EventRsvp.RsvpStatus.MAYBE), emp);

        verify(rsvpRepository).upsert(eventId, employeeId, "MAYBE");
        assertThat(result.myRsvp()).isEqualTo("MAYBE");
        assertThat(result.maybeCount()).isEqualTo(2);
    }
}
