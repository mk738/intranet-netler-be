package com.company.intranet.auth;

import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeMapper;
import com.company.intranet.employee.dto.EmployeeDto;
import com.company.intranet.employee.dto.EmployeeProfileDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    EmployeeMapper employeeMapper;

    @InjectMocks
    AuthService authService;

    @Test
    void getCurrentUser_delegatesToMapper_andReturnsDto() {
        Employee employee = Employee.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .email("erik@company.com")
                .role(Employee.Role.EMPLOYEE)
                .build();

        EmployeeDto expected = new EmployeeDto(
                employee.getId(),
                "erik@company.com",
                Employee.Role.EMPLOYEE,
                true,
                null,
                Collections.emptyList(),
                new EmployeeProfileDto(
                        "Erik", "Lindqvist", "Senior Backend Dev",
                        null, null, null, null,
                        LocalDate.of(2023, 3, 1), null
                )
        );

        when(employeeMapper.toDto(employee)).thenReturn(expected);

        EmployeeDto result = authService.getCurrentUser(employee);

        assertThat(result).isEqualTo(expected);
        assertThat(result.email()).isEqualTo("erik@company.com");
        assertThat(result.role()).isEqualTo(Employee.Role.EMPLOYEE);
        assertThat(result.profile().firstName()).isEqualTo("Erik");
    }
}
