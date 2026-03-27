package com.company.intranet.vacation;

import com.company.intranet.common.exception.BadRequestException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.vacation.dto.*;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VacationController.class)
class VacationControllerTest {

    @Autowired MockMvc       mockMvc;
    @Autowired ObjectMapper  objectMapper;

    @MockBean VacationService            vacationService;
    @MockBean FirebaseAuth               firebaseAuth;
    @MockBean EmployeeRepository         employeeRepository;
    @MockBean JpaMetamodelMappingContext jpaMetamodelMappingContext;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Employee adminEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("admin@x.com").role(Employee.Role.ADMIN).build();
    }

    private Employee superAdminEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("superadmin@x.com").role(Employee.Role.SUPERADMIN).build();
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

    private VacationDto sampleDto(UUID id) {
        return new VacationDto(id, UUID.randomUUID(), "Erik L", "EL",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(11),
                5, "PENDING", null, null, "2026-01-01T00:00:00Z", null, "Semester");
    }

    // ── GET /api/vacations/me ─────────────────────────────────────────────────

    @Test
    void getMyVacations_returns200WithList() throws Exception {
        Employee emp = regularEmployee();
        when(vacationService.getMyVacations(emp)).thenReturn(List.of(sampleDto(UUID.randomUUID())));

        mockMvc.perform(get("/api/vacations/me").with(authentication(auth(emp))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }

    // ── POST /api/vacations ───────────────────────────────────────────────────

    @Test
    void submitVacation_returns201() throws Exception {
        Employee emp = regularEmployee();
        UUID vacId   = UUID.randomUUID();
        SubmitVacationRequest req = new SubmitVacationRequest(
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(11), "Semester");

        when(vacationService.submitVacation(any(SubmitVacationRequest.class), eq(emp)))
                .thenReturn(sampleDto(vacId));

        mockMvc.perform(post("/api/vacations")
                        .with(authentication(auth(emp)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.daysCount").value(5));
    }

    @Test
    void submitVacation_pastStartDate_returns400() throws Exception {
        Employee emp = regularEmployee();
        SubmitVacationRequest req = new SubmitVacationRequest(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(3), "Semester");

        when(vacationService.submitVacation(any(), any()))
                .thenThrow(new BadRequestException("Start date cannot be in the past"));

        mockMvc.perform(post("/api/vacations")
                        .with(authentication(auth(emp)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Start date cannot be in the past"));
    }

    // ── DELETE /api/vacations/{id} ────────────────────────────────────────────

    @Test
    void cancelVacation_returns204() throws Exception {
        Employee emp = regularEmployee();
        UUID vacId   = UUID.randomUUID();

        doNothing().when(vacationService).cancelVacation(eq(vacId), eq(emp));

        mockMvc.perform(delete("/api/vacations/" + vacId)
                        .with(authentication(auth(emp)))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // ── GET /api/vacations ────────────────────────────────────────────────────

    @Test
    void getAllVacations_asAdmin_returns200() throws Exception {
        Employee admin = adminEmployee();
        when(vacationService.getAllVacations(null))
                .thenReturn(List.of(sampleDto(UUID.randomUUID())));

        mockMvc.perform(get("/api/vacations").with(authentication(auth(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getAllVacations_asEmployee_returns403() throws Exception {
        mockMvc.perform(get("/api/vacations")
                        .with(authentication(auth(regularEmployee()))))
                .andExpect(status().isForbidden());
    }

    // ── PUT /api/vacations/{id}/review ────────────────────────────────────────

    @Test
    void reviewVacation_returns200WithUpdatedStatus() throws Exception {
        Employee superAdmin = superAdminEmployee();
        UUID vacId          = UUID.randomUUID();

        ReviewVacationRequest req = new ReviewVacationRequest(true);
        VacationDto approved = new VacationDto(vacId, UUID.randomUUID(), "Erik L", "EL",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(11),
                5, "APPROVED", superAdmin.getEmail(), null, null, null, "Semester");

        when(vacationService.reviewVacation(eq(vacId), any(ReviewVacationRequest.class), eq(superAdmin)))
                .thenReturn(approved);

        mockMvc.perform(put("/api/vacations/" + vacId + "/review")
                        .with(authentication(auth(superAdmin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    // ── GET /api/vacations/summary ────────────────────────────────────────────

    @Test
    void getSummary_asAdmin_returns200WithCounts() throws Exception {
        Employee admin = adminEmployee();
        when(vacationService.getSummary())
                .thenReturn(new VacationSummaryDto(3, 7, 1));

        mockMvc.perform(get("/api/vacations/summary")
                        .with(authentication(auth(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pending").value(3))
                .andExpect(jsonPath("$.data.approved").value(7))
                .andExpect(jsonPath("$.data.rejected").value(1));
    }
}
