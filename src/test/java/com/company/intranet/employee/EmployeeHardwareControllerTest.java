package com.company.intranet.employee;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.employee.dto.EmployeeHardwareDto;
import com.company.intranet.security.RolePermissions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeHardwareController.class)
class EmployeeHardwareControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean EmployeeHardwareService hardwareService;
    @MockBean FirebaseAuth            firebaseAuth;
    @MockBean EmployeeRepository      employeeRepository;
    @MockBean JpaMetamodelMappingContext jpaMetamodelMappingContext;

    // ── helpers ───────────────────────────────────────────────────────────────

    private static final UUID EMP_ID      = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID HARDWARE_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

    private Employee buildEmployee(Employee.Role role) {
        return Employee.builder()
                .id(EMP_ID)
                .email("marcus@company.com")
                .role(role)
                .build();
    }

    private Authentication auth(Employee employee) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()));
        RolePermissions.of(employee.getRole())
                .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.name())));
        return new UsernamePasswordAuthenticationToken(employee, null, authorities);
    }

    private EmployeeHardwareDto sampleDto() {
        return new EmployeeHardwareDto(HARDWARE_ID, "MacBook Pro", Instant.parse("2026-01-01T10:00:00Z"));
    }

    // ── GET /api/employees/{id}/hardware ──────────────────────────────────────

    @Test
    void getHardware_asAdmin_returns200WithList() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);
        when(hardwareService.getHardware(EMP_ID)).thenReturn(List.of(sampleDto()));

        mockMvc.perform(get("/api/employees/{id}/hardware", EMP_ID)
                        .with(authentication(auth(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(HARDWARE_ID.toString()))
                .andExpect(jsonPath("$.data[0].name").value("MacBook Pro"));
    }

    @Test
    void getHardware_emptyList_returnsEmptyArray() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);
        when(hardwareService.getHardware(EMP_ID)).thenReturn(List.of());

        mockMvc.perform(get("/api/employees/{id}/hardware", EMP_ID)
                        .with(authentication(auth(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getHardware_asEmployee_returns403() throws Exception {
        Employee emp = buildEmployee(Employee.Role.EMPLOYEE);

        mockMvc.perform(get("/api/employees/{id}/hardware", EMP_ID)
                        .with(authentication(auth(emp))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getHardware_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/employees/{id}/hardware", EMP_ID))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/employees/{id}/hardware ─────────────────────────────────────

    @Test
    void addHardware_asAdmin_returns201WithDto() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);
        when(hardwareService.addHardware(eq(EMP_ID), eq("MacBook Pro"))).thenReturn(sampleDto());

        mockMvc.perform(post("/api/employees/{id}/hardware", EMP_ID)
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "MacBook Pro"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(HARDWARE_ID.toString()))
                .andExpect(jsonPath("$.data.name").value("MacBook Pro"));
    }

    @Test
    void addHardware_blankName_returns400() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);

        mockMvc.perform(post("/api/employees/{id}/hardware", EMP_ID)
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", ""))))
                .andExpect(status().isUnprocessableEntity());

        verify(hardwareService, never()).addHardware(any(), any());
    }

    @Test
    void addHardware_nameTooLong_returns400() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);
        String tooLong = "A".repeat(256);

        mockMvc.perform(post("/api/employees/{id}/hardware", EMP_ID)
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", tooLong))))
                .andExpect(status().isUnprocessableEntity());

        verify(hardwareService, never()).addHardware(any(), any());
    }

    @Test
    void addHardware_asEmployee_returns403() throws Exception {
        Employee emp = buildEmployee(Employee.Role.EMPLOYEE);

        mockMvc.perform(post("/api/employees/{id}/hardware", EMP_ID)
                        .with(authentication(auth(emp)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "MacBook Pro"))))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /api/employees/{id}/hardware/{hardwareId} ──────────────────────

    @Test
    void removeHardware_asAdmin_returns200() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);
        doNothing().when(hardwareService).removeHardware(EMP_ID, HARDWARE_ID);

        mockMvc.perform(delete("/api/employees/{id}/hardware/{hid}", EMP_ID, HARDWARE_ID)
                        .with(authentication(auth(admin)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void removeHardware_notFound_returns404() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);
        doThrow(new AppException(ErrorCode.NOT_FOUND, "Hardware item not found", HttpStatus.NOT_FOUND))
                .when(hardwareService).removeHardware(EMP_ID, HARDWARE_ID);

        mockMvc.perform(delete("/api/employees/{id}/hardware/{hid}", EMP_ID, HARDWARE_ID)
                        .with(authentication(auth(admin)))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeHardware_asEmployee_returns403() throws Exception {
        Employee emp = buildEmployee(Employee.Role.EMPLOYEE);

        mockMvc.perform(delete("/api/employees/{id}/hardware/{hid}", EMP_ID, HARDWARE_ID)
                        .with(authentication(auth(emp)))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
