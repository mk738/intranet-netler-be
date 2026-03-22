package com.company.intranet.employee;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.crm.AssignmentRepository;
import com.company.intranet.crm.CrmMapper;
import com.company.intranet.employee.dto.*;
import com.company.intranet.skill.SkillService;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

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

    @Mock EmployeeRepository        employeeRepository;
    @Mock EducationRepository       educationRepository;
    @Mock BankInfoRepository        bankInfoRepository;
    @Mock EmployeeContractRepository contractRepository;
    @Mock EmployeeCvRepository      cvRepository;
    @Mock EmployeeAvatarRepository  avatarRepository;
    @Mock EmployeeBenefitRepository benefitRepository;
    @Mock AssignmentRepository      assignmentRepository;
    @Mock CrmMapper                 crmMapper;
    @Mock SkillService              skillService;
    @Mock FirebaseAuth              firebaseAuth;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock EmployeeMapper            employeeMapper;

    @InjectMocks EmployeeService employeeService;

    // ── getAllEmployees ────────────────────────────────────────────────────────

    @Test
    void getAllEmployees_returnsMappedDtos() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Employee e1 = Employee.builder().id(id1).email("a@x.com").role(Employee.Role.EMPLOYEE).build();
        Employee e2 = Employee.builder().id(id2).email("b@x.com").role(Employee.Role.ADMIN).build();

        EmployeeDto dto1 = new EmployeeDto(id1, "a@x.com", Employee.Role.EMPLOYEE, true, null, List.of(), null);
        EmployeeDto dto2 = new EmployeeDto(id2, "b@x.com", Employee.Role.ADMIN,    true, null, List.of(), null);

        when(employeeRepository.findAllActiveWithProfile()).thenReturn(List.of(e1, e2));
        when(employeeMapper.toDto(e1)).thenReturn(dto1);
        when(employeeMapper.toDto(e2)).thenReturn(dto2);

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertThat(result).containsExactly(dto1, dto2);
    }

    // ── getEmployeeById ───────────────────────────────────────────────────────

    @Test
    void getEmployeeById_unknownId_throwsEmployeeNotFound() {
        UUID unknown = UUID.randomUUID();
        when(employeeRepository.findById(unknown)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(unknown))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException appEx = (AppException) ex;
                    assertThat(appEx.getCode()).isEqualTo(ErrorCode.EMPLOYEE_NOT_FOUND);
                    assertThat(appEx.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    // ── inviteEmployee ────────────────────────────────────────────────────────

    @Test
    void inviteEmployee_duplicateEmail_throwsEmailTaken() {
        InviteEmployeeRequest request = new InviteEmployeeRequest(
                "Erik", "Lindqvist", "erik@company.com",
                "Dev", Employee.Role.EMPLOYEE, LocalDate.of(2024, 1, 1));

        Employee admin = Employee.builder().id(UUID.randomUUID()).email("admin@company.com")
                .role(Employee.Role.ADMIN).build();

        when(employeeRepository.findByEmail("erik@company.com"))
                .thenReturn(Optional.of(Employee.builder().build()));

        assertThatThrownBy(() -> employeeService.inviteEmployee(request, admin))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException appEx = (AppException) ex;
                    assertThat(appEx.getCode()).isEqualTo(ErrorCode.EMPLOYEE_EMAIL_TAKEN);
                    assertThat(appEx.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                });

        verifyNoInteractions(firebaseAuth);
    }

    // ── updateMyBank ──────────────────────────────────────────────────────────

    @Test
    void updateMyBank_invalidClearing_throwsBankInvalidClearing() {
        Employee me = Employee.builder().id(UUID.randomUUID()).build();
        UpdateBankRequest request = new UpdateBankRequest("Swedbank", "12345678", "INVALID");

        assertThatThrownBy(() -> employeeService.updateMyBank(request, me))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getCode())
                        .isEqualTo(ErrorCode.BANK_INVALID_CLEARING));
    }

    @Test
    void updateMyBank_invalidAccount_throwsBankInvalidAccount() {
        Employee me = Employee.builder().id(UUID.randomUUID()).build();
        UpdateBankRequest request = new UpdateBankRequest("Swedbank", "123", "8327-9");

        assertThatThrownBy(() -> employeeService.updateMyBank(request, me))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getCode())
                        .isEqualTo(ErrorCode.BANK_INVALID_ACCOUNT));
    }

    @Test
    void updateMyBank_validDetails_savesSuccessfully() {
        Employee me = Employee.builder().id(UUID.randomUUID()).build();
        UpdateBankRequest request = new UpdateBankRequest("Swedbank", "12345678", "8327-9");

        when(bankInfoRepository.findByEmployee(me)).thenReturn(Optional.empty());
        when(bankInfoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        employeeService.updateMyBank(request, me);

        verify(bankInfoRepository).save(any());
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
