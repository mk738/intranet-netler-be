package com.company.intranet.hub;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.hub.dto.EventRsvpDto;
import com.company.intranet.hub.dto.RsvpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventRsvpController.class)
class EventRsvpControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean EventRsvpService            rsvpService;
    @MockBean FirebaseAuth                firebaseAuth;
    @MockBean EmployeeRepository          employeeRepository;
    @MockBean JpaMetamodelMappingContext  jpaMetamodelMappingContext;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Employee employee() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("emp@x.com").role(Employee.Role.EMPLOYEE).build();
    }

    private Authentication auth(Employee emp) {
        return new UsernamePasswordAuthenticationToken(
                emp, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + emp.getRole().name())));
    }

    private EventRsvpDto sampleRsvp(String myStatus) {
        return new EventRsvpDto(myStatus, 3L, 1L, 0L);
    }

    // ── GET /api/events/{id}/rsvp ─────────────────────────────────────────────

    @Test
    void getRsvp_returns200WithCounts() throws Exception {
        Employee emp = employee();
        UUID eventId = UUID.randomUUID();
        when(rsvpService.getRsvp(eq(eventId), any())).thenReturn(sampleRsvp("GOING"));

        mockMvc.perform(get("/api/events/" + eventId + "/rsvp")
                        .with(authentication(auth(emp))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.myRsvp").value("GOING"))
                .andExpect(jsonPath("$.data.goingCount").value(3))
                .andExpect(jsonPath("$.data.maybeCount").value(1))
                .andExpect(jsonPath("$.data.notGoingCount").value(0));
    }

    @Test
    void getRsvp_noRsvpYet_returnsNullMyRsvp() throws Exception {
        Employee emp = employee();
        UUID eventId = UUID.randomUUID();
        when(rsvpService.getRsvp(eq(eventId), any())).thenReturn(sampleRsvp(null));

        mockMvc.perform(get("/api/events/" + eventId + "/rsvp")
                        .with(authentication(auth(emp))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.myRsvp").doesNotExist()); // null omitted by @JsonInclude
    }

    @Test
    void getRsvp_eventNotFound_returns404() throws Exception {
        Employee emp = employee();
        UUID eventId = UUID.randomUUID();
        when(rsvpService.getRsvp(any(), any()))
                .thenThrow(new ResourceNotFoundException("Event not found"));

        mockMvc.perform(get("/api/events/" + eventId + "/rsvp")
                        .with(authentication(auth(emp))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @Test
    void getRsvp_unauthenticated_returns401() throws Exception {
        UUID eventId = UUID.randomUUID();
        mockMvc.perform(get("/api/events/" + eventId + "/rsvp"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/events/{id}/rsvp ────────────────────────────────────────────

    @Test
    void submitRsvp_going_returns200WithUpdatedCounts() throws Exception {
        Employee emp = employee();
        UUID eventId = UUID.randomUUID();
        RsvpRequest req = new RsvpRequest(EventRsvp.RsvpStatus.GOING);

        when(rsvpService.submitRsvp(eq(eventId), any(RsvpRequest.class), any()))
                .thenReturn(sampleRsvp("GOING"));

        mockMvc.perform(post("/api/events/" + eventId + "/rsvp")
                        .with(authentication(auth(emp)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.myRsvp").value("GOING"))
                .andExpect(jsonPath("$.data.goingCount").value(3));
    }

    @Test
    void submitRsvp_nullStatus_returns422() throws Exception {
        Employee emp = employee();
        UUID eventId = UUID.randomUUID();

        mockMvc.perform(post("/api/events/" + eventId + "/rsvp")
                        .with(authentication(auth(emp)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": null}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
