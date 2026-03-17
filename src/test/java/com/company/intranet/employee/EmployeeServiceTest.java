package com.company.intranet.employee;

import com.company.intranet.common.exception.BadRequestException;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.dto.*;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock EmployeeRepository       employeeRepository;
    @Mock EducationRepository      educationRepository;
    @Mock BankInfoRepository       bankInfoRepository;
    @Mock FirebaseAuth             firebaseAuth;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock EmployeeMapper           employeeMapper;

    @InjectMocks EmployeeService employeeService;

    // ── getAllEmployees ────────────────────────────────────────────────────────

    @Test
    void getAllEmployees_returnsMappedDtos() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Employee e1 = Employee.builder().id(id1).email("a@x.com").role(Employee.Role.EMPLOYEE).build();
        Employee e2 = Employee.builder().id(id2).email("b@x.com").role(Employee.Role.ADMIN).build();

        EmployeeDto dto1 = new EmployeeDto(id1, "a@x.com", Employee.Role.EMPLOYEE, true, null);
        EmployeeDto dto2 = new EmployeeDto(id2, "b@x.com", Employee.Role.ADMIN,    true, null);

        when(employeeRepository.findAllActiveWithProfile()).thenReturn(List.of(e1, e2));
        when(employeeMapper.toDto(e1)).thenReturn(dto1);
        when(employeeMapper.toDto(e2)).thenReturn(dto2);

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertThat(result).containsExactly(dto1, dto2);
    }

    // ── getEmployeeById ───────────────────────────────────────────────────────

    @Test
    void getEmployeeById_unknownId_throwsResourceNotFoundException() {
        UUID unknown = UUID.randomUUID();
        when(employeeRepository.findById(unknown)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(unknown))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found");
    }

    // ── inviteEmployee ────────────────────────────────────────────────────────

    @Test
    void inviteEmployee_duplicateEmail_throwsBadRequestException() {
        InviteEmployeeRequest request = new InviteEmployeeRequest(
                "Erik", "Lindqvist", "erik@company.com",
                "Dev", Employee.Role.EMPLOYEE, LocalDate.of(2024, 1, 1));

        when(employeeRepository.findByEmail("erik@company.com"))
                .thenReturn(Optional.of(Employee.builder().build()));

        assertThatThrownBy(() -> employeeService.inviteEmployee(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email already registered");

        verifyNoInteractions(firebaseAuth);
    }

    // ── updateMyProfile ───────────────────────────────────────────────────────

    @Test
    void updateMyProfile_doesNotUpdateStartDate() {
        UUID id = UUID.randomUUID();
        LocalDate originalStartDate = LocalDate.of(2023, 1, 1);

        EmployeeProfile profile = EmployeeProfile.builder()
                .firstName("Erik")
                .lastName("Lindqvist")
                .startDate(originalStartDate)
                .build();

        Employee employee = Employee.builder()
                .id(id)
                .email("erik@company.com")
                .role(Employee.Role.EMPLOYEE)
                .build();
        employee.setProfile(profile);
        profile.setEmployee(employee);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(
                new EmployeeDto(id, "erik@company.com", Employee.Role.EMPLOYEE, true, null));

        UpdateProfileRequest request = new UpdateProfileRequest(
                "Erik", "Updated", "Senior Dev",
                "+46700000000", null, null, null,
                LocalDate.of(2025, 6, 1)   // attacker tries to change startDate
        );

        employeeService.updateMyProfile(request, Employee.builder().id(id).build());

        // startDate must remain unchanged
        assertThat(profile.getStartDate()).isEqualTo(originalStartDate);
        // other fields were updated
        assertThat(profile.getLastName()).isEqualTo("Updated");
    }

    // ── deleteEducation ───────────────────────────────────────────────────────

    @Test
    void deleteEducation_notFound_throwsResourceNotFoundException() {
        UUID educationId = UUID.randomUUID();
        Employee me = Employee.builder().id(UUID.randomUUID()).build();

        when(educationRepository.findById(educationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEducation(educationId, me))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Education entry not found");

        verify(educationRepository, never()).delete(any());
    }
}
