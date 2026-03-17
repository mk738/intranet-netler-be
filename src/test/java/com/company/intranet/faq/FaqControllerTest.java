package com.company.intranet.faq;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.faq.dto.CreateFaqRequest;
import com.company.intranet.faq.dto.FaqItemDto;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FaqController.class)
class FaqControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean FaqService                  faqService;
    @MockBean FirebaseAuth                firebaseAuth;
    @MockBean EmployeeRepository          employeeRepository;
    @MockBean JpaMetamodelMappingContext  jpaMetamodelMappingContext;

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

    private FaqItemDto sampleDto(UUID id) {
        return new FaqItemDto(id, "How do I submit vacation?",
                "Go to the Vacation section.", "Vacation", 0, "2026-01-01T00:00:00Z");
    }

    // ── GET /api/faq ──────────────────────────────────────────────────────────

    @Test
    void getAll_asEmployee_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(faqService.getAll()).thenReturn(List.of(sampleDto(id)));

        mockMvc.perform(get("/api/faq").with(authentication(auth(employee()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].question").value("How do I submit vacation?"))
                .andExpect(jsonPath("$.data[0].sortOrder").value(0));
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/faq"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/faq ─────────────────────────────────────────────────────────

    @Test
    void create_asAdmin_returns201() throws Exception {
        Employee admin = admin();
        UUID id = UUID.randomUUID();
        CreateFaqRequest req = new CreateFaqRequest("New question?", "The answer.", "HR");

        when(faqService.create(any(CreateFaqRequest.class), eq(admin))).thenReturn(sampleDto(id));

        mockMvc.perform(post("/api/faq")
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    void create_asEmployee_returns403() throws Exception {
        CreateFaqRequest req = new CreateFaqRequest("Q?", "A.", null);

        mockMvc.perform(post("/api/faq")
                        .with(authentication(auth(employee())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_blankQuestion_returns422() throws Exception {
        Employee admin = admin();
        CreateFaqRequest req = new CreateFaqRequest("", "A.", null);

        mockMvc.perform(post("/api/faq")
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── PUT /api/faq/{id} ─────────────────────────────────────────────────────

    @Test
    void update_asAdmin_returns200() throws Exception {
        Employee admin = admin();
        UUID id = UUID.randomUUID();
        CreateFaqRequest req = new CreateFaqRequest("Updated Q?", "Updated A.", "Legal");

        when(faqService.update(eq(id), any(CreateFaqRequest.class))).thenReturn(sampleDto(id));

        mockMvc.perform(put("/api/faq/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        Employee admin = admin();
        UUID id = UUID.randomUUID();
        CreateFaqRequest req = new CreateFaqRequest("Q?", "A.", null);

        when(faqService.update(any(), any())).thenThrow(new ResourceNotFoundException("FAQ item not found"));

        mockMvc.perform(put("/api/faq/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("FAQ item not found"));
    }

    // ── DELETE /api/faq/{id} ──────────────────────────────────────────────────

    @Test
    void delete_asAdmin_returns204() throws Exception {
        Employee admin = admin();
        UUID id = UUID.randomUUID();
        doNothing().when(faqService).delete(id);

        mockMvc.perform(delete("/api/faq/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_asEmployee_returns403() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/faq/" + id)
                        .with(authentication(auth(employee())))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
