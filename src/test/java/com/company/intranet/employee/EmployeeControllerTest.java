package com.company.intranet.employee;

import com.company.intranet.employee.dto.*;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired MockMvc       mockMvc;
    @Autowired ObjectMapper  objectMapper;

    @MockBean EmployeeService    employeeService;
    @MockBean FirebaseAuth       firebaseAuth;                    // needed by FirebaseTokenFilter
    @MockBean EmployeeRepository employeeRepository;              // needed by FirebaseTokenFilter
    @MockBean JpaMetamodelMappingContext jpaMetamodelMappingContext; // needed by @EnableJpaAuditing

    // ── helpers ───────────────────────────────────────────────────────────────

    private static final UUID EMP_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private Employee buildEmployee(Employee.Role role) {
        return Employee.builder()
                .id(EMP_ID)
                .email("erik@company.com")
                .role(role)
                .build();
    }

    private Authentication auth(Employee employee) {
        String authority = "ROLE_" + employee.getRole().name();
        return new UsernamePasswordAuthenticationToken(
                employee, null,
                List.of(new SimpleGrantedAuthority(authority)));
    }

    private EmployeeDto sampleDto(Employee employee) {
        return new EmployeeDto(
                employee.getId(), employee.getEmail(), employee.getRole(), true,
                new EmployeeProfileDto("Erik", "Lindqvist", "Backend Dev",
                        null, null, null, LocalDate.of(2023, 3, 1), null));
    }

    private EmployeeDetailDto sampleDetailDto(Employee employee) {
        return new EmployeeDetailDto(
                employee.getId(), employee.getEmail(), employee.getRole().name(),
                true, "2023-01-01T00:00:00Z",
                new EmployeeProfileDto("Erik", "Lindqvist", "Backend Dev",
                        null, null, null, LocalDate.of(2023, 3, 1), null),
                null,
                List.of());
    }

    // ── GET /api/employees ────────────────────────────────────────────────────

    @Test
    void getAllEmployees_asAdmin_returns200() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);
        when(employeeService.getAllEmployees()).thenReturn(List.of(sampleDto(admin)));

        mockMvc.perform(get("/api/employees")
                        .with(authentication(auth(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].email").value("erik@company.com"));
    }

    @Test
    void getAllEmployees_asEmployee_returns403() throws Exception {
        Employee emp = buildEmployee(Employee.Role.EMPLOYEE);

        mockMvc.perform(get("/api/employees")
                        .with(authentication(auth(emp))))
                .andExpect(status().isForbidden());
    }

    // ── GET /api/employees/me ─────────────────────────────────────────────────

    @Test
    void getMyProfile_authenticated_returns200WithCorrectShape() throws Exception {
        Employee emp = buildEmployee(Employee.Role.EMPLOYEE);
        when(employeeService.getMyProfile(emp)).thenReturn(sampleDetailDto(emp));

        mockMvc.perform(get("/api/employees/me")
                        .with(authentication(auth(emp))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(EMP_ID.toString()))
                .andExpect(jsonPath("$.data.email").value("erik@company.com"))
                .andExpect(jsonPath("$.data.role").value("EMPLOYEE"))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.profile.firstName").value("Erik"))
                .andExpect(jsonPath("$.data.education").isArray());
    }

    // ── POST /api/employees ───────────────────────────────────────────────────

    @Test
    void inviteEmployee_asAdmin_returns201() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);
        InviteEmployeeRequest request = new InviteEmployeeRequest(
                "New", "Hire", "new@company.com",
                "Developer", Employee.Role.EMPLOYEE, LocalDate.of(2026, 4, 1));

        UUID newId = UUID.randomUUID();
        EmployeeDto created = new EmployeeDto(newId, "new@company.com",
                Employee.Role.EMPLOYEE, true, null);

        when(employeeService.inviteEmployee(any(InviteEmployeeRequest.class)))
                .thenReturn(created);

        mockMvc.perform(post("/api/employees")
                        .with(authentication(auth(admin)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("new@company.com"));
    }

    // ── POST /api/employees/me/education ──────────────────────────────────────

    @Test
    void addEducation_returns201() throws Exception {
        Employee emp = buildEmployee(Employee.Role.EMPLOYEE);
        AddEducationRequest request = new AddEducationRequest(
                "KTH", "MSc", "Computer Science", 2018, 2020, null);

        UUID eduId = UUID.randomUUID();
        EducationDto dto = new EducationDto(
                eduId, "KTH", "MSc", "Computer Science", 2018, 2020, null);

        when(employeeService.addEducation(any(AddEducationRequest.class), eq(emp)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/employees/me/education")
                        .with(authentication(auth(emp)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.institution").value("KTH"))
                .andExpect(jsonPath("$.data.degree").value("MSc"));
    }

    // ── DELETE /api/employees/me/education/{id} ───────────────────────────────

    @Test
    void deleteEducation_returns204() throws Exception {
        Employee emp = buildEmployee(Employee.Role.EMPLOYEE);
        UUID eduId = UUID.randomUUID();

        doNothing().when(employeeService).deleteEducation(eq(eduId), eq(emp));

        mockMvc.perform(delete("/api/employees/me/education/" + eduId)
                        .with(authentication(auth(emp)))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // ── GET /api/employees/{id} ───────────────────────────────────────────────

    @Test
    void getEmployeeById_asAdmin_returnsMaskedBankInfo() throws Exception {
        Employee admin = buildEmployee(Employee.Role.ADMIN);
        UUID targetId = UUID.randomUUID();

        EmployeeDetailDto detail = new EmployeeDetailDto(
                targetId, "sara@company.com", "EMPLOYEE", true, "2023-06-01T00:00:00Z",
                new EmployeeProfileDto("Sara", "Berg", "Backend Dev",
                        null, null, null, LocalDate.of(2023, 6, 1), null),
                new BankInfoDto("Swedbank", "••••7890", "8102"),
                List.of());

        when(employeeService.getEmployeeById(targetId)).thenReturn(detail);

        mockMvc.perform(get("/api/employees/" + targetId)
                        .with(authentication(auth(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bankInfo.accountNumber").value("••••7890"))
                .andExpect(jsonPath("$.data.bankInfo.clearingNumber").value("8102"));
    }
}
