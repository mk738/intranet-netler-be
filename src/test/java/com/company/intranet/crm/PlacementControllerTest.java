package com.company.intranet.crm;

import com.company.intranet.crm.dto.*;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlacementController.class)
class PlacementControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CrmService                  crmService;
    @MockBean FirebaseAuth                firebaseAuth;
    @MockBean EmployeeRepository          employeeRepository;
    @MockBean JpaMetamodelMappingContext  jpaMetamodelMappingContext;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Authentication adminAuth() {
        Employee admin = Employee.builder()
                .id(UUID.randomUUID()).email("admin@x.com").role(Employee.Role.ADMIN).build();
        return new UsernamePasswordAuthenticationToken(
                admin, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private Authentication employeeAuth() {
        Employee emp = Employee.builder()
                .id(UUID.randomUUID()).email("emp@x.com").role(Employee.Role.EMPLOYEE).build();
        return new UsernamePasswordAuthenticationToken(
                emp, null, List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    }

    // ── GET /api/placements ───────────────────────────────────────────────────

    @Test
    void getPlacements_asAdmin_returns200() throws Exception {
        PlacementViewDto view = new PlacementViewDto(
                List.of(), List.of(), 0, 0, 0, 0);
        when(crmService.getPlacementView()).thenReturn(view);

        mockMvc.perform(get("/api/placements").with(authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPlaced").value(0));
    }

    @Test
    void getPlacements_asEmployee_returns403() throws Exception {
        mockMvc.perform(get("/api/placements").with(authentication(employeeAuth())))
                .andExpect(status().isForbidden());
    }

    // ── POST /api/assignments ─────────────────────────────────────────────────

    @Test
    void createAssignment_returns201() throws Exception {
        UUID empId    = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        CreateAssignmentRequest request = new CreateAssignmentRequest(
                empId, clientId, null,
                "Data Platform", LocalDate.of(2026, 4, 1), null);

        AssignmentDto dto = new AssignmentDto(
                UUID.randomUUID(), empId, "Erik L", "EL", "Dev",
                clientId, "Spotify", "Data Platform",
                LocalDate.of(2026, 4, 1), null, "ACTIVE");

        when(crmService.createAssignment(any(CreateAssignmentRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/assignments")
                        .with(authentication(adminAuth()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.projectName").value("Data Platform"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void createAssignment_employeeAlreadyPlaced_returns400() throws Exception {
        UUID empId    = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        CreateAssignmentRequest request = new CreateAssignmentRequest(
                empId, clientId, null,
                "Project", LocalDate.now(), null);

        when(crmService.createAssignment(any()))
                .thenThrow(new com.company.intranet.common.exception.BadRequestException(
                        "Employee already has an active assignment"));

        mockMvc.perform(post("/api/assignments")
                        .with(authentication(adminAuth()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Employee already has an active assignment"));
    }

    // ── PUT /api/assignments/{id}/end ─────────────────────────────────────────

    @Test
    void endAssignment_returns200WithEndedStatus() throws Exception {
        UUID id = UUID.randomUUID();

        AssignmentDto dto = new AssignmentDto(
                id, UUID.randomUUID(), "Erik L", "EL", "Dev",
                UUID.randomUUID(), "Spotify", "Project",
                LocalDate.of(2025, 1, 1), LocalDate.now(), "ENDED");

        when(crmService.endAssignment(eq(id), any())).thenReturn(dto);

        mockMvc.perform(put("/api/assignments/" + id + "/end")
                        .with(authentication(adminAuth()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ENDED"))
                .andExpect(jsonPath("$.data.id").value(id.toString()));
    }
}
