package com.company.intranet.hub;

import com.company.intranet.common.exception.ResourceNotFoundException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsController.class)
class NewsControllerTest {

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
        String authority = "ROLE_" + employee.getRole().name();
        return new UsernamePasswordAuthenticationToken(
                employee, null, List.of(new SimpleGrantedAuthority(authority)));
    }

    private NewsListDto emptyList() {
        return new NewsListDto(List.of(), 0, 10, 0, 0);
    }

    private NewsPostDetailDto sampleDetail(UUID id) {
        return new NewsPostDetailDto(id, "Test Title", "Body text",
                "Anna Admin", "AA", null, false, null, null, Instant.parse("2026-01-01T00:00:00Z"), null);
    }

    // ── GET /api/news ─────────────────────────────────────────────────────────

    @Test
    void getNews_asEmployee_returns200() throws Exception {
        Employee emp = regularEmployee();
        when(hubService.getNews(0, 10, false)).thenReturn(emptyList());

        mockMvc.perform(get("/api/news").with(authentication(auth(emp))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getNews_asAdmin_returnsAllPosts() throws Exception {
        Employee admin = adminEmployee();
        NewsPostDto dto = new NewsPostDto(UUID.randomUUID(), "Title", "Anna", "AA",
                null, false, false, null, null);
        when(hubService.getNews(0, 10, true))
                .thenReturn(new NewsListDto(List.of(dto), 0, 10, 1, 1));

        mockMvc.perform(get("/api/news").with(authentication(auth(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("Title"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void getNews_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/news"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /api/news/{id} ────────────────────────────────────────────────────

    @Test
    void getNewsById_returns200() throws Exception {
        Employee emp = regularEmployee();
        UUID id = UUID.randomUUID();
        when(hubService.getNewsById(eq(id), eq(false))).thenReturn(sampleDetail(id));

        mockMvc.perform(get("/api/news/" + id).with(authentication(auth(emp))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Test Title"));
    }

    @Test
    void getNewsById_notFound_returns404() throws Exception {
        Employee emp = regularEmployee();
        UUID id = UUID.randomUUID();
        when(hubService.getNewsById(any(), anyBoolean()))
                .thenThrow(new ResourceNotFoundException("News post not found"));

        mockMvc.perform(get("/api/news/" + id).with(authentication(auth(emp))))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/news ────────────────────────────────────────────────────────

    @Test
    void createNews_asAdmin_returns201() throws Exception {
        Employee admin = adminEmployee();
        UUID id = UUID.randomUUID();
        CreateNewsRequest req = new CreateNewsRequest("Title", "Body", false, false, null, null, null);

        when(hubService.createNews(any(CreateNewsRequest.class), eq(admin)))
                .thenReturn(sampleDetail(id));

        mockMvc.perform(post("/api/news")
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Test Title"));
    }

    @Test
    void createNews_asEmployee_returns403() throws Exception {
        Employee emp = regularEmployee();
        CreateNewsRequest req = new CreateNewsRequest("Title", "Body", false, false, null, null, null);

        mockMvc.perform(post("/api/news")
                        .with(authentication(auth(emp)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── PUT /api/news/{id} ────────────────────────────────────────────────────

    @Test
    void updateNews_asAdmin_returns200() throws Exception {
        Employee admin = adminEmployee();
        UUID id = UUID.randomUUID();
        UpdateNewsRequest req = new UpdateNewsRequest("Updated Title", "New body", true, null, null);

        when(hubService.updateNews(eq(id), any(UpdateNewsRequest.class)))
                .thenReturn(sampleDetail(id));

        mockMvc.perform(put("/api/news/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    // ── DELETE /api/news/{id} ─────────────────────────────────────────────────

    @Test
    void deleteNews_asAdmin_returns204() throws Exception {
        Employee admin = adminEmployee();
        UUID id = UUID.randomUUID();
        doNothing().when(hubService).deleteNews(id);

        mockMvc.perform(delete("/api/news/" + id)
                        .with(authentication(auth(admin)))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // ── PUT /api/news/{id}/published ────────────────────────────────────────────

    @Test
    void publishNews_asAdmin_returns200() throws Exception {
        Employee admin = adminEmployee();
        UUID id = UUID.randomUUID();
        PublishNewsRequest req = new PublishNewsRequest(true);

        when(hubService.publishNews(eq(id), eq(true))).thenReturn(sampleDetail(id));

        mockMvc.perform(put("/api/news/" + id + "/published")
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Test Title"));
    }
}
