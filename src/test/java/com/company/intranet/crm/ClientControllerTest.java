package com.company.intranet.crm;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.crm.dto.ClientDto;
import com.company.intranet.crm.dto.UpdateClientRequest;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CrmService                 crmService;
    @MockBean FirebaseAuth               firebaseAuth;
    @MockBean EmployeeRepository         employeeRepository;
    @MockBean JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private Authentication adminAuth() {
        Employee admin = Employee.builder()
                .id(UUID.randomUUID()).email("admin@x.com").role(Employee.Role.ADMIN).build();
        return new UsernamePasswordAuthenticationToken(
                admin, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private ClientDto spotifyDto() {
        return new ClientDto(UUID.randomUUID(), "Spotify", "Johan", "j@spotify.com",
                null, null, "ACTIVE", "2024-01-01T00:00:00Z");
    }

    // ── GET /api/clients ──────────────────────────────────────────────────────

    @Test
    void getAllClients_returns200WithList() throws Exception {
        when(crmService.getAllClients()).thenReturn(List.of(spotifyDto()));

        mockMvc.perform(get("/api/clients").with(authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].companyName").value("Spotify"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));
    }

    // ── PUT /api/clients/{id} ─────────────────────────────────────────────────

    @Test
    void updateClient_returns200WithUpdatedFields() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateClientRequest request = new UpdateClientRequest(
                "Spotify AB", "Anna", "anna@spotify.com",
                "+46700000000", "556000-0001", Client.ClientStatus.ACTIVE);

        ClientDto updated = new ClientDto(id, "Spotify AB", "Anna", "anna@spotify.com",
                "+46700000000", "556000-0001", "ACTIVE", "2024-01-01T00:00:00Z");

        when(crmService.updateClient(eq(id), any(UpdateClientRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/clients/" + id)
                        .with(authentication(adminAuth()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.companyName").value("Spotify AB"))
                .andExpect(jsonPath("$.data.contactName").value("Anna"));
    }

    // ── GET /api/clients/{id} — 404 ───────────────────────────────────────────

    @Test
    void getClientById_unknownId_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(crmService.getClientById(unknownId))
                .thenThrow(new ResourceNotFoundException("Client not found"));

        mockMvc.perform(get("/api/clients/" + unknownId)
                        .with(authentication(adminAuth())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client not found"));
    }
}
