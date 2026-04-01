package com.company.intranet.employee;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeDeactivationSchedulerTest {

    @Mock EmployeeRepository employeeRepository;
    @Mock FirebaseAuth       firebaseAuth;

    @InjectMocks EmployeeDeactivationScheduler scheduler;

    private Employee activeEmployee(String firstName, String lastName) {
        EmployeeProfile profile = EmployeeProfile.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
        Employee emp = Employee.builder()
                .id(UUID.randomUUID())
                .firebaseUid("uid-" + UUID.randomUUID())
                .email(firstName.toLowerCase() + "@company.com")
                .role(Employee.Role.EMPLOYEE)
                .isActive(true)
                .employmentEndDate(LocalDate.now().minusDays(1))
                .build();
        emp.setProfile(profile);
        return emp;
    }

    @Test
    void scheduler_noExpiredEmployees_doesNothing() {
        when(employeeRepository.findByEmploymentEndDateBeforeAndIsActiveTrue(any()))
                .thenReturn(List.of());

        scheduler.deactivateExpiredEmployees();

        verify(employeeRepository, never()).save(any());
        verifyNoInteractions(firebaseAuth);
    }

    @Test
    void scheduler_expiredEmployee_deactivatesAndDisablesFirebase() throws Exception {
        Employee emp = activeEmployee("Erik", "Lindqvist");

        when(employeeRepository.findByEmploymentEndDateBeforeAndIsActiveTrue(any()))
                .thenReturn(List.of(emp));
        when(employeeRepository.save(any())).thenReturn(emp);

        scheduler.deactivateExpiredEmployees();

        assertThat(emp.isActive()).isFalse();
        assertThat(emp.getEmploymentEndDate()).isNull();
        verify(employeeRepository).save(emp);
        verify(firebaseAuth).updateUser(any(UserRecord.UpdateRequest.class));
    }

    @Test
    void scheduler_firebaseException_logsWarnAndContinues() throws Exception {
        Employee emp = activeEmployee("Erik", "Lindqvist");

        when(employeeRepository.findByEmploymentEndDateBeforeAndIsActiveTrue(any()))
                .thenReturn(List.of(emp));
        when(employeeRepository.save(any())).thenReturn(emp);
        doThrow(mock(FirebaseAuthException.class))
                .when(firebaseAuth).updateUser(any(UserRecord.UpdateRequest.class));

        scheduler.deactivateExpiredEmployees();

        // Employee is still deactivated in DB even when Firebase call fails
        assertThat(emp.isActive()).isFalse();
        verify(employeeRepository).save(emp);
    }
}
