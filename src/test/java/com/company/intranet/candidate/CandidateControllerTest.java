package com.company.intranet.candidate;

import com.company.intranet.candidate.dto.CandidateDto;
import com.company.intranet.candidate.dto.CreateCandidateRequest;
import com.company.intranet.candidate.dto.PatchCandidateRequest;
import com.company.intranet.common.exception.ResourceNotFoundException;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CandidateController.class)
class CandidateControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CandidateService               candidateService;
    @MockBean FirebaseAuth                   firebaseAuth;
    @MockBean EmployeeRepository             employeeRepository;
    @MockBean JpaMetamodelMappingContext     jpaMetamodelMappingContext;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Employee admin() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("admin@x.com").role(Employee.Role.ADMIN).build();
    }

    private Employee employee() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("emp@x.com").role(Employee.Role.EMPLOYEE).build();
    }

    private Authentication auth(Employee emp) {
        return new UsernamePasswordAuthenticationToken(
                emp, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + emp.getRole().name())));
    }

    private CandidateDto sampleDto(UUID id) {
        return new CandidateDto(id, "Anna Andersson", "Fullstack-utvecklare",
                "anna@example.com", "070-123 45 67", "Stark React-bakgrund.",
                0, Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z"));
    }

    // ── GET /api/candidates ───────────────────────────────────────────────────

    @Test
    void getAll_asAdmin_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(candidateService.getAll()).thenReturn(List.of(sampleDto(id)));

        mockMvc.perform(get("/api/candidates").with(authentication(auth(admin()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Anna Andersson"))
                .andExpect(jsonPath("$.data[0].stage").value(0));
    }

    @Test
    void getAll_asEmployee_returns403() throws Exception {
        mockMvc.perform(get("/api/candidates").with(authentication(auth(employee()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/candidates"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/candidates ──────────────────────────────────────────────────

    @Test
    void create_asAdmin_returns201() throws Exception {
        Employee admin = admin();
        UUID id = UUID.randomUUID();
        CreateCandidateRequest req = new CreateCandidateRequest(
                "Anna Andersson", "Fullstack-utvecklare",
                "anna@example.com", "070-123 45 67", "Stark React-bakgrund.", 0);

        when(candidateService.create(any(CreateCandidateRequest.class))).thenReturn(sampleDto(id));

        mockMvc.perform(post("/api/candidates")
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Anna Andersson"))
                .andExpect(jsonPath("$.data.stage").value(0));
    }

    @Test
    void create_blankName_returns422() throws Exception {
        Employee admin = admin();
        CreateCandidateRequest req = new CreateCandidateRequest(
                "", "Developer", null, null, null, 0);

        mockMvc.perform(post("/api/candidates")
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create_invalidStage_returns422() throws Exception {
        Employee admin = admin();
        String body = """
                {"name":"Anna","role":"Dev","stage":6}
                """;

        mockMvc.perform(post("/api/candidates")
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create_asEmployee_returns403() throws Exception {
        CreateCandidateRequest req = new CreateCandidateRequest(
                "Anna", "Dev", null, null, null, 0);

        mockMvc.perform(post("/api/candidates")
                        .with(authentication(auth(employee())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── PATCH /api/candidates/{id} ────────────────────────────────────────────

    @Test
    void patch_asAdmin_returns200() throws Exception {
        Employee admin = admin();
        UUID id = UUID.randomUUID();
        PatchCandidateRequest req = new PatchCandidateRequest(null, null, null, null, null, 2);

        when(candidateService.patch(eq(id), any(PatchCandidateRequest.class)))
                .thenReturn(sampleDto(id));

        mockMvc.perform(patch("/api/candidates/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void patch_notFound_returns404() throws Exception {
        Employee admin = admin();
        UUID id = UUID.randomUUID();
        PatchCandidateRequest req = new PatchCandidateRequest(null, null, null, null, null, 1);

        when(candidateService.patch(any(), any()))
                .thenThrow(new ResourceNotFoundException("Candidate not found"));

        mockMvc.perform(patch("/api/candidates/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Candidate not found"));
    }

    @Test
    void patch_invalidStage_returns422() throws Exception {
        Employee admin = admin();
        String body = """
                {"stage":99}
                """;

        mockMvc.perform(patch("/api/candidates/" + UUID.randomUUID())
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── DELETE /api/candidates/{id} ───────────────────────────────────────────

    @Test
    void delete_asAdmin_returns204() throws Exception {
        Employee admin = admin();
        UUID id = UUID.randomUUID();
        doNothing().when(candidateService).delete(id);

        mockMvc.perform(delete("/api/candidates/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_asEmployee_returns403() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/candidates/" + id)
                        .with(authentication(auth(employee())))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
