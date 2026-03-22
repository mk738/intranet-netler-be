package com.company.intranet.auth;

import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.employee.dto.EmployeeDto;
import com.company.intranet.employee.dto.EmployeeProfileDto;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    // Required by FirebaseTokenFilter (auto-detected as a web-layer @Component)
    @MockBean FirebaseAuth firebaseAuth;
    @MockBean EmployeeRepository employeeRepository;
    // Required because @EnableJpaAuditing on IntranetApplication is visible to @WebMvcTest
    @MockBean JpaMetamodelMappingContext jpaMetamodelMappingContext;

    // ── helpers ────────────────────────────────────────────────────────────────

    private static final UUID EMPLOYEE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private Employee buildEmployee() {
        return Employee.builder()
                .id(EMPLOYEE_ID)
                .email("erik@company.com")
                .role(Employee.Role.EMPLOYEE)
                .build();
    }

    private EmployeeDto buildDto() {
        return new EmployeeDto(
                EMPLOYEE_ID,
                "erik@company.com",
                Employee.Role.EMPLOYEE,
                true,
                Collections.emptyList(),
                new EmployeeProfileDto(
                        "Erik", "Lindqvist", "Senior Backend Dev",
                        null, null, null, null,
                        LocalDate.of(2023, 3, 1), null
                )
        );
    }

    // ── tests ──────────────────────────────────────────────────────────────────

    @Test
    void me_withValidAuthentication_returnsOkWithEmployeeDto() throws Exception {
        Employee employee = buildEmployee();
        EmployeeDto dto = buildDto();

        when(authService.getCurrentUser(employee)).thenReturn(dto);

        var auth = new UsernamePasswordAuthenticationToken(
                employee, null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );

        mockMvc.perform(post("/api/auth/me")
                        .with(authentication(auth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").doesNotExist())
                .andExpect(jsonPath("$.data.id").value(EMPLOYEE_ID.toString()))
                .andExpect(jsonPath("$.data.email").value("erik@company.com"))
                .andExpect(jsonPath("$.data.role").value("EMPLOYEE"))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.profile.firstName").value("Erik"))
                .andExpect(jsonPath("$.data.profile.lastName").value("Lindqvist"))
                .andExpect(jsonPath("$.data.profile.jobTitle").value("Senior Backend Dev"))
                .andExpect(jsonPath("$.data.profile.startDate").value("2023-03-01"))
                .andExpect(jsonPath("$.data.profile.phone").doesNotExist())
                .andExpect(jsonPath("$.data.profile.address").doesNotExist())
                .andExpect(jsonPath("$.data.profile.avatarUrl").doesNotExist());
    }

    @Test
    void me_withNoAuthentication_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
