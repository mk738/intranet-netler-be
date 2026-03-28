package com.company.intranet.hub;

import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.hub.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import com.company.intranet.security.RolePermissions;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired MockMvc       mockMvc;
    @Autowired ObjectMapper  objectMapper;

    @MockBean HubService                  hubService;
    @MockBean FirebaseAuth                firebaseAuth;
    @MockBean EmployeeRepository          employeeRepository;
    @MockBean JpaMetamodelMappingContext  jpaMetamodelMappingContext;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Employee adminEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("admin@x.com").role(Employee.Role.ADMIN).build();
    }

    private Employee regularEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("emp@x.com").role(Employee.Role.EMPLOYEE).build();
    }

    private Authentication auth(Employee employee) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()));
        RolePermissions.of(employee.getRole())
                .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.name())));
        return new UsernamePasswordAuthenticationToken(employee, null, authorities);
    }

    private EventDto sampleEvent(UUID id) {
        return new EventDto(id, "Team Meeting", "Quarterly sync", "HQ",
                LocalDate.now().plusDays(7), null, true, null, null, "Anna Admin", "2026-01-01T00:00:00Z", null);
    }

    // ── GET /api/events ───────────────────────────────────────────────────────

    @Test
    void getEvents_asEmployee_returns200WithList() throws Exception {
        Employee emp = regularEmployee();
        UUID id = UUID.randomUUID();
        when(hubService.getUpcomingEvents(any())).thenReturn(List.of(sampleEvent(id)));

        mockMvc.perform(get("/api/events").with(authentication(auth(emp))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Team Meeting"));
    }

    @Test
    void getEvents_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /api/events/{id} ──────────────────────────────────────────────────

    @Test
    void getEventById_returns200() throws Exception {
        Employee emp = regularEmployee();
        UUID id = UUID.randomUUID();
        when(hubService.getEventById(eq(id), any())).thenReturn(sampleEvent(id));

        mockMvc.perform(get("/api/events/" + id).with(authentication(auth(emp))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.location").value("HQ"));
    }

    // ── POST /api/events ──────────────────────────────────────────────────────

    @Test
    void createEvent_asAdmin_returns201() throws Exception {
        Employee admin = adminEmployee();
        UUID id = UUID.randomUUID();
        LocalDate eventDate = LocalDate.now().plusDays(7);
        CreateEventRequest req = new CreateEventRequest(
                "Team Meeting", "Quarterly sync", "HQ", eventDate, null, true, null, null);

        when(hubService.createEvent(any(CreateEventRequest.class), eq(admin)))
                .thenReturn(sampleEvent(id));

        mockMvc.perform(post("/api/events")
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Team Meeting"));
    }

    @Test
    void createEvent_asEmployee_returns403() throws Exception {
        Employee emp = regularEmployee();
        LocalDate eventDate = LocalDate.now().plusDays(7);
        CreateEventRequest req = new CreateEventRequest(
                "Team Meeting", null, null, eventDate, null, true, null, null);

        mockMvc.perform(post("/api/events")
                        .with(authentication(auth(emp)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── PUT /api/events/{id} ──────────────────────────────────────────────────

    @Test
    void updateEvent_asAdmin_returns200() throws Exception {
        Employee admin = adminEmployee();
        UUID id = UUID.randomUUID();
        LocalDate eventDate = LocalDate.now().plusDays(10);
        UpdateEventRequest req = new UpdateEventRequest(
                "Updated Meeting", "New description", "Remote", eventDate, null, true, null, null);

        when(hubService.updateEvent(eq(id), any(UpdateEventRequest.class)))
                .thenReturn(sampleEvent(id));

        mockMvc.perform(put("/api/events/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    // ── DELETE /api/events/{id} ───────────────────────────────────────────────

    @Test
    void deleteEvent_asAdmin_returns204() throws Exception {
        Employee admin = adminEmployee();
        UUID id = UUID.randomUUID();
        doNothing().when(hubService).deleteEvent(id);

        mockMvc.perform(delete("/api/events/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEvent_asEmployee_returns403() throws Exception {
        Employee emp = regularEmployee();
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/events/" + id)
                        .with(authentication(auth(emp)))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
