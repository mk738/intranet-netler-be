package com.company.intranet.vacation;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeProfile;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.notification.events.VacationRequestedEvent;
import com.company.intranet.notification.events.VacationReviewedEvent;
import com.company.intranet.vacation.dto.ReviewVacationRequest;
import com.company.intranet.vacation.dto.SubmitVacationRequest;
import com.company.intranet.vacation.dto.VacationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class VacationServiceTest {

    @Mock VacationRepository        vacationRepository;
    @Mock EmployeeRepository        employeeRepository;
    @Mock VacationMapper            vacationMapper;
    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks VacationService vacationService;

    private Employee employee(UUID id, String email) {
        EmployeeProfile profile = EmployeeProfile.builder()
                .firstName("Erik").lastName("Lindqvist").build();
        Employee emp = Employee.builder()
                .id(id).email(email).role(Employee.Role.EMPLOYEE).build();
        emp.setProfile(profile);
        profile.setEmployee(emp);
        return emp;
    }

    // ── submitVacation ────────────────────────────────────────────────────────

    @Test
    void submitVacation_pastStartDate_throwsVacationPastDate() {
        Employee me = employee(UUID.randomUUID(), "e@x.com");
        SubmitVacationRequest req = new SubmitVacationRequest(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(3));

        assertThatThrownBy(() -> vacationService.submitVacation(req, me))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getCode())
                        .isEqualTo(ErrorCode.VACATION_PAST_DATE));
    }

    @Test
    void submitVacation_endBeforeStart_throwsVacationDateInvalid() {
        Employee me = employee(UUID.randomUUID(), "e@x.com");
        LocalDate start = LocalDate.now().plusDays(5);
        SubmitVacationRequest req = new SubmitVacationRequest(start, start.minusDays(1));

        assertThatThrownBy(() -> vacationService.submitVacation(req, me))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getCode())
                        .isEqualTo(ErrorCode.VACATION_DATE_INVALID));
    }

    @Test
    void submitVacation_overlappingRequest_throwsVacationOverlap() {
        Employee me = employee(UUID.randomUUID(), "e@x.com");
        LocalDate start = LocalDate.now().plusDays(5);
        SubmitVacationRequest req = new SubmitVacationRequest(start, start.plusDays(4));

        when(vacationRepository
                .existsByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusNot(
                        eq(me), any(), any(), eq(VacationRequest.VacationStatus.REJECTED)))
                .thenReturn(true);

        assertThatThrownBy(() -> vacationService.submitVacation(req, me))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException appEx = (AppException) ex;
                    assertThat(appEx.getCode()).isEqualTo(ErrorCode.VACATION_OVERLAP);
                    assertThat(appEx.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                });
    }

    @Test
    void submitVacation_insufficientDays_throwsVacationInsufficientDays() {
        Employee me = employee(UUID.randomUUID(), "e@x.com");
        LocalDate start = LocalDate.now().plusDays(5);
        // Request 5 business days when 23 already used (25 allowance)
        SubmitVacationRequest req = new SubmitVacationRequest(start, start.plusDays(6));

        when(vacationRepository
                .existsByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusNot(
                        any(), any(), any(), any()))
                .thenReturn(false);
        when(vacationRepository.sumApprovedDaysForYear(eq(me), anyInt())).thenReturn(23);

        assertThatThrownBy(() -> vacationService.submitVacation(req, me))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getCode())
                        .isEqualTo(ErrorCode.VACATION_INSUFFICIENT_DAYS));
    }

    @Test
    void submitVacation_success_publishesVacationRequestedEvent() {
        UUID id = UUID.randomUUID();
        Employee me = employee(id, "erik@company.com");
        LocalDate start = LocalDate.now().plusDays(7);
        LocalDate end   = start.plusDays(4);
        SubmitVacationRequest req = new SubmitVacationRequest(start, end);

        when(vacationRepository
                .existsByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusNot(
                        any(), any(), any(), any()))
                .thenReturn(false);
        when(vacationRepository.sumApprovedDaysForYear(eq(me), anyInt())).thenReturn(0);

        VacationRequest saved = VacationRequest.builder()
                .id(UUID.randomUUID()).employee(me)
                .startDate(start).endDate(end).daysCount(5)
                .status(VacationRequest.VacationStatus.PENDING).build();

        when(vacationRepository.save(any())).thenReturn(saved);
        when(employeeRepository.findAllAdminEmails())
                .thenReturn(List.of("admin@company.com"));
        when(vacationMapper.toDto(saved))
                .thenReturn(new VacationDto(saved.getId(), id, "Erik Lindqvist", "EL",
                        start, end, 5, "PENDING", null, null, null));

        vacationService.submitVacation(req, me);

        ArgumentCaptor<VacationRequestedEvent> captor =
                ArgumentCaptor.forClass(VacationRequestedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().employeeEmail()).isEqualTo("erik@company.com");
        assertThat(captor.getValue().adminEmails()).containsExactly("admin@company.com");
    }

    // ── cancelVacation ────────────────────────────────────────────────────────

    @Test
    void cancelVacation_notOwned_throwsAppException() {
        Employee owner = employee(UUID.randomUUID(), "owner@x.com");
        Employee other = employee(UUID.randomUUID(), "other@x.com");

        VacationRequest vacation = VacationRequest.builder()
                .id(UUID.randomUUID()).employee(owner)
                .status(VacationRequest.VacationStatus.PENDING).build();

        when(vacationRepository.findById(vacation.getId()))
                .thenReturn(Optional.of(vacation));

        assertThatThrownBy(() -> vacationService.cancelVacation(vacation.getId(), other))
                .isInstanceOf(AppException.class);
    }

    @Test
    void cancelVacation_notPending_throwsAppException() {
        UUID id = UUID.randomUUID();
        Employee me = employee(id, "e@x.com");

        VacationRequest vacation = VacationRequest.builder()
                .id(UUID.randomUUID()).employee(me)
                .status(VacationRequest.VacationStatus.APPROVED).build();

        when(vacationRepository.findById(vacation.getId()))
                .thenReturn(Optional.of(vacation));

        assertThatThrownBy(() -> vacationService.cancelVacation(vacation.getId(), me))
                .isInstanceOf(AppException.class);
    }

    // ── reviewVacation ────────────────────────────────────────────────────────

    @Test
    void reviewVacation_notPending_throwsAppException() {
        UUID vacId = UUID.randomUUID();
        Employee me = employee(UUID.randomUUID(), "e@x.com");

        VacationRequest vacation = VacationRequest.builder()
                .id(vacId).employee(me)
                .status(VacationRequest.VacationStatus.APPROVED).build();

        when(vacationRepository.findById(vacId)).thenReturn(Optional.of(vacation));

        assertThatThrownBy(() -> vacationService.reviewVacation(
                vacId, new ReviewVacationRequest(true),
                employee(UUID.randomUUID(), "admin@x.com")))
                .isInstanceOf(AppException.class);
    }

    @Test
    void reviewVacation_success_publishesVacationReviewedEvent() {
        UUID vacId    = UUID.randomUUID();
        Employee emp   = employee(UUID.randomUUID(), "emp@company.com");
        Employee admin = employee(UUID.randomUUID(), "admin@company.com");

        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end   = start.plusDays(4);

        VacationRequest vacation = VacationRequest.builder()
                .id(vacId).employee(emp)
                .startDate(start).endDate(end).daysCount(5)
                .status(VacationRequest.VacationStatus.PENDING).build();

        when(vacationRepository.findById(vacId)).thenReturn(Optional.of(vacation));
        when(vacationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(vacationMapper.toDto(any())).thenReturn(
                new VacationDto(vacId, emp.getId(), "Erik Lindqvist", "EL",
                        start, end, 5, "APPROVED", "Admin User", null, null));

        vacationService.reviewVacation(vacId, new ReviewVacationRequest(true), admin);

        ArgumentCaptor<VacationReviewedEvent> captor =
                ArgumentCaptor.forClass(VacationReviewedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().employeeEmail()).isEqualTo("emp@company.com");
        assertThat(captor.getValue().status()).isEqualTo("approved");
    }

    // ── calculateBusinessDays ─────────────────────────────────────────────────

    @Test
    void calculateBusinessDays_mondayToFriday_returns5() {
        LocalDate monday = LocalDate.of(2026, 3, 16);
        LocalDate friday = LocalDate.of(2026, 3, 20);
        assertThat(vacationService.calculateBusinessDays(monday, friday)).isEqualTo(5);
    }

    @Test
    void calculateBusinessDays_fridayToMonday_returns2() {
        LocalDate friday = LocalDate.of(2026, 3, 20);
        LocalDate monday = LocalDate.of(2026, 3, 23);
        assertThat(vacationService.calculateBusinessDays(friday, monday)).isEqualTo(2);
    }
}
